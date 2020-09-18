package io.renren.modules.etf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.etf.entity.EtfGridEntity;

import java.util.Map;

/**
 * 
 *
 *
 * @date 2020-09-18 17:06:22
 */
public interface EtfGridService extends IService<EtfGridEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

