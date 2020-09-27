
package io.renren.modules.etf.danjuan.trade;

import java.util.List;
import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Data {

    private long currentPage;
    private List<Item> items;
    private long size;
    private long totalItems;
    private long totalPages;

}
