
package io.renren.modules.etf.danjuan.fund;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class FundDerived {

    private String endDate;
    private String navGrbase;
    private String navGrl1m;
    private String navGrl1y;
    private String navGrl3m;
    private String navGrl3y;
    private String navGrl6m;
    private String navGrlty;
    private String navGrowth;
    private String navGrtd;
    private String srankL1m;
    private String srankL1y;
    private String srankL3m;
    private String srankL3y;
    private String srankL6m;
    private String srankLty;
    private BigDecimal unit_nav;
    private List<YieldHistory> yieldHistory;

}
