package io.renren.modules.etf.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.danjuan.DanJuanModel;
import io.renren.modules.etf.danjuan.DanJuanTradeList;
import io.renren.modules.etf.danjuan.fund.DanJuanFundInfo;
import io.renren.modules.etf.danjuan.fund.detail.FundDetails;
import io.renren.modules.etf.danjuan.index.IndexUpsAndDowns;
import io.renren.modules.etf.danjuan.pb.HorizontalLine;
import io.renren.modules.etf.danjuan.pb.IndexEvaPbGrowth;
import io.renren.modules.etf.danjuan.pb.PbHistoryModel;
import io.renren.modules.etf.danjuan.pe.IndexEvaPeGrowth;
import io.renren.modules.etf.danjuan.pe.PeHistoryModel;
import io.renren.modules.etf.danjuan.trade.SingleFundTradeList;
import io.renren.modules.etf.danjuan.valuation.DanJuanValuation;
import io.renren.modules.etf.danjuan.valuation.Item;
import io.renren.modules.etf.danjuan.worth.DanJuanWorthInfo;
import org.apache.commons.lang.StringUtils;
import org.postgresql.jdbc.TimestampUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-09-22 12:30
 **/
@Service
public class DanJuanService {

    private Map<String, FundDetails> industryProportionMap = new HashMap<>();

    private Map<String, List<Item>> danJuanValuationMap = new HashMap<>();

    private Map<String, PbHistoryModel> pbHistoryModelMap = new HashMap<>();

    private Map<String, PeHistoryModel> peHistoryModelMap = new HashMap<>();


    public DanJuanTradeList getTradeList(String zCode, String fundCode, String cookies) {

        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/order/" + zCode + "/" + fundCode + "/trade/list?size=300&page=1");
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求结果：" + body);
        return JSON.parseObject(body, DanJuanTradeList.class);
    }

    public SingleFundTradeList getSingleFundTradeList(String fundCode, String cookies) {

        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/order/p/" + fundCode + "/list?page=1&size=200&type=all");
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求结果：" + body);
        return JSON.parseObject(body, SingleFundTradeList.class);
    }


    public DanJuanModel getOrderInfoByPlan(String orderId, String cookies) {
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/plan/order/" + orderId);
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求订单详情：" + body);
        return JSON.parseObject(body, DanJuanModel.class);
    }

    public DanJuanModel getOrderInfoByFund(String orderId, String cookies) {
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/fund/order/" + orderId);
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求订单详情：" + body);
        return JSON.parseObject(body, DanJuanModel.class);
    }


    private IndexUpsAndDowns getIndexUpsAndDowns(String category, String cookies) {
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/v3/index/quotes?category=" + category);
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求指数涨跌：" + body);
        return JSON.parseObject(body, IndexUpsAndDowns.class);
    }

    public FundDetails getFundDetails(String fundNo, String cookies) {
        FundDetails fundDetails = industryProportionMap.get(fundNo);
        if (fundDetails != null) {
            return fundDetails;
        }
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/fund/detail/" + fundNo);
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("基金详情：" + body);
        return JSON.parseObject(body, FundDetails.class);
    }


    public IndexUpsAndDowns getMainIndexChanges() {
        return getIndexUpsAndDowns("zyzs", null);
    }

    public IndexUpsAndDowns getIndustryIndexChanges() {
        return getIndexUpsAndDowns("hyzs", null);
    }

    public DanJuanFundInfo getFundInfo(String fundNo, String cookies) {
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/fund/" + fundNo);
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求基金详情：" + body);
        return JSON.parseObject(body, DanJuanFundInfo.class);
    }

    public List<Item> getFundValuation(String cookies) {
        String s = DateUtil.formatDate(new Date());
        List<Item> itemList = danJuanValuationMap.get(s);
        if (!CollectionUtils.isEmpty(itemList)) {
            return itemList;
        }
        danJuanValuationMap.clear();
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/index_eva/dj");
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("基金估值：" + body);
        DanJuanValuation danJuanValuation = JSON.parseObject(body, DanJuanValuation.class);
        List<Item> items = danJuanValuation.getData().getItems();
        danJuanValuationMap.put(s, items);
        return items;
    }

    public PbHistoryModel getPbHistory(String cookies, String indexNo) {
        String s = DateUtil.formatDate(new Date());
        PbHistoryModel pbHistoryModel1 = pbHistoryModelMap.get(s);
        if (pbHistoryModel1 != null) {
            return pbHistoryModel1;
        }
        danJuanValuationMap.clear();
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/index_eva/pb_history/" + indexNo + "?day=all");
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("基金估值：" + body);
        PbHistoryModel pbHistoryModel = JSON.parseObject(body, PbHistoryModel.class);
        pbHistoryModelMap.put(s, pbHistoryModel);
        return pbHistoryModel1;
    }

    public PeHistoryModel getPeHistory(String cookies, String indexNo) {
        String s = DateUtil.formatDate(new Date());
        PeHistoryModel peHistoryModel1 = peHistoryModelMap.get(s);
        if (peHistoryModel1 != null) {
            return peHistoryModel1;
        }
        danJuanValuationMap.clear();
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/index_eva/pe_history/" + indexNo + "?day=all");
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("基金估值：" + body);
        PeHistoryModel peHistoryModel = JSON.parseObject(body, PeHistoryModel.class);
        peHistoryModelMap.put(s, peHistoryModel);
        return peHistoryModel;
    }

    public boolean isLow(String indexNo, DateTime dateTime) {
        Timestamp timestamp = DateUtil.parse(DateUtil.formatDateTime(dateTime)).toTimestamp();

        PbHistoryModel pbHistory = getPbHistory(null, indexNo);
        List<HorizontalLine> horizontalLines = pbHistory.getData().getHorizontalLines();
        double lowPb = horizontalLines.stream().mapToDouble(u -> u.getLineValue()).min().getAsDouble();
        Optional<IndexEvaPbGrowth> first = pbHistory.getData().getIndexEvaPbGrowths().stream().filter(u -> timestamp.equals(u.getTs())).findFirst();
        if (!first.isPresent()) {
            System.out.println("未查询到该交易日");
            return false;
        }
        IndexEvaPbGrowth indexEvaPbGrowth = first.get();
        if (indexEvaPbGrowth.getPb() > lowPb) {
            return false;
        }

        PeHistoryModel peHistory = getPeHistory(null, indexNo);
        List<io.renren.modules.etf.danjuan.pe.HorizontalLine> peLines = peHistory.getData().getHorizontalLines();
        double lowPe = peLines.stream().mapToDouble(u -> u.getLineValue()).min().getAsDouble();
        Optional<IndexEvaPeGrowth> two = peHistory.getData().getIndexEvaPeGrowths().stream().filter(u -> timestamp.equals(u.getTs())).findFirst();
        if (!two.isPresent()) {
            System.out.println("未查询到该交易日");
            return false;
        }
        IndexEvaPeGrowth peGrowth = two.get();
        if (peGrowth.getPe() > lowPb) {
            return false;
        }
        return true;
    }


    public Optional<Item> getFundValuationByFundTypeName(String fundTypeName) {
        List<Item> fundValuation = getFundValuation(null);
        return fundValuation.stream().filter(u -> fundTypeName.equals(u.getName()) || fundTypeName.contains(u.getName()) || u.getName().contains(fundTypeName)).findFirst();
    }

    public String getValuationStringByFundTypeName(String fundTypeName) {
        Optional<Item> fundValuationByFundTypeName = getFundValuationByFundTypeName(fundTypeName);
        if (fundValuationByFundTypeName.isPresent()) {
            return fundValuationByFundTypeName.get().getEvaType();
        }
        return "未知";
    }


    public DanJuanWorthInfo getFundWorth(String fundNo, String cookies) {
        return getFundWorth(fundNo, cookies, 10000);
    }


    public DanJuanWorthInfo getFundWorth(String fundNo, String cookies, long size) {
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/fund/nav/history/" + fundNo + "?size=" + size);
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求净值：" + body);
        return JSONUtil.toBean(body, DanJuanWorthInfo.class);
    }

    private Map<String, String> getHead(String cookies) {
        Map<String, String> head = new HashMap<>();
        head.put("Accept", "application/json, text/plain, */*");
        head.put("Accept-Encoding", " gzip, deflate, br");
        head.put("Accept-Language", "en,zh-CN;q=0.9,zh;q=0.8");
        if (StringUtils.isNotBlank(cookies)) {
            head.put("Cookie", cookies);
        }
        head.put("Sec-Fetch-Dest", "empty");
        head.put("Sec-Fetch-Mode", "cors");
        head.put("Sec-Fetch-Site", "same-origin");
        return head;
    }

}
