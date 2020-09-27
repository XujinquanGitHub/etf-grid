
package io.renren.modules.etf.danjuan.fund.detail;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class FundDateConf {

    private long allBuyDays;
    private long allSaleDays;
    private long buyConfirmDate;
    private long buyQueryDate;
    private String fdCode;
    private long saleConfirmDate;
    private long saleQueryDate;

}
