package io.renren.modules.etf.service.impl;

import io.renren.modules.etf.dao.EtfOperationDao;
import io.renren.modules.etf.entity.EtfOperationEntity;
import io.renren.modules.etf.service.EtfOperationService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;



@Service("etfOperationService")
public class EtfOperationServiceImpl extends ServiceImpl<EtfOperationDao, EtfOperationEntity> implements EtfOperationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfOperationEntity> page = this.page(
                new Query<EtfOperationEntity>().getPage(params),
                new QueryWrapper<EtfOperationEntity>()
        );

        return new PageUtils(page);
    }

}