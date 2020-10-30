package io.renren.modules.etf.service.impl;

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
import io.renren.modules.etf.danjuan.trade.SingleFundTradeList;
import io.renren.modules.etf.danjuan.valuation.DanJuanValuation;
import io.renren.modules.etf.danjuan.valuation.Item;
import io.renren.modules.etf.danjuan.worth.DanJuanWorthInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
