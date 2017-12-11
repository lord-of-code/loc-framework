[![Build Status](https://travis-ci.org/lord-of-code/loc-framework.svg?branch=master)](https://travis-ci.org/lord-of-code/loc-framework)
[![codecov](https://codecov.io/gh/lord-of-code/loc-framework/branch/master/graph/badge.svg)](https://codecov.io/gh/lord-of-code/loc-framework)

# loc-framework
本项目是完全基于Spring Boot2和Springcloud Finchley所进行了开发的，目的是简化和统一公司内部使用微服务框架的使用方法

## 目的
通过一定的规则来统一编写各种starter，方便在企业内部进行规则的统一，配置的统一，用法的统一，监控的统一，部署的统一
方便再进一步的简化springboot和springcloud的使用

## 计划主要的starter
- springmvc
- logger
- jdbc
- redis
- eureka
- ...



## springmvc的starter的统一标准

* 添加了Filter，增加了http请求的accesslog的记录，记录的request和response的关键信息
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
5**  服务端错误，如Runtime Exception错误 等
```

## logger的starter的统一标准


## jdbc的starter的统一标准


## redis的starter的统一标准


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
