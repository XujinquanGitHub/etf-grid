
package io.renren.modules.etf.danjuan.pe;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class IndexEvaPeGrowth {

    private double pe;
    private long ts;

}
