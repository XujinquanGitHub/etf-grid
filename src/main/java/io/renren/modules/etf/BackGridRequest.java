package io.renren.modules.etf;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-10-29 18:12
 **/
@Data
public class BackGridRequest {

    private String fundNo;

    private String startDay;

    // 每一网投入多少
    private BigDecimal gridPrice;

    // 每下坠几个百分点加入一网
    private BigDecimal gridPercentage;

    // 每网上升几个百分点卖出
    private BigDecimal gridFailPercentage;

    private BigDecimal investmentAmount;


}
