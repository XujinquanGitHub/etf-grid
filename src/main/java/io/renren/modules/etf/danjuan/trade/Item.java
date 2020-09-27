
package io.renren.modules.etf.danjuan.trade;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Item {

    private String action;
    private String actionDesc;
    private BigDecimal amount;
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

}
