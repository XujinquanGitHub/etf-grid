package io.renren.modules.etf.xueqiu;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.danjuan.fund.DanJuanFundInfo;
import io.renren.modules.etf.xueqiu.stock.Quote;
import io.renren.modules.etf.xueqiu.stock.StockQuote;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-10-29 14:34
 **/
@Service
public class XueQiuService {

    public StockQuote getStockDetail(String stockNo) {
        String url = "https://stock.xueqiu.com/v5/stock/quote.json?symbol=" + stockNo + "&extend=detail";
        HttpRequest get = HttpRequest.get(url);
        Map<String, String> heads = new HashMap<>();
        heads.put("","");
heads.put("Accept-Encoding" ,"gzip, deflate, br");
heads.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        heads.put("Connection", "keep-alive");
        heads.put("Cookie", "device_id=b8b0bc6bb59c3b9df93e3d10d7bf0d30; s=dg1bu0r2wv; __utmz=1.1600651890.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utma=1.1305052674.1600651890.1600651890.1603335541.2; __utmc=1; Hm_lvt_1db88642e346389874251b5a1eded6e3=1603964908,1604306282,1604307206,1604307597; acw_tc=2760822616043951164772499e7ac1e5bc8dfa6f6f31546cfec3590a4b2dd3; xq_a_token=db48cfe87b71562f38e03269b22f459d974aa8ae; xqat=db48cfe87b71562f38e03269b22f459d974aa8ae; xq_r_token=500b4e3d30d8b8237cdcf62998edbf723842f73a; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTYwNjk2MzA1MCwiY3RtIjoxNjA0Mzk1MDc4Mzg0LCJjaWQiOiJkOWQwbjRBWnVwIn0.IMkK0x2Z5cNEtZXLdcrLrUEhNEI8Z4Jw1j5nEZFyeb967nAeqt-zWjUMs4ForhUTOu1VZoG61Bi3wDgUiUN7uEEwvTXX4YnlwAQXWYH7Hc4cngW9asiOFP9NhUvditUmpAlmXpCatWNWuMhgzKJlif8Fx1bZV7HjM2vZ9To-MSr-82DzrqmP6FxY7TLzFfHjrqwaCh_3dgUofEyH71iQ5fwQPH5eeAk1ApisFF3DliS8Y0BxecwhFGFGFgzWcSDa22XpQsnlW5raZyG6fooVWXbfUcM-FSquPRiGe-leJA0ips5WDCTydAAvccsPNgtTc__eBEK10igpOODbcg-r1w; u=821604395116487; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1604395117");
        heads.put("Host", "stock.xueqiu.com");
        heads.put("Origin", "https://xueqiu.com");
        heads.put("Sec-Fetch-Dest", "empty");
        heads.put("Sec-Fetch-Mode", "cors");
        heads.put("Sec-Fetch-Site", "same-site");
        heads.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
        get.addHeaders(heads);
        String body = get.execute().body();
        System.out.println("请求股票详情：" + body);
        return JSON.parseObject(body, StockQuote.class);
    }

    public Quote getStockTTM(String stockNo) {
        StockQuote stockDetail = getStockDetail(stockNo);
        return stockDetail.getData().getQuote();
    }

}
