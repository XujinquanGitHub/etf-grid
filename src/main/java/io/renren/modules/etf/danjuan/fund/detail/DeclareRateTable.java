
package io.renren.modules.etf.danjuan.fund.detail;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class DeclareRateTable {

    private String name;
    private String value;

}
