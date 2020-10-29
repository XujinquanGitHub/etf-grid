
package io.renren.modules.etf.xueqiu.stock;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Tag {

    private String description;
    private long value;

}
