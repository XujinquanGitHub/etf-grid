
package io.renren.modules.etf.danjuan;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class Item {

    private String action;
    private String actionDesc;
    private double amount;
    private String code;
    private Boolean convert;
    private long createdAt;
    private String name;
    private String orderId;
    private String status;
    private String statusDesc;
    private String title;
    private String ttype;
    private long uid;
    private String valueDesc;
    private long volume;

}
