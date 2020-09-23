package io.renren.modules.etf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.etf.entity.EtfFundWorthEntity;

import java.util.Map;

/**
 * 
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-09-23 15:10:56
 */
public interface EtfFundWorthService extends IService<EtfFundWorthEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean isExit(String fundNo);
}

