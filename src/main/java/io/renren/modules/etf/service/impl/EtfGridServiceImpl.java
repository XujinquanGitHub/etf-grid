package io.renren.modules.etf.service.impl;

import io.renren.modules.etf.dao.EtfGridDao;
import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.service.EtfGridService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;



@Service("etfGridService")
public class EtfGridServiceImpl extends ServiceImpl<EtfGridDao, EtfGridEntity> implements EtfGridService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfGridEntity> page = this.page(
                new Query<EtfGridEntity>().getPage(params),
                new QueryWrapper<EtfGridEntity>()
        );

        return new PageUtils(page);
    }

}