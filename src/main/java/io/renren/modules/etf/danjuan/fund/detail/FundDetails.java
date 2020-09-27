
package io.renren.modules.etf.danjuan.fund.detail;

import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class FundDetails {

    private io.renren.modules.etf.danjuan.fund.detail.Data data;
    private long resultCode;

}
