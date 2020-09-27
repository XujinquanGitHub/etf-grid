
package io.renren.modules.etf.danjuan.fund.detail;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class FundPosition {

    private double assetTot;
    private double assetVal;
    private List<BondList> bondList;
    private double bondPercent;
    private double cashPercent;
    private String enddate;
    private double otherPercent;
    private String source;
    private String sourceMark;
    private List<StockList> stockList;
    private double stockPercent;

}
