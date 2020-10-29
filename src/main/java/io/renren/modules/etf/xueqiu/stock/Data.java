
package io.renren.modules.etf.xueqiu.stock;

import java.util.List;
import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Data {

    private Market market;
    private Others others;
    private Quote quote;
    private List<Tag> tags;

}
