
package io.renren.modules.etf.danjuan.fund;

import java.util.List;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class OpFund {

    private String bannerImg;
    private List<FundTag> fundTags;
    private String recomDesc;
    private String tips;

}
