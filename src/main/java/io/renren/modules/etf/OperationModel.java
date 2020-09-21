package io.renren.modules.etf;

import io.renren.modules.etf.entity.EtfGridEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-09-21 17:46
 **/
@Data
@Accessors(chain = true)
public class OperationModel extends EtfGridEntity {

    private String operationString;

}
