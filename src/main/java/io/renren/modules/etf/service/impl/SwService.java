package io.renren.modules.etf.service.impl;

import io.renren.modules.etf.StockModel;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-09-27 10:00
 **/
@Service
public class SwService {

    private List<StockModel> stockModels = new ArrayList<>();

    public List<StockModel> getAllIndustryIndexes() throws Exception {
        if (!CollectionUtils.isEmpty(stockModels)) {
            return stockModels;
        }

        //用流的方式先读取到你想要的excel的文件
        FileInputStream fis = new FileInputStream(new File(System.getProperty("user.dir") + "/" + "SwClass.xls"));
        //解析excel
        POIFSFileSystem pSystem = new POIFSFileSystem(fis);
        //获取整个excel
        HSSFWorkbook hb = new HSSFWorkbook(pSystem);
        //获取第一个表单sheet
        HSSFSheet sheet = hb.getSheetAt(0);
        //获取第一行
        int firstrow = sheet.getFirstRowNum();
        //获取最后一行
        int lastrow = sheet.getLastRowNum();
        //循环行数依次获取列数
        for (int i = firstrow; i < lastrow + 1; i++) {
            //获取哪一行i
            Row row = sheet.getRow(i);
            if (row != null) {
                //获取这一行的第一列
                int firstcell = row.getFirstCellNum();
                //获取这一行的最后一列
                int lastcell = row.getLastCellNum();
                //创建一个集合，用处将每一行的每一列数据都存入集合中
                List<String> list = new ArrayList<>();
                for (int j = firstcell; j < lastcell; j++) {
                    //获取第j列
                    Cell cell = row.getCell(j);

                    if (cell != null) {
                        list.add(cell.toString());
                    }
                }

                StockModel stockModel = new StockModel();
                if (list.size() > 0) {
                    stockModel.setIndustryName(list.get(0));
                    stockModel.setStockCode(list.get(1));
                    stockModel.setStockName(list.get(2));
                }
                stockModels.add(stockModel);
            }
        }
        fis.close();
        return stockModels;

    }


}
