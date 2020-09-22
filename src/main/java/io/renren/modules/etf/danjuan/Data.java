
package io.renren.modules.etf.danjuan;

import java.math.BigDecimal;
import java.util.List;

@lombok.Data
@SuppressWarnings("unused")
public class Data {

    private String action;
    private String action_desc;
    private BigDecimal amount;
    private String bank_name;
    private String channel;
    private String code;
    private BigDecimal confirm_amount;
    private List<List<String>> confirm_infos;
    private BigDecimal confirm_volume;
    private Boolean convert;
    private long created_at;
    private Boolean cycle_flag;
    private List<String> desc;
    private String fd_code;
    private String fd_name;
    private String if_undo;
    private String name;
    private Boolean old_version;
    private String order_id;
    private long parent_order_id;
    private Boolean plan;
    private String plan_type;
    private String status;
    private String status_desc;
    private long step;
    private List<SubOrder> sub_orders;
    private String title;
    private String transaction_account_id;
    private long ts;
    private long ts_confirm;
    private String ttype;
    private long uid;
    private String undo_tip;
    private long volume;

}
