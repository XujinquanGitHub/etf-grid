package io.renren.modules.etf.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * @date 2020-09-18 17:06:22
 */
@Data
@Accessors(chain = true)
@TableName("etf_investment_plan")
public class EtfInvestmentPlanEntity extends Model<EtfInvestmentPlanEntity> implements Serializable {
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

	/**
	 * 买入计算点 1、按最后一次买入做为参考点 2、按买入记录的最低点做为参考
	 */
	private Integer buyType;

	/**
	 * 操作方式 1、正常买卖 2、只买不卖 3、只卖不买 4、终止计划,
	 */
	private Integer planOperationType;

	/**
	 * 基金对应的指数号
	 */
	private String indexNo;

	private Date watchDate;

	private String accountDesc;


	private String venueNo;


}
