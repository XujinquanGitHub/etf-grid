package io.renren.modules.etf.xueqiu;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.danjuan.fund.DanJuanFundInfo;
import io.renren.modules.etf.xueqiu.stock.StockQuote;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
