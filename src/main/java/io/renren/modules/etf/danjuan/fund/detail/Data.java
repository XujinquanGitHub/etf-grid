
package io.renren.modules.etf.danjuan.fund.detail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.experimental.Accessors;

@lombok.Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Data {

    private String fundCompany;
    private FundDateConf fundDateConf;
    private FundPosition fundPosition;
    private FundRates fundRates;
    private List<ManagerList> managerList;

    private Map<String, BigDecimal> industryProportion=new HashMap<>();

}