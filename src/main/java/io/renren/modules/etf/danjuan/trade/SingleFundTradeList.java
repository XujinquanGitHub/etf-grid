
package io.renren.modules.etf.danjuan.trade;

import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class SingleFundTradeList {

    private io.renren.modules.etf.danjuan.trade.Data data;
    private long resultCode;

}
