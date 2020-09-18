package io.renren.modules.etf.controller;

import java.util.Arrays;
import java.util.Map;

import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.service.EtfGridService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 *
 * @date 2020-09-18 17:06:22
 */
@RestController
@RequestMapping("generator/etfgrid")
public class EtfGridController {
    @Autowired
    private EtfGridService etfGridService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:etfgrid:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = etfGridService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:etfgrid:info")
    public R info(@PathVariable("id") Integer id){
		EtfGridEntity etfGrid = etfGridService.getById(id);

        return R.ok().put("etfGrid", etfGrid);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:etfgrid:save")
    public R save(@RequestBody EtfGridEntity etfGrid){
		etfGridService.save(etfGrid);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:etfgrid:update")
    public R update(@RequestBody EtfGridEntity etfGrid){
		etfGridService.updateById(etfGrid);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:etfgrid:delete")
    public R delete(@RequestBody Integer[] ids){
		etfGridService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
