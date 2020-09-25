
package io.renren.modules.etf.danjuan.index;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Datum {

    private double chg;
    private double current;
    private String fdCode;
    private String name;
    private double percentage;
    private String symbol;

}
