# About
该工具实现从ES中导出数据,并且可以对导出的数据格式和数据文件做部分自定义(后面支持更多的自定义),该工具主要使用ES中srcoll接口多线程导出数据.

# Design
![Base](https://github.com/760515805/es_data_export/blob/master/docs/design.png)
&nbsp;&nbsp;&nbsp;&nbsp;项目通过两个线程池实现功能,一个线程池主要从ElasticSearch获取数据,获取方式为es接口的Srcoll方式,多线程的话则通过slice切割数据.另一个线程池主要拿来写文件,使用BlockingQueue把数据缓冲到线程队列中，通过一条线程单线程写文件.

# Version
版本号说明:大版本.新增功能.提交次数

## V1.3.5

&nbsp;&nbsp;1.新增ES导数据入DB，支持大部分主流数据库。<br>
&nbsp;&nbsp;1.新增支持导出SQL语句自定义sql。<br>
&nbsp;&nbsp;2.修改配置文件的配置名，利于阅读。<br>
&nbsp;&nbsp;3.修改文件分割策略，以文件大小分割取消以文件数量分割。
&nbsp;&nbsp;4.优化代码。

## V1.2.4

&nbsp;&nbsp;1.新增线程池监控,在数据导出结束后正确停止程序。<br>
&nbsp;&nbsp;2.新增配置启动前验证配置是否正确,设置配置默认值。<br>
&nbsp;&nbsp;3.优化异常日志输出,更好排查问题。

## V1.2.3

&nbsp;&nbsp;1.优化写文件操作,使用BlockingQueue队列缓存。<br>
&nbsp;&nbsp;2.新增支持文件写到一定大小后进行文件切割。<br>
&nbsp;&nbsp;3.新增支持SSL加密获取数据。

## V1.2.2

&nbsp;&nbsp;1.重构代码,取消自己封装的HTTP工具，使用官方RestClient工具。<br>
&nbsp;&nbsp;2.新增支持多线程拉取ES数据。

## V1.0.1

&nbsp;&nbsp;1.实现单线程导出数据。

# Supported
| Elasticsearch version        | support   |
| --------   | -----:  | 
| >= 6.0.0     | yes |
| >= 5.0.0        |   not test| 
| >= 2.0.0        |   not test | 
| <= 1       |   not test |

# Running
```
$git clone git://github.com/760515805/es_data_export.git
$cd es_data_export
```
如果已经安装了ant环境和maven环境则可以使用以下操作
```
$ant 
$cd build
$vim global.properties
$./run.sh
```
如果只安装了maven环境则如下操作
```
$mvn clean package
$cp global.properties run.sh stop.sh logback.xml  target/
$cd target
$vim global.properties
$./run.sh
```
切记修改global.properties文件

# Development
## 1.运行环境
- IDE：IntelliJ IDEA或者Eclipse
- 项目构建工具：Maven

## 2.初始化项目
- 打开IntelliJ IDEA，将项目导入
- 修改global.properties文件配置
- 运行App.java执行

# 配置文件名词解释
## index   
    数据索引
## type
     索引type,无则可留空,ES7.0以后删除
## query
    查询条件DSL,必须为ES的查询语句,可留空,默认:查询全部,条数1000
## includes
    取哪些字段的数据,逗号隔开,如果全部取则设为空
## threadSize
    获取数据线程数据,最大不超过索引的shards数量和CPU数量,默认为1
## esserver
    ES集群IP地址,逗号隔开,如:192.169.2.98:9200,192.169.2.156:9200,192.169.2.188:9200
## esusername
    如有帐号密码则填写,如果无则留空
## espassword
    如有帐号密码则填写,如果无则留空
## isLineFeed
    导出数据写入文件每条数据是否需要换行,默认:true
## dataLayout
    输出源数据形式,目前支持json、txt,下个版本支持sql、excel,如果为txt字段间是用逗号隔开,默认:json
## filePath
    数据输出文件路径,必填字段
## fileName
    输出的文件名,无则取默认:index
## fileSize
    每个文件多少条数据分割,需要则设置该项,该值应该比query的size大,如果设置该值则一个文件数据条数到达该数时则会分割,会有一点误差,分割文件名为fileName+文件数,单位:条
## customFieldName
    自定义字段名,将库里该字段取出来后换为该字段名,原字段名:替换后的字段名,多个逗号隔开,如phone:telphone
## fieldSplit
    字段以什么分割,不设置则默认英文逗号隔开
## fieldSort
     字段输出顺序(必设),必须和索引表字段名一样,逗号隔开
## needFieldName
    输出为txt的时候需要字段名字,默认:false,需要的时候以此形式输出类似:fieldName1=fieldValue1,fieldName2=fieldValue2
## SSL_type
    SSL类型
## SSL_keyStorePath
    密钥地址,文件地址
## SSL_keyStorePass
    密钥密码
# 线程池设置
关于threadSize设置设置为多少合适,这里给出的权重是
-CPU核数>Shards>配置设置
意思是配置的设置不能大于CPU核数也不能大于索引的shards数量。
比如我是8核的机器,shards为15,配置设置20,最后取的线程数是8
如果我是8核的机器,shards为15,配置设置7，最后取的是 7
# 导出例子
## 1、导出为json格式
导出为json格式只需要设置以下,以下根据自己的设置进行设置
```
isLineFeed=true
dataLayout=json
filePath=F:\\pb_sa_phone\\test
fileName=pb_sa_phone
fileSize=1000
customFieldName=
```
## 2、导出为txt格式
以下是dataLayout=txt,为json时以下配置都无效的时候的自定义设置
```
fieldSplit=,
fieldSort=phone
needFieldName=false
```
# 联系作者

## QQ:760515805
## wx:chj-95
