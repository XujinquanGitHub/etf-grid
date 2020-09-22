
package io.renren.modules.etf.danjuan;



import java.util.List;

@lombok.Data
@SuppressWarnings("unused")
public class DanJuanTradeList {

    private Data data;
    private long resultCode;


    @lombok.Data
    @SuppressWarnings("unused")
    public class Data {

        private long currentPage;
        private List<Item> items;
        private long size;
        private long totalItems;
        private long totalPages;

    }
}
