
package io.renren.modules.etf.danjuan.pe;

import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class PeHistoryModel {

    private io.renren.modules.etf.danjuan.pe.Data data;
    private long resultCode;

}
