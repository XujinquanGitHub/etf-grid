package io.renren.modules.etf;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-09-18 17:52
 **/
@Data
@Accessors(chain = true)
public class FundModel {

    private String fundcode;
    private String name;
    //  private String jzrq;
    // 开盘价格
    private BigDecimal dwjz;

    // 当前价格
    private BigDecimal gsz;

    // 涨幅
    private BigDecimal gszzl;

    // 时间
    private String gztime;
}
