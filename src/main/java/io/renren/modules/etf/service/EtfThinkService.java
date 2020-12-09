package io.renren.modules.etf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.etf.entity.EtfThinkEntity;

import java.util.Map;

/**
 * @date 2020-12-09 13:51:29
 */
public interface EtfThinkService extends IService<EtfThinkEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

