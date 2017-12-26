package com.loc.framework.autoconfigure.jdbc;


import java.util.function.Supplier;
import lombok.Data;

/**
 * Created on 2017/12/26.
 */
@Data
public class JdbcProperties {

  public static final Supplier<JdbcPoolProperties> JDBC_POOL = JdbcPoolProperties::new;
  private String username;
  private String password;
  private String jdbcUrl;
  private JdbcPoolProperties jdbcPool = JDBC_POOL.get();
}
