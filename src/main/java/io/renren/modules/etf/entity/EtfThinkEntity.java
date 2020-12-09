package io.renren.modules.etf.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @date 2020-12-09 13:51:29
 */
@Data
@TableName("etf_think")
public class EtfThinkEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 思考内容
	 */
	private String context;
	/**
	 * 当日涨跌情况
	 */
	private String upsDowns;

}
