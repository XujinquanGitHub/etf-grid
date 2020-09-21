package io.renren.modules.etf.controller;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.OperationModel;
import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;
import io.renren.modules.etf.service.EtfGridService;
import io.renren.modules.etf.service.EtfInvestmentPlanService;
import io.swagger.models.Operation;
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
@RequestMapping("generator/etfgrid")
public class EtfGridController {
    @Autowired
    private EtfGridService etfGridService;

    @Autowired
    private EtfInvestmentPlanService etfInvestmentPlanService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = etfGridService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        EtfGridEntity etfGrid = etfGridService.getById(id);

        return R.ok().put("etfGrid", etfGrid);
    }

    /**
     * 添加一份买入网格
     */
    @RequestMapping("/save")
    public R save(@RequestBody EtfGridEntity etfGrid) {
        etfGrid.setNum(etfGrid.getBuyAmount().divide(etfGrid.getBuyPrice(), 2, BigDecimal.ROUND_HALF_UP));
        etfGridService.save(etfGrid);
        return R.ok();
    }

    /**
     * 卖出一份
     */
    @RequestMapping("/update")
    public R update(@RequestBody EtfGridEntity etfGrid) {
        etfGridService.updateById(etfGrid);
        return R.ok();
    }

    @RequestMapping("/selectPrice")
    public List<OperationModel> selectPrice() {
        List<OperationModel> updateList = new ArrayList<>();
        List<EtfInvestmentPlanEntity> list = etfInvestmentPlanService.list();
        List<EtfGridEntity> gridEntityList = etfGridService.list();
        for (EtfInvestmentPlanEntity plan : list) {
            List<EtfGridEntity> collect = gridEntityList.stream().filter(u -> plan.getId().equals(u.getPlanId()) && u.getStatus().equals(1)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                FundModel fundInfo = etfInvestmentPlanService.getFundInfo(plan.getFundNo());
                fundInfo.setGsz(new BigDecimal(0.9));
                for (int i = 0; i < collect.size(); i++) {
                    EtfGridEntity etfGridEntity = collect.get(i);
                    // 当前价格减去买入价格
                    BigDecimal subtract = fundInfo.getGsz().subtract(etfGridEntity.getBuyPrice());
                    BigDecimal divide = subtract.divide(etfGridEntity.getBuyPrice(), 6, BigDecimal.ROUND_HALF_UP);
                    System.out.println("涨幅：" + divide.multiply(new BigDecimal(100)).toString() + "%  赚:" + etfGridEntity.getBuyAmount().multiply(divide));
                    divide = divide.multiply(new BigDecimal(100));
                    if (divide.compareTo(plan.getRiseRange()) > 0) {
                        OperationModel entity = new OperationModel();
                        BeanUtils.copyProperties(etfGridEntity, entity);
                        entity.setName(fundInfo.getName());
                        entity.setFundNo(fundInfo.getFundcode());
                        entity.setSellPrice(fundInfo.getGsz());
                        BigDecimal sellAmount = fundInfo.getGsz().multiply(etfGridEntity.getNum());
                        entity.setSellAmount(sellAmount);
                        entity.setProfitRate(divide);
                        entity.setProfit(sellAmount.subtract(etfGridEntity.getBuyAmount()));
                        entity.setOperationString("卖出份额:" + entity.getNum() + "   盈利：" + entity.getProfit() + "  盈利率：" + entity.getProfitRate() + "%");
                        updateList.add(entity);
                        // 将这一网格设置为计划卖出
                        EtfGridEntity updateModel = new EtfGridEntity();
                        updateModel.setId(entity.getId());
                        updateModel.setStatus(2);
                    }
                }

                EtfGridEntity etfGridEntity = collect.stream().min(Comparator.comparingDouble(u -> new Double(u.getBuyPrice().toString()))).get();
                // 现在价格比买入时低
                if (fundInfo.getGsz().compareTo(etfGridEntity.getBuyPrice()) < 0) {
                    // 计算差价
                    BigDecimal subtract =etfGridEntity.getBuyPrice().subtract(fundInfo.getGsz());
                    // 计算亏损率
                    BigDecimal divide = subtract.divide(etfGridEntity.getBuyPrice(), 6, BigDecimal.ROUND_HALF_UP);
                    divide = divide.multiply(new BigDecimal(100));
                    if (divide.compareTo(plan.getFallRange()) > 0) {
                        OperationModel entity = new OperationModel();
                        entity.setName(fundInfo.getName());
                        entity.setFundNo(fundInfo.getFundcode());
                        // 计算买入金额，用最低点的亏损率除以计划的亏损率乘以单批金额
                        BigDecimal amount = divide.divide(plan.getFallRange(), 6, BigDecimal.ROUND_HALF_UP).multiply(plan.getSingleAmount());
                        entity.setBuyAmount(amount);
                        entity.setOperationString("买入金额:" + entity.getBuyAmount() );
                        updateList.add(entity);

                        // 将这一网格设置为计划买入
                        entity.setStatus(0);
                        // todo: 添加一个定时任务在晚上11点将基金净值和份额添加进去

                    }
                }


            }
        }
        return updateList;
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        etfGridService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
