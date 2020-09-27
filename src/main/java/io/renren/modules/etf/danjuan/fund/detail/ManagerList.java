
package io.renren.modules.etf.danjuan.fund.detail;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class ManagerList {

    private List<AchievementList> achievementList;
    private String college;
    private String name;
    private String resume;
    private String workYear;

}
