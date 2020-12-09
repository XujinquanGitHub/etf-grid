package io.renren.modules.etf.service.impl;

import io.renren.modules.etf.dao.EtfThinkDao;
import io.renren.modules.etf.entity.EtfThinkEntity;
import io.renren.modules.etf.service.EtfThinkService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;


@Service("etfThinkService")
public class EtfThinkServiceImpl extends ServiceImpl<EtfThinkDao, EtfThinkEntity> implements EtfThinkService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfThinkEntity> page = this.page(
                new Query<EtfThinkEntity>().getPage(params),
                new QueryWrapper<EtfThinkEntity>()
        );

        return new PageUtils(page);
    }

}