package io.renren.modules.etf.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.danjuan.DanJuanModel;
import io.renren.modules.etf.danjuan.DanJuanTradeList;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-09-22 12:30
 **/
@Service
public class DanJuanService {

    public DanJuanTradeList getTradeList(String zCode, String fundCode,String cookies) {

        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/order/" + zCode + "/" + fundCode + "/trade/list?size=300&page=1");
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求结果：" + body);
        return JSON.parseObject(body, DanJuanTradeList.class);
    }


    public DanJuanModel getOrderInfo(String orderId,String cookies) {
        HttpRequest get = HttpRequest.get("https://danjuanapp.com/djapi/plan/order/" + orderId);
        get.addHeaders(getHead(cookies));
        String body = get.execute().body();
        System.out.println("请求订单详情：" + body);
        return JSON.parseObject(body, DanJuanModel.class);
    }

    private Map<String, String> getHead(String cookies) {
        Map<String, String> head = new HashMap<>();
        head.put("Accept", "application/json, text/plain, */*");
        head.put("Accept-Encoding", " gzip, deflate, br");
        head.put("Accept-Language", "en,zh-CN;q=0.9,zh;q=0.8");
        head.put("Cookie", cookies);
        head.put("Sec-Fetch-Dest", "empty");
        head.put("Sec-Fetch-Mode", "cors");
        head.put("Sec-Fetch-Site", "same-origin");
        return head;
    }

}
