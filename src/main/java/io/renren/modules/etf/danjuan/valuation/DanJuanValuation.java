
package io.renren.modules.etf.danjuan.valuation;

import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class DanJuanValuation {

    private io.renren.modules.etf.danjuan.valuation.Data data;
    private long resultCode;

}
