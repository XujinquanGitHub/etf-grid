package io.renren.modules.etf;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-09-27 10:54
 **/
@Data
@Accessors(chain = true)
public class StockModel {

    private String industryName;

    private String stockName;

    private String stockCode;

}
