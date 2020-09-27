
package io.renren.modules.etf.danjuan.fund.detail;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class StockList {

    private Boolean amarket;
    private double changePercentage;
    private String code;
    private double currentPrice;
    private String name;
    private double percent;
    private String xqSymbol;
    private String xqUrl;

}
