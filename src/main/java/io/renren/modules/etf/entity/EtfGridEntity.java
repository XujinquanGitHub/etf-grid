package io.renren.modules.etf.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * 
 *
 * @date 2020-09-18 17:06:22
 */
@Data
@TableName("etf_grid")
@Accessors(chain = true)
public class EtfGridEntity extends Model<EtfGridEntity> implements Serializable {
	private static final long serialVersionUID = 1L;



	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private Integer planId;


	// 基金名
	private String name;

	// 基金代码
	private String fundNo;

	/**
	 * 
	 */
	private BigDecimal num;
	/**
	 * 
	 */
	private BigDecimal buyPrice;
	/**
	 * 
	 */
	private BigDecimal sellPrice;
	/**
	 * 
	 */
	private BigDecimal buyAmount;

	/**
	 * 
	 */
	private BigDecimal sellAmount;

	/**
	 * 
	 */
	private BigDecimal profit;
	/**
	 * 
	 */
	private BigDecimal profitRate;
	/**
	 * 
	 */
	private Date buyTime;

	private Date sellTime;

	//0、计划买入 1、买入 2、待卖出 3、已卖出
	private Integer status;

	private String outId;


}
