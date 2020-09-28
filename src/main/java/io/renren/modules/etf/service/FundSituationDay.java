package io.renren.modules.etf.service;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @program: etf-grid
 * @description: 基金当天情况
 * @author: 许金泉
 * @create: 2020-09-27 18:19
 **/
@Data
@Accessors(chain = true)
public class FundSituationDay {

    private String fundName;

    private String fundNo;

    private BigDecimal makeMoney;

    private BigDecimal fundGains;

    private BigDecimal fundAmount;

    @Override
    public String toString() {
        return " 赚钱:" + addForNum(15, makeMoney.toString()) + ", 基金号:" + addForNum(8, fundNo) + ", 涨幅:" + addForNum(15, fundGains.toString()) + ", 基金开盘价值:" + addForNum(20, fundAmount.toString())+ ",基金名:" + addForNum(35, fundName) ;
    }

    public String addForNum(int strLength, String str) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append(str).append(" ");
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }
}
