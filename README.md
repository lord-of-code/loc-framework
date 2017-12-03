[![Build Status](https://travis-ci.org/lord-of-code/loc-framework.svg?branch=master)](https://travis-ci.org/lord-of-code/loc-framework)

# loc-framework
本项目是完全基于Spring Boot2和Springcloud Finchley所进行了开发的，目的是简化和统一公司内部使用微服务框架的使用方法

## 目的
通过一定的规则来统一编写各种starter，方便在企业内部进行规则的统一，配置的统一，用法的统一，监控的统一，部署的统一
方便再进一步的简化springboot和springcloud的使用

## 计划主要的starter
- logger
- springmvc
- jdbc
- redis
- eureka
- ...


## logger的starter的统一标准


## springmvc的starter的统一标准


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
