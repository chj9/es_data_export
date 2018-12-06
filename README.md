# About
该工具实现从ES中导出数据,并且可以对导出的数据格式和数据文件做部分自定义(后面支持更多的自定义),该工具主要使用ES中srcoll接口多线程导出数据.

# 流程图

# Version

##V1.2.1

&nbsp;&nbsp;1、优化写文件操作,使用队列。

##V1.2.2

&nbsp;&nbsp;1、重构代码,取消自己写的HTTP请求，使用官方RestClient工具。
&nbsp;&nbsp;2、支持多线程拉取数据。

##V1.0.1

&nbsp;&nbsp;1、实现单线程导出数据。

# Supported
| Elasticsearch version        | support   |
| --------   | -----:  | 
| 6.5.2     | yes |
| 6.5.1       |  yes   | 
| 6.5.0        |    yes    | 
| 6.4.3     | yes |
| 6.4.2        |   yes   | 
| 6.4.1        |    yes    | 
| 6.4.0     | yes |
| 6.3.2        |   yes   | 
| 6.3.1        |    yes    | 
| 6.3.0        | yes      |
| 6.2.4        |   yes   | 
| 6.2.3        |   yes    | 
| 6.2.2        |   yes    | 
| 6.2.1        |   yes    | 
| 6.2.0        |   yes    | 
| 6.1.4        |   yes   | 
| 6.1.3        |   yes    | 
| 6.1.2        |   yes    | 
| 6.1.1        |   yes    | 
| 6.1.0        |   yes    | 
| 6.0.1        |   yes    | 
| 6.0.0        |   yes    | 
| 5.x          |   not test| 
| 2.x          |   not test | 

# Running
```
$git clone git://github.com/760515805/es_data_export.git
$cd es_data_export
$ant 
$cd build
$vim global.properties
$./run.sh
```
切记修改global.properties文件

# Development
## 1、运行环境
- IDE：IntelliJ IDEA
- 项目构建工具：Maven

## 2、初始化项目
- 打开IntelliJ IDEA，将项目导入
- 修改global.properties文件配置
- 运行App.java执行

