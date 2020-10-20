package io.renren.modules.etf.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.etf.FundDown;
import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.danjuan.worth.DanJuanWorthInfo;
import io.renren.modules.etf.danjuan.worth.Item;
import io.renren.modules.etf.entity.EtfFundWorthEntity;
import io.renren.modules.etf.service.EtfFundWorthService;
import io.renren.modules.etf.service.EtfInvestmentPlanService;
import io.renren.modules.etf.service.FundSituationDay;
import io.renren.modules.etf.service.impl.DanJuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @date 2020-09-23 15:10:56
 */
@RestController
@RequestMapping("generator/etffundworth")
public class EtfFundWorthController {
    @Autowired
    private EtfFundWorthService etfFundWorthService;


    @Autowired
    private DanJuanService danJuanService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam String fundNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("fund_no", fundNo);
        PageUtils page = etfFundWorthService.queryPage(params);

        return R.ok().put("page", page);
    }

    @Autowired
    private EtfInvestmentPlanService etfInvestmentPlanService;

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam String fundNo) {
        etfFundWorthService.importWorth(fundNo);
        return R.ok();
    }

    @RequestMapping("/maximumStart")
    public JSONObject maximumStart(@RequestParam String fundNoString, @RequestParam(required = false) Integer dayNum) {
        if (dayNum == null) {
            dayNum = 90;
        }
        JSONObject result = new JSONObject();
        List<String> fundList = Arrays.asList(fundNoString.split(","));
        Map<String, String> fundMap = new LinkedHashMap<>();
        List<FundDown> downList = new ArrayList<>();
        for (String fundNo : fundList) {
            List<EtfFundWorthEntity> worthEntityList = etfFundWorthService.getWorthByDanJuan(fundNo, null, dayNum);
            worthEntityList = worthEntityList.stream().filter(u -> u.getPercentage() != null).sorted(Comparator.comparing(EtfFundWorthEntity::getFundDate)).collect(Collectors.toList());
            double asDouble = worthEntityList.stream().mapToDouble(u -> u.getWorth().doubleValue()).max().getAsDouble();
            List<EtfFundWorthEntity> collect = worthEntityList.stream().filter(u -> u.getWorth().doubleValue() == asDouble).sorted(Comparator.comparing(EtfFundWorthEntity::getFundDate)).collect(Collectors.toList());
            EtfFundWorthEntity maxWorth = collect.get(collect.size() - 1);
            FundModel sourceFund = etfInvestmentPlanService.getFundInfo(fundNo, "");
            BigDecimal divide = maxWorth.getWorth().subtract(sourceFund.getGsz()).divide(maxWorth.getWorth(), 4, BigDecimal.ROUND_HALF_UP);
            divide = divide.multiply(new BigDecimal(100));
            String ss = FundSituationDay.addForNum(30, fundNo + "  " + sourceFund.getName());
            FundDown down = new FundDown().setDesc("最近回落：" + divide + "%,      最高点时间:" + DateUtils.format(maxWorth.getFundDate())).setDown(divide).setFundName(sourceFund.getName()).setFundNo(fundNo);
            downList.add(down);
        }
        downList = downList.stream().sorted(Comparator.comparing(FundDown::getDown).reversed()).collect(Collectors.toList());
        for (FundDown down : downList) {
            fundMap.put(FundSituationDay.addForNum(30, down.getFundNo() + "  " + down.getFundName()), down.getDesc());
        }
        return result.fluentPut("基金回落详情", fundMap);
    }

    @RequestMapping("/maximumDrawdown")
    public JSONObject maximumDrawdown(@RequestParam String fundNo) {
        BigDecimal highSum = new BigDecimal(0);
        List<EtfFundWorthEntity> worthEntityList = etfFundWorthService.importWorth(fundNo);
        worthEntityList = worthEntityList.stream().filter(u -> u.getPercentage() != null).sorted(Comparator.comparing(EtfFundWorthEntity::getFundDate)).collect(Collectors.toList());
        BigDecimal maximum = new BigDecimal(0);

        // 历史最高点
        double maxHeight = 0;
        // 当前点数
        double currentSum = 0;

        for (EtfFundWorthEntity wo : worthEntityList) {
            currentSum = currentSum + wo.getPercentage().doubleValue();
            if (currentSum > maxHeight) {
                maxHeight = currentSum;
            }

            highSum = highSum.add(wo.getPercentage());
            if (highSum.doubleValue() > 0) {
                highSum = new BigDecimal(0);
            }
            if (maximum.doubleValue() > highSum.doubleValue()) {
                maximum = new BigDecimal(highSum.doubleValue());
            }
        }

        return new JSONObject().fluentPut("历史最大回撤", maximum.toString()).fluentPut("现在已经回撤", maxHeight - currentSum);
    }

}
