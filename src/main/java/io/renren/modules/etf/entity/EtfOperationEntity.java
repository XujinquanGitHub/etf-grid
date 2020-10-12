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
 * @date 2020-10-12 10:36:07
 */
@Data
@TableName("etf_operation")
public class EtfOperationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private Integer gridId;
	/**
	 * 0 卖出  1、买入
	 */
	private Integer operationType;
	/**
	 * 操作时间
	 */
	private Date createTime;
	/**
	 * 操作金额
	 */
	private BigDecimal amount;

}
