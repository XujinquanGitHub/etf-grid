
package io.renren.modules.etf.danjuan;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class SubOrder {

    private String action;
    private String actionDesc;
    private double amount;
    private String bankName;
    private String code;
    private Boolean convert;
    private long createdAt;
    private String fdCode;
    private String fdName;
    private String name;
    private Boolean oldVersion;
    private String orderId;
    private long parentOrderId;
    private Boolean plan;
    private String status;
    private String statusDesc;
    private String title;
    private String transactionAccountId;
    private long ts;
    private String ttype;
    private long uid;

}
