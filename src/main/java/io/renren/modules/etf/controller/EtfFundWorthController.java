package io.renren.modules.etf.controller;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.etf.danjuan.worth.DanJuanWorthInfo;
import io.renren.modules.etf.danjuan.worth.Item;
import io.renren.modules.etf.entity.EtfFundWorthEntity;
import io.renren.modules.etf.service.EtfFundWorthService;
import io.renren.modules.etf.service.impl.DanJuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-09-23 15:10:56
 */
@RestController
@RequestMapping("generator/etffundworth")
public class EtfFundWorthController {
    @Autowired
    private EtfFundWorthService etfFundWorthService;


    @Autowired
    private DanJuanService danJuanService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam String fundNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("fund_no", fundNo);
        PageUtils page = etfFundWorthService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam String fundNo) {
        if (!etfFundWorthService.isExit(fundNo)) {
            DanJuanWorthInfo fundWorth = danJuanService.getFundWorth(fundNo, null);
            List<Item> items = fundWorth.getData().getItems();
            if (!CollectionUtils.isEmpty(items)) {
                List<EtfFundWorthEntity> collect = items.stream().map(u -> new EtfFundWorthEntity().setFundDate(u.getDate()).setFundNo(fundNo).setPercentage(u.getPercentage()).setWorth(u.getValue())).collect(Collectors.toList());
                etfFundWorthService.saveBatch(collect);
            }
        }
        return R.ok();
    }

    @RequestMapping("/maximumDrawdown")
    public String maximumDrawdown(@RequestParam String fundNo) {
        List<EtfFundWorthEntity> worthEntityList = etfFundWorthService.getListByFundNo(fundNo);
        if (CollectionUtils.isEmpty(worthEntityList)) {
            return "未导入基金净值，请先导入基金净值";
        }
        worthEntityList = worthEntityList.stream().sorted(Comparator.comparing(EtfFundWorthEntity::getFundDate).reversed()).collect(Collectors.toList());
        BigDecimal maximum=new BigDecimal(0);
        for (EtfFundWorthEntity wo : worthEntityList) {
            BigDecimal add = maximum.add(wo.getPercentage());
        }
        return "";
    }

}
