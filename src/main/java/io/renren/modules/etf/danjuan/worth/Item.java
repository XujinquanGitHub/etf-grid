
package io.renren.modules.etf.danjuan.worth;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Item {

    private Date date;
    private BigDecimal nav;
    private BigDecimal percentage;
    private BigDecimal value;

}
