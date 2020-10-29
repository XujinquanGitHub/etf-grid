
package io.renren.modules.etf.xueqiu.stock;

import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class StockQuote {

    private io.renren.modules.etf.xueqiu.stock.Data data;
    private long errorCode;
    private String errorDescription;

}
