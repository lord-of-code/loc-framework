package com.loc.framework.autoconfigure.mybatis;

import java.util.Map;
import lombok.Data;

/**
 * Created on 2018/1/13.
 */
@Data
public class LocMybatisProperties {

  private Map<String, MybatisProperties> dataSource;
}
