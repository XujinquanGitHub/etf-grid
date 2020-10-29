
package io.renren.modules.etf.xueqiu.stock;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Quote {

    private double amount;
    private double amplitude;
    private double avgPrice;
    private double chg;
    private String code;
    private String currency;
    private double current;
    private Object currentExt;
    private double currentYearPercent;
    private long delayed;
    private double dividend;
    private double dividendYield;
    private double eps;
    private String exchange;
    private long floatMarketCapital;
    private long floatShares;
    private Object goodwillInNetAssets;
    private long high;
    private long high52w;
    private Object isRegistration;
    private Object isRegistrationDesc;
    private Object isVie;
    private Object isVieDesc;
    private long issueDate;
    private double lastClose;
    private double limitDown;
    private double limitUp;
    private Object lockSet;
    private long lotSize;
    private double low;
    private double low52w;
    private long marketCapital;
    private String name;
    private double navps;
    private Object noProfit;
    private Object noProfitDesc;
    private double open;
    private double pb;
    private double peForecast;
    private double peLyr;
    private double peTtm;
    private double percent;
    private double pledgeRatio;
    private double profit;
    private long profitForecast;
    private double profitFour;
    private Object securityStatus;
    private long status;
    private String subType;
    private String symbol;
    private double tickSize;
    private long time;
    private long timestamp;
    private Object timestampExt;
    private long totalShares;
    private Object tradedAmountExt;
    private double turnoverRate;
    private long type;
    private long volume;
    private Object volumeExt;
    private double volumeRatio;
    private Object weightedVotingRights;
    private Object weightedVotingRightsDesc;

}
