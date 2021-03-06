package io.renren.modules.etf.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.danjuan.fund.DanJuanFundInfo;
import io.renren.modules.etf.danjuan.fund.Data;
import io.renren.modules.etf.danjuan.fund.FundDerived;
import io.renren.modules.etf.danjuan.index.Datum;
import io.renren.modules.etf.danjuan.index.IndexUpsAndDowns;
import io.renren.modules.etf.dao.EtfInvestmentPlanDao;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;
import io.renren.modules.etf.service.EtfInvestmentPlanService;
import io.renren.modules.etf.xueqiu.XueQiuService;
import io.renren.modules.etf.xueqiu.stock.Quote;
import io.renren.modules.etf.xueqiu.stock.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import org.springframework.util.CollectionUtils;


@Service("etfInvestmentPlanService")
public class EtfInvestmentPlanServiceImpl extends ServiceImpl<EtfInvestmentPlanDao, EtfInvestmentPlanEntity> implements EtfInvestmentPlanService {

    @Autowired
    private DanJuanService danJuanService;

    @Autowired
    private XueQiuService xueQiuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfInvestmentPlanEntity> page = this.page(new Query<EtfInvestmentPlanEntity>().getPage(params), new QueryWrapper<EtfInvestmentPlanEntity>());
        return new PageUtils(page);
    }

    public List<EtfInvestmentPlanEntity> queryListByFundName(String fundName) {
        Map<String, Object> params = new HashMap<>();
        params.put("fund_name", fundName);
        return listByMap(params);
    }

    public EtfInvestmentPlanEntity queryByFundName(String fundName) {
        List<EtfInvestmentPlanEntity> etfInvestmentPlanEntities = queryListByFundName(fundName);
        if (CollectionUtils.isEmpty(etfInvestmentPlanEntities)) {
            return null;
        }
        return etfInvestmentPlanEntities.get(0);
    }

    public FundModel getFundInfoByTianTian(String fundNo) {
        String url = "http://fundgz.1234567.com.cn/js/" + fundNo + ".js?rt=634543645643";
        Map<String, String> head = new HashMap<>();
        head.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        head.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
        String response = HttpRequest.get(url).addHeaders(head).execute().body();
        response = response.replace("jsonpgz({", "{");
        response = response.replace("});", "}");
        System.out.println(response);
        return JSONUtil.toBean(response, FundModel.class);

    }

    @Override
    public FundModel getFundInfo(String fundNo, String indexNo) {
        return getFundInfo(fundNo, indexNo, "");
    }

    @Override
    public FundModel getFundInfo(String fundNo, String indexNo, String venueNo) {
        FundModel result = null;
        try {
            result = getFundInfoByTianTian(fundNo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (!result.getGztime().contains(sdf.format(new Date()))) {
                throw new RuntimeException("非今天数据");
            }
        } catch (Exception ex) {
            System.out.println("异常：" + ex.toString());
            try {
                DanJuanFundInfo fundInfo = danJuanService.getFundInfo(fundNo, "");
                Data data = fundInfo.getData();
                FundDerived fundDerived = data.getFundDerived();
                // 从蛋卷获取到的是前一个交易日的净值
                result = new FundModel().setFundcode(fundNo).setName(data.getFdName()).setGsz(fundDerived.getUnit_nav()).setDwjz(fundDerived.getUnit_nav());
                if (StringUtils.isNotBlank(venueNo)) {
                    Quote stockTTM = xueQiuService.getStockTTM(venueNo);
                    if (stockTTM != null) {
                        double percent = stockTTM.getPercent();
                        BigDecimal pr = new BigDecimal(percent).setScale(4, BigDecimal.ROUND_HALF_UP);
                        result.setGszzl(pr);
                        BigDecimal amountPercent = new BigDecimal(100).add(pr).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
                        BigDecimal newPrice = fundDerived.getUnit_nav().multiply(amountPercent);
                        result.setGsz(newPrice);
                    }
                }
                if (StringUtils.isNotBlank(indexNo)) {
                    IndexUpsAndDowns mainIndexChanges = danJuanService.getMainIndexChanges();
                    Optional<Datum> first = mainIndexChanges.getData().stream().filter(u -> indexNo.equals(u.getSymbol())).findFirst();
                    if (!first.isPresent()) {
                        IndexUpsAndDowns industryIndexChanges = danJuanService.getIndustryIndexChanges();
                        first = industryIndexChanges.getData().stream().filter(u -> indexNo.equals(u.getSymbol())).findFirst();
                    }

                    if (first.isPresent()) {
                        Datum datum = first.get();
                        BigDecimal pr = new BigDecimal(datum.getPercentage()).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_UP);
                        result.setGszzl(pr);
                        BigDecimal amountPercent = new BigDecimal(1).add(pr);
                        log.debug("指数情况：" + JSONUtil.toJsonStr(datum));
                        log.debug("涨跌幅度：" + amountPercent);
                        BigDecimal newPrice = fundDerived.getUnit_nav().multiply(amountPercent);
                        result.setGsz(newPrice);
                    } else {
                        result.setGszzl(danJuanService.getIndexPercent(indexNo, null)).setDwjz(fundDerived.getUnit_nav());
                    }
                }
            } catch (Exception exs) {
                System.out.println(exs.toString());
                result = new FundModel().setFundcode(fundNo).setGsz(new BigDecimal(1));
            }
        }
        return result;

    }

    @Override
    public List<EtfInvestmentPlanEntity> queryList(Map<String, Object> params) {
        return listByMap(params);
    }
}