
package io.renren.modules.etf.danjuan.worth;

import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class DanJuanWorthInfo {

    private io.renren.modules.etf.danjuan.worth.Data data;
    private long resultCode;

}
