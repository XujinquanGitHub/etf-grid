package io.renren.modules.etf;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-10-16 14:33
 **/
@Data
public class TranslateModel {

    // 原基金代码
    private String sourceFundNo;

    private String sourceFundName;

    // 原基金需要转换的份额
    private BigDecimal sourceNum;

    private String targetFundNo;

    private String targetFundName;

    // 目标基金需要买入的金额
    private BigDecimal amount;


}
