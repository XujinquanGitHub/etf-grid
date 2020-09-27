
package io.renren.modules.etf.danjuan.fund.detail;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class BondList {

    private Boolean amarket;
    private String code;
    private String name;
    private double percent;
    private String xqSymbol;
    private String xqUrl;

}
