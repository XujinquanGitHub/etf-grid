
package io.renren.modules.etf.danjuan.pb;

import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class PbHistoryModel {

    private io.renren.modules.etf.danjuan.pb.Data data;
    private long resultCode;

}
