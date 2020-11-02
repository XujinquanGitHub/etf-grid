
package io.renren.modules.etf.danjuan.pb;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class HorizontalLine {

    private String lineColor;
    private String lineName;
    private double lineValue;

}
