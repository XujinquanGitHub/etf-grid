
package io.renren.modules.etf.danjuan.pb;

import java.util.List;
import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Data {

    private List<HorizontalLine> horizontalLines;
    private List<IndexEvaPbGrowth> indexEvaPbGrowths;

}
