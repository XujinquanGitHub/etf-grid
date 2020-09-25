
package io.renren.modules.etf.danjuan.index;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class IndexUpsAndDowns {

    private List<Datum> data;
    private long resultCode;

}
