package com.loc.framework.autoconfigure.jdbc;

import lombok.Data;

/**
 * Created on 2017/12/26.
 */
@Data
public class JdbcPoolProperties {

  //配置根据  https://github.com/brettwooldridge/HikariCP  文档来的
  public static final boolean DEFAULT_AUTO_COMMIT = true;
  public static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
  public static final int DEFAULT_IDLE_TIMEOUT = 600000;
  public static final int DEFAULT_MAX_LIFETIME = 1800000;
  public static final int DEFAULT_MAXIMUM_POOL_SIZE = 10;
  public static final int DEFAULT_MINIMUM_IDLE = DEFAULT_MAXIMUM_POOL_SIZE;
  public static final int DEFAULT_INITIALIZATION_FAIL_TIMEOUT = 1;
  public static final boolean DEFAULT_ISOLATE_INTERNAL_QUERIES = false;
  public static final boolean DEFAULT_ALLOW_POOL_SUSPENSION = false;
  public static final boolean DEFAULT_READ_ONLY = false;
  public static final boolean DEFAULT_REGISTER_MBEANS = false;
  public static final String DEFAULT_DRIVER_CLASS_NAME = null;
  public static final int DEFAULT_VALIDATION_TIMEOUT = 5000;
  public static final int DEFAULT_LEAK_DETECTION_THRESHOLD = 0;

  private boolean autoCommit = DEFAULT_AUTO_COMMIT;
  private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
  private int idleTimeout = DEFAULT_IDLE_TIMEOUT;
  private int maxLifetime = DEFAULT_MAX_LIFETIME;
  private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE;
  private int minimumIdle = DEFAULT_MINIMUM_IDLE;
  private int initializationFailTimeout = DEFAULT_INITIALIZATION_FAIL_TIMEOUT;
  private boolean isolateInternalQueries = DEFAULT_ISOLATE_INTERNAL_QUERIES;
  private boolean allowPoolSuspension = DEFAULT_ALLOW_POOL_SUSPENSION;
  private boolean readOnly = DEFAULT_READ_ONLY;
  private boolean registerMbeans = DEFAULT_REGISTER_MBEANS;
  private String driverClassName = DEFAULT_DRIVER_CLASS_NAME;
  private int validationTimeout = DEFAULT_VALIDATION_TIMEOUT;
  private int leakDetectionThreshold = DEFAULT_LEAK_DETECTION_THRESHOLD;
}
