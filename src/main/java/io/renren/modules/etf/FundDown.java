package io.renren.modules.etf;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-10-20 10:33
 **/
@Data
@Accessors(chain = true)
public class FundDown {

    private String fundNo;

    private String fundName;

    private String desc;

    private BigDecimal down;

}
