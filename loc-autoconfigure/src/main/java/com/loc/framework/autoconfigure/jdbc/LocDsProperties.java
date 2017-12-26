package com.loc.framework.autoconfigure.jdbc;

import java.util.Map;
import lombok.Data;

/**
 * Created on 2017/12/26.
 */
@Data
public class LocDsProperties {

  private Map<String, JdbcProperties> locDs;
}
