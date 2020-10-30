
package io.renren.modules.etf.danjuan.valuation;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Item {

    private long beginAt;
    private double bondYeild;
    private long createdAt;
    private String date;
    private String evaType;
    private long evaTypeInt;
    private long id;
    private String indexCode;
    private String name;
    private double pb;
    private Boolean pbFlag;
    private double pbOverHistory;
    private double pbPercentile;
    private double pe;
    private double peOverHistory;
    private double pePercentile;
    private double peg;
    private double roe;
    private long ts;
    private String ttype;
    private long updatedAt;
    private String url;
    private double yeild;

}
