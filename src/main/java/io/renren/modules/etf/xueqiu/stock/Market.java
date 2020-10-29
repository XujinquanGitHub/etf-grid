
package io.renren.modules.etf.xueqiu.stock;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Market {

    private String region;
    private String status;
    private long statusId;
    private String timeZone;
    private Object timeZoneDesc;

}
