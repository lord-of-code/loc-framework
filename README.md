[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.lord-of-code/loc-framework-build/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.lord-of-code/loc-framework-build)
[![Build Status](https://travis-ci.org/lord-of-code/loc-framework.svg?branch=master)](https://travis-ci.org/lord-of-code/loc-framework)
[![codecov](https://codecov.io/gh/lord-of-code/loc-framework/branch/master/graph/badge.svg)](https://codecov.io/gh/lord-of-code/loc-framework)

# loc-framework
本项目是完全基于Spring Boot2和Springcloud Finchley所进行了开发的，目的是简化和统一公司内部使用微服务框架的使用方法

## 目的
通过一定的规则来统一编写各种starter，方便在企业内部进行规则的统一，配置的统一，用法的统一，监控的统一，部署的统一
方便再进一步的简化springboot和springcloud的使用

## 使用方式
- pom.xml中加入parent
```
<parent>
    <groupId>com.github.lord-of-code</groupId>
    <artifactId>loc-framework-starter-parent</artifactId>
    <version>0.1.1.RELEASE</version>
    <relativePath/>
  </parent>
```

- pom.xml中指定仓库地址(因为springboot2在中央仓库还没有，所以需要指定仓库)
```
<repositories>
  <repository>
    <id>spring-snapshots</id>
    <name>Spring Snapshots</name>
    <url>https://repo.spring.io/snapshot</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
  <repository>
    <id>spring-milestones</id>
    <name>Spring Milestones</name>
    <url>https://repo.spring.io/milestone</url>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
</repositories>

<pluginRepositories>
  <pluginRepository>
    <id>spring-snapshots</id>
    <name>Spring Snapshots</name>
    <url>https://repo.spring.io/snapshot</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </pluginRepository>
  <pluginRepository>
    <id>spring-milestones</id>
    <name>Spring Milestones</name>
    <url>https://repo.spring.io/milestone</url>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </pluginRepository>
</pluginRepositories>
```
- pom.xml加入相关插件(插件打出的uber.jar可以直接通过`java -jar uber.jar`来启动)
```
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
    </plugin>
  </plugins>
</build>
```


## demo程序

具体代码可以参考

- [具体的demo](https://github.com/lord-of-code/loc-framework-demo)



## 计划主要的starter
- springmvc
- logger
- shutdown
- jdbc
- okhttp
- eureka
- ...



## springmvc的starter的统一标准

* 增加了http请求的accesslog的记录的Filter，记录的request和response的关键信息
* 添加了ExceptionHandler，增加了异常情况的处理
* 添加了统一的Response返回的数据格式

```
  private int code;          #返回的状态码
  private String msg;        #返回的信息，用于前端进行展示
  private String detailMsg;  #返回的详细信息，用户内部调试和排查问题
  private T data;            #返回的具体数据(泛型)
```

* 添加了统一的业务异常类

```
  LocCommonException
``` 

对于抛出这种异常框架会进行转换，这种异常为业务异常，返回的response status为200, 返回的code码可以根据业务情况自行定义

* response状态码大类分类

```
2xx  正常返回, 通过LocCommonException抛出的异常
4xx  客户端请求失败，如http method错误，参数错误，MediaType错误 等
5xx  服务端错误，如Runtime Exception错误 等
```

* 加入了cors跨域相关的Filter的配置
```
loc.web.springmvc.cors.enabled: false   #默认为false，默认不开启cors跨域的配置, 如果要开启，改成true
loc.web.springmvc.cors.allowOrigins:    #默认为*, 可以通过数组进行添加
  - ...
  - ...
loc.web.springmvc.cors.allowHeaders:    #默认为*, 可以通过数组进行添加
  - ...
  - ...
loc.web.springmvc.cors.allowMethods:    #默认为*, 可以通过数组进行添加
  - GET
  - POST
  - ...
loc.web.springmvc.cors.allowExposeHeaders:   #默认没有，可以通过数组进行添加
  - ...
  - ...
```

* 加入了swagger的相关配置
```
loc.web.springmvc.swagger2.enabled: true  #默认为true, 表示是否开启swagger功能

loc.web.springmvc.swagger2.apiinfo.title = "project title";
loc.web.springmvc.swagger2.apiinfo.description = "project description";
loc.web.springmvc.swagger2.apiinfo.version = "v1";
loc.web.springmvc.swagger2.apiinfo.termsOfServiceUrl = "https://github.com/lord-of-code/loc-framework";
loc.web.springmvc.swagger2.apiinfo.contactName = "loc project";
loc.web.springmvc.swagger2.apiinfo.contactUrl = "https://github.com/lord-of-code/loc-framework";
loc.web.springmvc.swagger2.apiinfo.contactEmail = "dragonlong1986@126.com";
loc.web.springmvc.swagger2.apiinfo.license = "project license";
loc.web.springmvc.swagger2.apiinfo.licenseUrl = "https://github.com/lord-of-code/loc-framework";

loc.web.springmvc.swagger2.restapi.basePackage   #扫描的包路径，默认为all
loc.web.springmvc.swagger2.restapi.paths         #扫描的paths，默认为all
```

## logger的starter统一日志标准(依赖logback日志框架)

* 统一了logger的输出格式，主要考虑到后续的日志采集、统计、分析的统一
```
[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%t] %-5level %logger{50} - %msg%n
```
* 默认了logger的输出，分为console和file，默认的file存储在`/tmp/loc.log`目录，可以通过配置文件`logging.file`进行修改
* 日志按小时进行切割，切割后存放目录，默认是`./logs`目录，可以通过`logging.path`进行修改
* 日志文件备份默认存放720小时(30天)


## shutdown的starter来进行优雅的停机操作

* 完成了tomcat的优雅停机策略

```
loc.tomcat.shutdown.enabled: true  #默认优雅停机策略是打开的
loc.tomcat.shutdown.waitTime: 30   #默认30s的优雅停机等待时间，超过时间强行关闭
```


## jdbc的starter的统一标准

* springboot从2.0开始使用HikariCP作为默认的DataSource
关于HikariCP的配置可以参考[官方配置文档](https://github.com/brettwooldridge/HikariCP)

程序启动的时候，通过实现`BeanFactoryPostProcessor`接口，来读取配置文件里面特定的字段来实现多数据源的加载
```
loc.dataSource:
  firstDs:
    username: root
    password:
    jdbcUrl: jdbc:mysql://127.0.0.1:3306/test
    jdbcPool:
      maximumPoolSize: 30
      driverClassName: com.mysql.jdbc.Driver
```
因为都是通过手动进行读取的配置然后加载的，而没有走默认的`DataSourceAutoConfiguration`和`DataSourceTransactionManagerAutoConfiguration`
所以在使用的时候`exclude`排出掉`DataSourceAutoConfiguration.class`和`DataSourceTransactionManagerAutoConfiguration.class`

* 通过利用log4jdbc给HikariCP做相关的日志记录
关于log4jdbc的配置可以参考[log4jdbc定义文件](https://github.com/lord-of-code/loc-framework/blob/master/loc-autoconfigure/src/main/resources/META-INF/spring-configuration-metadata.json)

* 可以通过logging级别来控制数据库日志的输出
```
logging.level.jdbc:
  sqlonly: OFF
  sqltiming: INFO
  audit: OFF
  resultset: OFF
  resultsettable: OFF
  connection: OFF
```

## okhttp的starter的统一标准

* 加入了okhttp的client的构造，用于发起http请求

```
loc.okhttp:
  connectTimeout: 5000
  readTimeout: 30000
  writeTimeout: 30000
  retryOnConnectionFailure: true
  followRedirects: true
  followSslRedirects: true
  Connection:
    maxIdleConnections: 5
    keepAliveDurationNs: 5
  Level:
    level: BASIC       # 记录请求日志级别
```

## eureka的starter的统一标准


# 源码编译
如果你想了解最新的一些功能和改动，可以通过编译源码来进行使用和测试

* 如果使用mvn wrapper来进行编译,你需要确保使用的JDK版本为1.8以上
```
$ ./mvnw clean install
```

* 如果使用mvn来进行编译，你需要确保使用的mvn为Apache Maven v3.5.0 +
```
$ mvn clean install
```


# License
Loc Framework is Open Source software released under the MIT license.
