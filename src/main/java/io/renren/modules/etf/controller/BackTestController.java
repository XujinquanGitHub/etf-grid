package io.renren.modules.etf.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import io.renren.modules.etf.BackGridRequest;
import io.renren.modules.etf.FundDown;
import io.renren.modules.etf.OperationModel;
import io.renren.modules.etf.entity.EtfFundWorthEntity;
import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.service.EtfFundWorthService;
import io.renren.modules.etf.service.impl.DanJuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: etf-grid
 * @description: 回测方法
 * @author: 许金泉
 * @create: 2020-10-29 15:17
 **/
@RestController
@RequestMapping("generator/back")
public class BackTestController {

    @Autowired
    private EtfFundWorthService etfFundWorthService;

    @Autowired
    private DanJuanService danJuanService;

    @RequestMapping("/regardlessOfTheValuationGridBackTest")
    public JSONObject regardlessOfTheValuationGridBackTest(@RequestBody BackGridRequest request) {
        String fundNo = request.getFundNo();
        String day = request.getStartDay();

        // 每一网投入多少
        BigDecimal gridPrice = request.getGridPrice();
        // 每下坠几个百分点加入一网
        BigDecimal gridPercentage = request.getGridPercentage();
        // 每网上升几个百分点卖出
        BigDecimal gridFailPercentage = request.getGridFailPercentage();

        // 可投入金额
        BigDecimal investmentAmount = request.getInvestmentAmount();

        List<OperationModel> gridEntityList = new ArrayList<>();

        DateTime startDate = DateUtil.parse(day);
        DateTime today = DateUtil.parse(DateUtil.today());
        // 计算相差日期
        long betweenDay = DateUtil.betweenDay(startDate, today, false);
        // 大致毛估交易日期
        double numDay = ((double) 0.72) * ((double) (betweenDay));
        int dayNum = (int) numDay;

        JSONObject result = new JSONObject();
        Map<String, String> fundMap = new LinkedHashMap<>();
        List<FundDown> downList = new ArrayList<>();
        List<EtfFundWorthEntity> worthEntityList = etfFundWorthService.getWorthByDanJuan(fundNo, null, dayNum);
        worthEntityList = worthEntityList.stream().filter(u -> u.getPercentage() != null && startDate.before(u.getFundDate())).sorted(Comparator.comparing(EtfFundWorthEntity::getFundDate)).collect(Collectors.toList());


        // 第一日净值
        BigDecimal bigDecimal = new BigDecimal(1000);
        EtfFundWorthEntity startWorthModel = worthEntityList.get(0);
        OperationModel buy = buy(startWorthModel.getWorth(), startWorthModel.getFundDate(), bigDecimal, fundNo, startWorthModel.getFundDate(), investmentAmount);
        investmentAmount = buy.getAmountThatCanBeInvested();
        gridEntityList.add(buy);

        Date lastBuyOrSellDate = startWorthModel.getFundDate();

        for (EtfFundWorthEntity worthEntity : worthEntityList) {

            // 如果已全部卖出，则按最初的价格做最低价格
            BigDecimal lastWorth = startWorthModel.getWorth();
            Optional<OperationModel> lastWorthModelOp = gridEntityList.stream().filter(u -> u.getStatus().equals(1)).sorted(Comparator.comparing(OperationModel::getBuyPrice)).findFirst();

            if (lastWorthModelOp.isPresent()) {
                lastWorth = lastWorthModelOp.get().getBuyPrice();
            }

            BigDecimal failPercentage = worthEntity.getWorth().subtract(lastWorth).divide(lastWorth, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            // 距离上一个买入点低，加仓买入
            if (failPercentage.doubleValue() < gridPercentage.doubleValue()) {
                BigDecimal money = failPercentage.divide(gridPercentage, 6, BigDecimal.ROUND_HALF_UP).multiply(gridPrice);
                OperationModel operationModel = buy(worthEntity.getWorth(), worthEntity.getFundDate(), money, fundNo, lastBuyOrSellDate, investmentAmount).setFailToday(failPercentage);
                investmentAmount = operationModel.getAmountThatCanBeInvested();
                gridEntityList.add(operationModel);

                lastBuyOrSellDate = worthEntity.getFundDate();
            }

            List<EtfGridEntity> sellList = gridEntityList.stream().filter(u -> u.getStatus().equals(1) && worthEntity.getWorth().subtract(u.getBuyPrice()).divide(u.getBuyPrice(), 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue() > gridFailPercentage.doubleValue()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(sellList)) {
                for (EtfGridEntity gridModel : sellList) {
                    gridModel.setStatus(3);
                    gridModel.setSellPrice(worthEntity.getWorth());
                    gridModel.setSellTime(worthEntity.getFundDate());
                    gridModel.setSellAmount(gridModel.getSellPrice().multiply(gridModel.getNum()));

                    investmentAmount = investmentAmount.add(gridModel.getSellAmount());
                }
                lastBuyOrSellDate = worthEntity.getFundDate();
            }

        }
        // 最后一天的净值
        EtfFundWorthEntity etfFundWorthEntity = worthEntityList.get(worthEntityList.size() - 1);
        double totalUnsold = gridEntityList.stream().filter(u -> u.getStatus().equals(1)).mapToDouble(u -> etfFundWorthEntity.getWorth().multiply(u.getNum()).doubleValue()).sum();
        BigDecimal totalMoney = investmentAmount.add(new BigDecimal(totalUnsold));
        BigDecimal profitMargin = totalMoney.subtract(request.getInvestmentAmount()).divide(request.getInvestmentAmount(), 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        return result.fluentPut("基金买卖情况", gridEntityList).fluentPut("未卖出金额", totalUnsold).fluentPut("总金额",totalMoney).fluentPut("利润率",profitMargin);
    }

    private OperationModel buy(BigDecimal startWorth, Date fundDate, BigDecimal money, String fundNo, Date lastBuyOrSellDate, BigDecimal invertMoney) {
        OperationModel gridEntity = new OperationModel();
        // 根据上次买入的时间和这次买入的时间计算货币基金这段时间收益
        if (!fundDate.equals(lastBuyOrSellDate)) {
            invertMoney = moneyFundIncome(invertMoney, lastBuyOrSellDate, fundDate);
        }
        if (invertMoney.doubleValue() > money.doubleValue()) {
            invertMoney = invertMoney.subtract(money);
            gridEntity.setAmountThatCanBeInvested(invertMoney);
        } else {
            money = invertMoney;
            gridEntity.setAmountThatCanBeInvested(new BigDecimal(0));
        }

        BigDecimal num = money.divide(startWorth, 6, BigDecimal.ROUND_HALF_UP);
        gridEntity.setFundNo(fundNo).setBuyTime(fundDate).setBuyPrice(startWorth).setBuyAmount(money).setNum(num).setStatus(1);
        return (OperationModel) gridEntity;
    }

    // 计算货币基金之后的收益
    private BigDecimal moneyFundIncome(BigDecimal startMoney, Date startDate, Date endDate) {
        BigDecimal fundPercentage = new BigDecimal("0.02");
        long day = DateUtil.betweenDay(startDate, endDate, false);
        BigDecimal divide = new BigDecimal(day).divide(new BigDecimal(365), 6, BigDecimal.ROUND_HALF_UP);
        return divide.multiply(fundPercentage).multiply(startMoney).add(startMoney);
    }

}
