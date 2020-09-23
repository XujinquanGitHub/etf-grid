package io.renren.modules.etf.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-09-23 15:10:56
 */
@Data
@TableName("etf_fund_worth")
@Accessors(chain = true)
public class EtfFundWorthEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String fundNo;
	/**
	 * 
	 */
	private Date fundDate;
	/**
	 * 
	 */
	private BigDecimal worth;
	/**
	 * 
	 */
	private BigDecimal percentage;

}
