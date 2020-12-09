package io.renren.modules.etf.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.renren.common.utils.DateUtils;
import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.OperationModel;
import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;
import io.renren.modules.etf.service.EtfGridService;
import io.renren.modules.etf.service.EtfInvestmentPlanService;
import io.renren.modules.etf.service.FundSituationDay;
import io.renren.modules.etf.service.impl.SwService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;


/**
 * @date 2020-09-18 17:06:22
 */
@RestController
@RequestMapping("generator/etfinvestmentplan")
public class EtfInvestmentPlanController {
    @Autowired
    private EtfInvestmentPlanService etfInvestmentPlanService;
    @Autowired
    private EtfGridService etfGridService;
    @Autowired
    private SwService swService;

    /**
     * 列表 净值
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = etfInvestmentPlanService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        EtfInvestmentPlanEntity etfInvestmentPlan = etfInvestmentPlanService.getById(id);

        return R.ok().put("etfInvestmentPlan", etfInvestmentPlan);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody EtfInvestmentPlanEntity etfInvestmentPlan) {
        FundModel fundModel = etfInvestmentPlanService.getFundInfo(etfInvestmentPlan.getFundNo(), etfInvestmentPlan.getIndexNo());
        if (StringUtils.isBlank(etfInvestmentPlan.getName())) {
            etfInvestmentPlan.setName(fundModel.getName() + "投资计划");
        }
        etfInvestmentPlan.setFundName(fundModel.getName());
        etfInvestmentPlan.setInitPrice(fundModel.getGsz());
        etfInvestmentPlan.setCurrentPrice(fundModel.getGsz());
        etfInvestmentPlan.setCreateDate(DateTime.now());
        etfInvestmentPlanService.save(etfInvestmentPlan);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:etfinvestmentplan:update")
    public R update(@RequestBody EtfInvestmentPlanEntity etfInvestmentPlan) {
        etfInvestmentPlanService.updateById(etfInvestmentPlan);

        return R.ok();
    }

    /**
     * 获取申万一级行业指数
     */
    @RequestMapping("/getAllIndustryIndexes")
    public String getAllIndustryIndexes() throws Exception {
        return JSON.toJSONString(swService.getAllIndustryIndexes());
    }

    @RequestMapping("/getMoneyMakeToday")
    public JSONObject getMoneyMakeToday(@RequestParam String accountList) throws Exception {
        List<EtfInvestmentPlanEntity> list = etfInvestmentPlanService.list();
        if (StringUtils.isNotBlank(accountList)) {
            List<String> strings = Arrays.asList(accountList.split(","));
            list = list.stream().filter(u -> strings.contains(u.getAccountDesc())).collect(Collectors.toList());
        }
        List<EtfGridEntity> gridEntityList = etfGridService.list();
        BigDecimal money = new BigDecimal(0);
        BigDecimal total = new BigDecimal(0);
        List<String> uncountedFunds = new ArrayList<>();

        TreeMap<BigDecimal, String> fundInfoList = new TreeMap<>();
        TreeMap<String, BigDecimal> accountMoney = new TreeMap<>();
        for (EtfInvestmentPlanEntity plan : list) {
            List<EtfGridEntity> collect = gridEntityList.stream().filter(u -> plan.getId().equals(u.getPlanId()) && (u.getStatus().equals(1) || (u.getStatus().equals(3) && DateUtils.format(u.getSellTime()).equals(DateUtils.format(new Date()))))).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                continue;
            }
            FundModel fundInfo = etfInvestmentPlanService.getFundInfo(plan.getFundNo(), plan.getIndexNo(), plan.getVenueNo());
            // 计算当开盘时所值金额
            double sum = collect.stream().filter(u -> u.getNum() != null).mapToDouble(u -> u.getNum().multiply(fundInfo.getDwjz()).doubleValue()).sum();
            total = total.add(new BigDecimal(sum));
            if (fundInfo.getGszzl() != null) {
                BigDecimal singleAmount = new BigDecimal(sum).multiply(fundInfo.getGszzl().divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_UP));
                FundSituationDay fundSituationDay = new FundSituationDay();
                fundSituationDay.setFundName(plan.getFundName()).setFundNo(plan.getFundNo()).setMakeMoney(singleAmount.setScale(2, BigDecimal.ROUND_HALF_UP)).setFundGains(fundInfo.getGszzl()).setFundAmount(new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP));
                fundInfoList.put(fundSituationDay.getMakeMoney(), fundSituationDay.toString());
                money = money.add(singleAmount);
                BigDecimal bigDecimal = accountMoney.get(plan.getAccountDesc());
                if (bigDecimal == null) {
                    accountMoney.put(plan.getAccountDesc(), fundSituationDay.getMakeMoney());
                } else {
                    accountMoney.put(plan.getAccountDesc(), bigDecimal.add(fundSituationDay.getMakeMoney()));
                }

            } else {
                uncountedFunds.add("基金名：" + plan.getFundName() + "----基金代码：" + plan.getFundNo() + "----金额：" + String.format("%.2f", sum));
            }
        }
        return new JSONObject().fluentPut("1今天赚钱", money.setScale(2, BigDecimal.ROUND_HALF_UP)).fluentPut("1总投资额", total.setScale(2, BigDecimal.ROUND_HALF_UP)).fluentPut("3帐号详情", accountMoney).fluentPut("3今天赚钱详情", fundInfoList.descendingMap().values()).fluentPut("2未统计基金", uncountedFunds);
    }

}
