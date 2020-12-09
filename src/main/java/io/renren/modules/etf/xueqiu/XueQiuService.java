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
        heads.put("Cookie", "device_id=b8b0bc6bb59c3b9df93e3d10d7bf0d30; s=dg1bu0r2wv; __utmz=1.1600651890.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utma=1.1305052674.1600651890.1600651890.1603335541.2; acw_tc=2760820416067004925561681e32dd4b4020f5e6a055fbcbdb3ae2677ac266; xq_a_token=1132205e8c57eb587b26526804cff9f3b6bf6799; xqat=1132205e8c57eb587b26526804cff9f3b6bf6799; xq_r_token=81b9c911ea3907729d8f8e9f60d9f5251227c551; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTYwOTEyMzA1NywiY3RtIjoxNjA2NzAwNDg2Njc4LCJjaWQiOiJkOWQwbjRBWnVwIn0.UlFMyjQK_Bewbczje-Ab8GWGR6HCEBt2EZCrq8NIG-U1P6Rztb2aW2entAKurEEVFWdrom9X96VLe5W7mgepxNIQe7ZHrAln-YRaH8V94tkGVs4fVdT1SLKDwWaqhz5N3ifxO-_n9-Ca7hjfhjuvVvq9HrQ7irnW1umFqUVy3i5aiCNJkdtOg5s51QPrsjmFFD1SZphS6yb6iZ4j1v_PRz-i1YkDU-FqcG8IT0hp06Pay7SGX1DfiqNNMvxhqqQ5qbq-scKqJ_KJwapaqRHDjR0yRgK5iBAR1iehd-7fJ69gLDzEHRWlXBLFJofdnj_vsnF02FEbuVUyMCwXcu-OdA; u=261606700492563; Hm_lvt_1db88642e346389874251b5a1eded6e3=1604979998,1605150383,1605260976,1606700494; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1606700494");
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
