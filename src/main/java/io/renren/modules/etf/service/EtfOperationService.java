package io.renren.modules.etf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.etf.entity.EtfOperationEntity;

import java.util.Map;

/**
 * 
 *
 * @date 2020-10-12 10:36:07
 */
public interface EtfOperationService extends IService<EtfOperationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

