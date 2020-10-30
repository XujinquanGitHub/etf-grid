package io.renren.modules.etf.xueqiu;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.danjuan.fund.DanJuanFundInfo;
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
        heads.put("Cookie", "device_id=b8b0bc6bb59c3b9df93e3d10d7bf0d30; s=dg1bu0r2wv; xq_a_token=3242a6863ac15761c18a8469b89065b03bd5e164; xqat=3242a6863ac15761c18a8469b89065b03bd5e164; xq_r_token=729679220e12a2fbd19b15c94e6b7624c5ea8702; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTYwNDgwMzAzMSwiY3RtIjoxNjAzMzMwNzU0NTEyLCJjaWQiOiJkOWQwbjRBWnVwIn0.TcBY1-3Hvx8-vdEQ1rTTfqrJvdg75X78iHR27QCNnxPG3S4Ao05PKGN3tGUVA7P2YeuxWPvhMB786oqW6UfF2X82L7hJI6orZLJBivLQ9vOI5KGyi4ehzthGxDqYXaVK_t4HQVtJuKVSGYOBi4mESoNqtNpxgnsW1Bht42gYMSc2hXN1CNjaG47bUuPl0H23Xh8yd_ta6vxr5PTjYUvf2qpmdfpHyqIQHJSbm_vecu4YjMrfArkCb6u7rddlp97IeNKN5_NZ4B9ixYdaZuAY2Jy1E6sgefDUUZp1ktUAGbvKrHDU9gT5Gb73DvPjQpcgh2SJhbZ3O5KCe9x8j_wEuQ; u=261603330811697; Hm_lvt_1db88642e346389874251b5a1eded6e3=1603330811,1603335502,1603964903,1603964908; is_overseas=0; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1604052807");
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

    public BigDecimal getStockTTM(String stockNo) {
        StockQuote stockDetail = getStockDetail(stockNo);
        double peTtm = stockDetail.getData().getQuote().getPeTtm();
        return new BigDecimal(peTtm);
    }

}
