
package io.renren.modules.etf.danjuan.fund.detail;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class AchievementList {

    private double cpRate;
    private String fundCode;
    private String fundsname;
    private String postDate;

}
