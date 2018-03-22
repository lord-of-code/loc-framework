[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.lord-of-code/loc-framework-build/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.lord-of-code/loc-framework-build)
[![Build Status](https://travis-ci.org/lord-of-code/loc-framework.svg?branch=master)](https://travis-ci.org/lord-of-code/loc-framework)
[![codecov](https://codecov.io/gh/lord-of-code/loc-framework/branch/master/graph/badge.svg)](https://codecov.io/gh/lord-of-code/loc-framework)

# loc-framework
本项目是完全基于Spring Boot 2.0版本和Spring Cloud Finchley所进行了开发的
目的是简化和统一微服务框架Spring Boot 2和Spring Cloud的使用方法

## 目的
通过一定的规则来统一编写各种依赖的Starter，方便在企业内部进行规则的统一，配置的统一，用法的统一，监控的统一，部署的统一
方便再进一步的简化Spring Boot和Spring Cloud的使用，进一步分离通用框架代码和业务代码的耦合

## 原因
Spring Boot 2目前版本是RELEASE版本
对于之前使用Spring Boot 1.x的同学如果要进行升级到Spring Boot 2的话，需要改动的地方还是很多的

## Spring Boot 2主要改动
1. Spring Boot 2是完全基于java8，这也就证明了java8已经被大家全面接受和普及， 虽然java9也已经发布， 但是java9的普及可能还需要一段时间，但是现在Spring Boot 2也同时对java9做了一些支持。
2. http请求方面， 引入了Webflux， 他是基于Spring Webflux， 它是一个新的非堵塞函数式 Reactive Web 框架，可以用来建立异步的，非阻塞，事件驱动的服务，并且扩展性非常好。性能对比于之前的同步方式有了一定的提高
3. db方面，默认引入了HikariCP，替代了之前的tomcat-pool作为底层的数据库连接池， 对比于tomcat-pool， HikariCP拥有更好的性能，总而言之就是提高了db的访问速度。
4. redis方面， 默认引入了Lettuce, 替代了之前的jedis作为底层的redis链接方式， 同样Lettuce底层基于netty框架，使用异步的方式，访问redis，并且如果结合之前的Webflux, 可以达成请求的全异步， 同样对比于之前的jedis，统一了redis和redis-cluster的访问方式，简化了开发人员的使用方式，同时也提高了redis的访问速度
5. es方面，默认也从之前的支持es2升级到了es5+, es5也出来了一段时间， 大部分的人应该也是通过自己实现来完成es5的对接， 现在springboot2也是进行了es5的支持。
6. metrics方面，Spring Boot 2引入了Micrometer，来统一metrics的规范，使得开发人员更好的理解和使用metrics的模块，而不需要关心对接的具体存储是什么东西。
7. Spring Boot 2 同时也加入了 对于OAuth 2.0的支持， 使得开发人员更加友好的和方面的使用spring-security来完成权限模块的开发

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

- pom.xml中指定仓库地址(因为Spring Boot 2在中央仓库还没有，所以需要指定仓库)
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

## 提供的核心依赖(SpringBoot和SpringCloud)和辅助依赖

- SpringBoot 2.0.0.RELEASE
- SpringCloud Release Finchley.M8
- springfox-swagger2  2.8.0
- log4jdbc 1.16
- mybatis 3.4.5
- mybatis-spring 1.3.1
- mybatis-pagehelper 5.1.2
- elasticjob 2.1.5

## demo程序

具体代码可以参考

- [具体的demo](https://github.com/lord-of-code/loc-framework-demo)

## 利用docker-compose启动基础依赖

#### mysql
docker-compose -f docker/mysql.yml up -d
端口为 3306

#### redis
docker-compose -f docker/redis.yml up -d
端口为 6379

#### zk
docker-compose -f docker/zk.yml up -d
端口为 2181, 2182, 2183

#### kafka
docker-compose -f docker/kafka.yml up -d
端口为 9092

#### elasticsearch
docker-compose -f docker/es.yml up -d
端口为 9200

#### elasticjob
docker-compose -f docker/job.yml up -d
端口为 8899 用户名 root 密码 root123

#### zipkin-server
docker-compose -f docker/zipkin.yml up -d
端口为 9411

#### keycloak
docker-compose -f docker/keycloak.yml up -d
端口为 7777

#### sentry
docker-compose -f docker/sentry.yml run --rm web upgrade #升级数据库
docker-compose -f docker/sentry.yml up -d   #启动sentry服务
端口为 9000

#### spring-cloud的基础环境搭建步骤
[loc-cloud-component](https://github.com/lord-of-code/loc-cloud-component)
里面主要包括了`eureka`、`config`、`sleuth`、`zuul`等基础组件的docker部署

## 计划主要的starter
- springmvc
- logger
- shutdown
- jdbc
- mybatis
- okhttp
- redis
- kafka
- keycloak
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

* 请求日志的相关配置
```
loc.web.springmvc.log:
  enabled: true     # 是否启用access log
  includeRequest: true    # 记录请求
  includeResponse: false  # 记录返回
  requestBodyLength: 8192  # 记录日志请求长度
  responseBodyLength: 8192 # 记录日志返回长度
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
  sqlonly: 'OFF'
  sqltiming: INFO
  audit: 'OFF'
  resultset: 'OFF'
  resultsettable: 'OFF'
  connection: 'OFF'
```

## mybatis的starter的统一标准

* mybatis的starter依赖了jdbc的starter, 所以一些配置是使用jdbc的配置

* mybatis的starter同样支持多数据源的配置

* mybatis的starter支持xml配置文件和注解的两种配置方式具体使用方式可以参考samples里面的:

1. [mybatis-xml例子](https://github.com/lord-of-code/loc-framework/tree/master/loc-samples/loc-framework-mybatis-xml-sample)
2. [mybatis-annotation例子](https://github.com/lord-of-code/loc-framework/tree/master/loc-samples/loc-framework-mybatis-annotation-sample)

* myabtis的starter新增了和数据源相关的配置, 默认值都为null
```
loc.dataSource:
  firstDs:
    configLocation:            # datasource对应的mybatis-config.xml文件
    basePackage:               # 接口所在的项目包
    mapperLocations:           #
    typeAliasesPackage:        #
    executorType:              #
    configurationProperties:   #
    configuration:             #
```

* mybatis的starter新增了分页插件的相关的全局配置
```
loc.pagehelper:
  helperDialect = "mysql";
  offsetAsPageNum = false;
  rowBoundsWithCount = false;
  pageSizeZero = false;
  reasonable = false;
  params = "pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=reasonable;pageSizeZero=pageSizeZero";
  supportMethodsArguments = false;
  autoRuntimeDialect = false;
  closeConn = true;
```

## okhttp的starter的统一标准

* 加入了okhttp的client的构造，用于发起http请求，并且用于记录请求和回应的日志

```
loc.okhttp:
  connectTimeout: 5000
  readTimeout: 30000
  writeTimeout: 30000
  retryOnConnectionFailure: true
  followRedirects: true
  followSslRedirects: true
  connection:
    maxIdleConnections: 5
    keepAliveDuration: 60000
  level: BASIC       # 记录请求日志级别
```

## redis的starter的统一标准

* 修改了一下cache manager的序列化方式, 把默认的value的序列化方式从jdk的序列化方式改成了json的序列化方式, 主要是为了方便redis存储数据的可读性
* 重新封装了cache manager的使用方式，对于redis不可用的时候，还可以继续进行后续操作，认为缓存是可能出问题的，但是不会影响具体的业务逻辑操作

* 下列事例配置参数
```
spring:
  cache:
    redis:
      time-to-live: 100s
  redis:
    host: 127.0.0.1
    port: 6379
    timeout: 2s   #操作超时的情况，默认60s
#  cluster.nodes:
#    - 10.10.3.51:2001
#    - 10.10.3.51:2002
#    - 10.10.3.51:2003
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        max-wait: "1000"
```

## kafka的starter的统一标准

* 用kafka来作为主要的的消息队列
* 统一定义k,v 使用 byte[] 数组来定义消息的key和value，方便统一处理
* 给producer在发送的时候头里面加上了messageid、trace相关的信息、记录发送的日志和进行json转换，
* 给consumer在处理消息错误的时候记录日志，处理完消息的时候记录日志，接受到消息的时候记录日志、处理trace的信息，对消息进行json转换等功能

## elasticjob的starter的统一标准

* 利用elasticjob组件完成分布式的调度
* 提供注解来进行任务的配置

## sentry的配置
* 利用sentry来发送error日志
* 通过配置sentry.dsn来指定dsn的地址，然后对于error日志会直接发送到sentry的服务端

```
sentry.dsn: http://public:private@host:port/1
```

## keycloak的starter的统一标准

* keycloak是一个开源的统一认证的安全框架, 现在由于框架是基于springboot2进行开发的，keycloak-springboot-adapter的是基于springboot 1.x的版本来做的，所以出现了无法使用的情况，这个问题已经上报给keycloak官方了
* 等待keycloak针对springboot2提供了adapter后会继续进行跟进此模块

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

* 如果使用windows系统，编译的时候需要跳过测试
```
$ mvn clean install -Dmaven.test.skip=true
```


# License
Loc Framework is Open Source software released under the MIT license.
