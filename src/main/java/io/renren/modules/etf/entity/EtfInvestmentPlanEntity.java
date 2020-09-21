package io.renren.modules.etf.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @date 2020-09-18 17:06:22
 */
@Data
@TableName("etf_investment_plan")
public class EtfInvestmentPlanEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 投资计划名
	 */
	private String name;
	/**
	 * 基金名称
	 */
	private String fundName;
	/**
	 * 基金号
	 */
	private String fundNo;
	/**
	 * 投资基金的初始净值
	 */
	private BigDecimal initPrice;
	/**
	 * 基金当前净值
	 */
	private BigDecimal currentPrice;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 初始单次投入金额
	 */
	private BigDecimal singleAmount;
	/**
	 * 涨幅度到多少时通知用户
	 */
	private BigDecimal riseRange;
	/**
	 * 跌幅度到多少时通知用户
	 */
	private BigDecimal fallRange;
	/**
	 * 接收通知的邮件
	 */
	private String receiveMail;
	/**
	 * 每个交易日检查时间
	 */
	private Time inspectTime;

}
