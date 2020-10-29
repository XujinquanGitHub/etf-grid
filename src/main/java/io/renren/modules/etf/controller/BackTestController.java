package io.renren.modules.etf.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import io.renren.common.utils.DateUtils;
import io.renren.modules.etf.FundDown;
import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.OperationModel;
import io.renren.modules.etf.entity.EtfFundWorthEntity;
import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.service.EtfFundWorthService;
import io.renren.modules.etf.service.FundSituationDay;
import io.renren.modules.etf.service.impl.DanJuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public JSONObject regardlessOfTheValuationGridBackTest(@RequestParam String fundNo, @RequestParam String day) {
        // 每一网投入多少
        BigDecimal gridPrice = new BigDecimal(100);
        // 每下坠几个百分点加入一网
        BigDecimal gridPercentage = new BigDecimal(-0.1);
        // 每网上升几个百分点卖出
        BigDecimal gridFailPercentage = new BigDecimal(5);

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
        EtfFundWorthEntity startWorthModel = worthEntityList.get(0);
        gridEntityList.add(buy(startWorthModel.getWorth(), startWorthModel.getFundDate(), new BigDecimal(1000), fundNo));

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
                gridEntityList.add(buy(worthEntity.getWorth(), worthEntity.getFundDate(), money, fundNo).setFailToday(failPercentage));
            }

            List<EtfGridEntity> sellList = gridEntityList.stream().filter(u -> u.getStatus().equals(1) && worthEntity.getWorth().subtract(u.getBuyPrice()).divide(u.getBuyPrice(), 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue() > gridFailPercentage.doubleValue()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(sellList)) {
                for (EtfGridEntity gridModel : sellList) {
                    gridModel.setStatus(3);
                    gridModel.setSellPrice(worthEntity.getWorth());
                    gridModel.setSellTime(worthEntity.getFundDate());
                    gridModel.setSellAmount(gridModel.getSellPrice().multiply(gridModel.getNum()));
                }
            }

        }
        double buyTotalMoney = gridEntityList.stream().mapToDouble(u -> u.getBuyAmount().doubleValue()).sum();
        double sellTotalMoney = gridEntityList.stream().filter(u->u.getStatus().equals(3)).mapToDouble(u -> u.getSellAmount().doubleValue()).sum();
        double buyMoneySell=gridEntityList.stream().filter(u->u.getStatus().equals(3)).mapToDouble(u -> u.getBuyAmount().doubleValue()).sum();
        return result.fluentPut("基金买卖情况", gridEntityList).fluentPut("总买入", buyTotalMoney).fluentPut("总卖出", sellTotalMoney).fluentPut("利润率", (sellTotalMoney - buyMoneySell) / buyMoneySell);
    }

    private OperationModel buy(BigDecimal startWorth, Date fundDate, BigDecimal money, String fundNo) {
        BigDecimal num = money.divide(startWorth, 6, BigDecimal.ROUND_HALF_UP);
        EtfGridEntity gridEntity = new OperationModel().setFundNo(fundNo).setBuyTime(fundDate).setBuyPrice(startWorth).setBuyAmount(money).setNum(num).setStatus(1);
        return (OperationModel) gridEntity;
    }


}
