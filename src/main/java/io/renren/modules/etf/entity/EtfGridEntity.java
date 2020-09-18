package io.renren.modules.etf.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 *
 * @date 2020-09-18 17:06:22
 */
@Data
@TableName("etf_grid")
public class EtfGridEntity implements Serializable {
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
	private BigDecimal buyCost;
	/**
	 * 
	 */
	private BigDecimal sellAmount;
	/**
	 * 
	 */
	private BigDecimal sellCost;
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
	private Date createTime;

}
