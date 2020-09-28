package io.renren.modules.etf.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.renren.modules.etf.dao.EtfGridDao;
import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.service.EtfGridService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import org.springframework.util.CollectionUtils;


@Service("etfGridService")
public class EtfGridServiceImpl extends ServiceImpl<EtfGridDao, EtfGridEntity> implements EtfGridService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfGridEntity> page = this.page(new Query<EtfGridEntity>().getPage(params), new QueryWrapper<EtfGridEntity>());

        return new PageUtils(page);
    }

    @Override
    public List<EtfGridEntity> queryList(Map<String, Object> params) {
        return listByMap(params);
    }

    @Override
    public List<EtfGridEntity> queryListByFundName(String fundName) {
        Map<String, Object> params = new HashMap<>();
        params.put("fund_name", fundName);
        return listByMap(params);
    }

    public boolean buy(EtfGridEntity etfGridEntity) {
        if (etfGridEntity.getPlanId()==null) {
            return false;
        }
        if (StringUtils.isNotBlank(etfGridEntity.getOutId())) {
            QueryWrapper<EtfGridEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("out_id", etfGridEntity.getOutId());
            if (count(queryWrapper) > 0) {
                return false;
            }
        }
        QueryWrapper<EtfGridEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buy_price", etfGridEntity.getBuyPrice());
        queryWrapper.eq("buy_amount", etfGridEntity.getBuyAmount());
        List<EtfGridEntity> list = list(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            EtfGridEntity update = new EtfGridEntity();
            update.setId(list.get(0).getId()).setBuyTime(etfGridEntity.getBuyTime()).updateById();
            return false;
        }
        etfGridEntity.setStatus(1);
        if (etfGridEntity.getNum() == null) {
            etfGridEntity.setNum(etfGridEntity.getBuyAmount().divide(etfGridEntity.getBuyPrice(), 2, BigDecimal.ROUND_HALF_UP));
        }
        return etfGridEntity.insert();
    }

}