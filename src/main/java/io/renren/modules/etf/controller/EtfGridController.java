package io.renren.modules.etf.controller;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.gson.JsonObject;
import io.renren.modules.etf.danjuan.DanJuanModel;
import io.renren.modules.etf.danjuan.DanJuanTradeList;
import io.renren.modules.etf.danjuan.Data;
import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.OperationModel;
import io.renren.modules.etf.danjuan.Item;
import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;
import io.renren.modules.etf.service.EtfGridService;
import io.renren.modules.etf.service.EtfInvestmentPlanService;
import io.renren.modules.etf.service.impl.DanJuanService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

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
    public com.alibaba.fastjson.JSONObject selectPrice(@RequestParam(required = false) String fundNoListString, @RequestParam(required = false) Integer type) {
        // type 为1查询买入操作  为0时查询卖出。为空查询所有
        List<OperationModel> updateList = new ArrayList<>();
        List<EtfInvestmentPlanEntity> list = etfInvestmentPlanService.list();
        List<EtfGridEntity> gridEntityList = etfGridService.list();
        if (StringUtils.isNotBlank(fundNoListString)) {
            List<String> fundNoList = Arrays.asList(fundNoListString.split(","));
            list = list.stream().filter(u -> fundNoList.contains(u.getFundNo())).collect(Collectors.toList());
        }
        BigDecimal totalBuyAmount = new BigDecimal(0);
        BigDecimal totalSellAmount = new BigDecimal(0);
        for (EtfInvestmentPlanEntity plan : list) {
            List<EtfGridEntity> collect = gridEntityList.stream().filter(u -> plan.getId().equals(u.getPlanId()) && u.getStatus().equals(1)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                FundModel fundInfo = etfInvestmentPlanService.getFundInfo(plan.getFundNo());
                if (type == null || type == 0) {
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
                            entity.setOperationString("卖出金额:" + entity.getSellAmount() + "   卖出份额：" + entity.getNum() + "   盈利：" + entity.getProfit() + "  盈利率：" + entity.getProfitRate() + "%");
                            totalSellAmount = totalSellAmount.add(entity.getSellAmount());
                            updateList.add(entity);
                            // 将这一网格设置为计划卖出
                            EtfGridEntity updateModel = new EtfGridEntity();
                            updateModel.setId(entity.getId());
                            updateModel.setStatus(2);
                        }
                    }
                }

                if (type == null || type == 1) {
                    EtfGridEntity etfGridEntity = collect.stream().min(Comparator.comparingDouble(u -> new Double(u.getBuyPrice().toString()))).get();
                    BigDecimal referencePrice = etfGridEntity.getBuyPrice();
                    if (plan.getBuyType() == 1) {
                        etfGridEntity = collect.stream().max(Comparator.comparingInt(u -> u.getId())).get();
                        referencePrice = etfGridEntity.getBuyPrice();
                    }
                    // 现在价格比买入时低
                    if (fundInfo.getGsz().compareTo(referencePrice) < 0) {
                        // 计算差价
                        BigDecimal subtract = referencePrice.subtract(fundInfo.getGsz());
                        // 计算亏损率
                        BigDecimal divide = subtract.divide(referencePrice, 6, BigDecimal.ROUND_HALF_UP);
                        divide = divide.multiply(new BigDecimal(100));
                        if (divide.compareTo(plan.getFallRange()) > 0) {
                            OperationModel entity = new OperationModel();
                            entity.setName(fundInfo.getName());
                            entity.setFundNo(fundInfo.getFundcode());
                            // 计算买入金额，用最低点的亏损率除以计划的亏损率乘以单批金额
                            BigDecimal amount = divide.divide(plan.getFallRange(), 6, BigDecimal.ROUND_HALF_UP).multiply(plan.getSingleAmount());
                            entity.setBuyAmount(amount);
                            entity.setOperationString("买入金额:" + entity.getBuyAmount());
                            // 将这一网格设置为计划买入 加上
//                            entity.setStatus(0);
                            entity.setPlanId(plan.getId());
                            entity.setBuyTime(new Date());
                            updateList.add(entity);

                            totalBuyAmount = totalBuyAmount.add(amount);

                            // 查询同一基金是否有未买入的网格，如果没有添加一网待卖入
                            List<EtfGridEntity> unBuyList = gridEntityList.stream().filter(u -> u.getStatus().equals(0) && fundInfo.getFundcode().equals(u.getFundNo())).collect(Collectors.toList());
                            if (CollectionUtils.isEmpty(unBuyList)) {
                                //删除自动插入，不然重复请求容易重复插入
//                            entity.insert();
                            }

                            // todo: 添加一个接口，定时任务在晚上11点将基金净值和份额添加进去

                        }
                    }
                }


            }
        }

        return new com.alibaba.fastjson.JSONObject().fluentPut("卖出金额", totalSellAmount).fluentPut("买入金额", totalBuyAmount).fluentPut("updateList", updateList);
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        etfGridService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @RequestMapping("/importDanJuanData")
    public String importDanJuanData(@RequestBody DanJuanModel danJuanModel) {
        Data data = danJuanModel.getData();
        if ("failed".equals(data.getStatus())) {
            return "买入失败的记录";
        }
        if (StringUtils.isBlank(data.getFd_code())) {
            return "基金代码为空";
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("fund_no", data.getFd_code());
        List<EtfInvestmentPlanEntity> etfInvestmentPlanEntities = etfInvestmentPlanService.queryList(params);
        EtfInvestmentPlanEntity planEntity = null;
        if (CollectionUtils.isEmpty(etfInvestmentPlanEntities)) {
            planEntity = new EtfInvestmentPlanEntity();
            planEntity.setCreateDate(DateUtil.date());
            planEntity.setFallRange(new BigDecimal(1));
            planEntity.setRiseRange(new BigDecimal(10));
            planEntity.setSingleAmount(new BigDecimal(1000));
            FundModel fundModel = etfInvestmentPlanService.getFundInfo(data.getFd_code());
            planEntity.setFundName(fundModel.getName());
            planEntity.setInitPrice(fundModel.getGsz());
            planEntity.setFundNo(data.getFd_code());
            planEntity.setName(fundModel.getName() + "投资计划");
            planEntity.setCurrentPrice(fundModel.getGsz());
            planEntity.setCreateDate(DateTime.now());
            etfInvestmentPlanService.save(planEntity);
        } else {
            planEntity = etfInvestmentPlanEntities.get(0);
        }

        params.clear();
        params.put("out_id", data.getOrder_id());
        // 已导入订单
        List<EtfGridEntity> gridEntityList = etfGridService.queryList(params);
        if (!CollectionUtils.isEmpty(gridEntityList)) {
            return "已导入订单";
        }
        if (data.getConfirm_volume().equals(new BigDecimal(0))) {
            return "份额为0";
        }
        EtfGridEntity gridEntity = new EtfGridEntity();
        gridEntity.setStatus(1);
        gridEntity.setBuyAmount(data.getConfirm_amount());
        gridEntity.setNum(data.getConfirm_volume());
        BigDecimal divide = gridEntity.getBuyAmount().divide(gridEntity.getNum(), 6, BigDecimal.ROUND_HALF_UP);
        gridEntity.setBuyPrice(divide);
        gridEntity.setBuyTime(DateUtil.date(data.getCreated_at()));
        gridEntity.setPlanId(planEntity.getId());
        gridEntity.setOutId(data.getOrder_id());
        gridEntity.insert();
        return "成功";
    }

    @Autowired
    private DanJuanService danJuanService;

    @RequestMapping("/importDanJuanDataList")
    public String importDanJuanDataList(@RequestParam String zCode, @RequestParam String fundCode, @RequestHeader String cookieParams) {
        if (StringUtils.isBlank(cookieParams)) {
            return "cookie为空";
        }
        DanJuanTradeList tradeList = danJuanService.getTradeList(zCode, fundCode, cookieParams);
        List<Item> items = tradeList.getData().getItems().stream().filter(u -> !"failed".equals(u.getStatus()) && !"pay_failed".equals(u.getStatus())).collect(Collectors.toList());
        for (int i = 0; i < items.size(); i++) {
            // 已导入订单
            HashMap<String, Object> params = new HashMap<>();
            params.put("out_id", items.get(i).getOrderId());
            List<EtfGridEntity> gridEntityList = etfGridService.queryList(params);
            if (!CollectionUtils.isEmpty(gridEntityList)) {
                continue;
            }
            DanJuanModel orderInfo = danJuanService.getOrderInfo(items.get(i).getOrderId(), cookieParams);
            importDanJuanData(orderInfo);
        }
        return "成功";

    }
}
