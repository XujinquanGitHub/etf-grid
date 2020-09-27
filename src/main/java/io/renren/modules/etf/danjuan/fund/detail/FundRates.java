
package io.renren.modules.etf.danjuan.fund.detail;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class FundRates {

    private String declareDiscount;
    private String declareRate;
    private List<DeclareRateTable> declareRateTable;
    private String discount;
    private String fdCode;
    private List<OtherRateTable> otherRateTable;
    private String subscribeDiscount;
    private String subscribeRate;
    private String withdrawRate;
    private List<WithdrawRateTable> withdrawRateTable;

}
