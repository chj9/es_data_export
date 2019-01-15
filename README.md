# 下一个版本将支持
* 1、断点续导；
* 2、配置参数动态输入；
* 3、支持从一个index导入另一个index，一个集群导入另一个集群ES；
* 4、写个定时脚本执行,PS:很多小伙伴说他们需要定时导出数据；
# About
该工具实现从ES中导出数据,并且可以对导出的数据格式和数据文件做部分自定义,该工具主要使用ES中srcoll接口多线程导出数据.

# Design
![Base](https://github.com/760515805/es_data_export/blob/master/docs/design.png)

-  项目采用 `Java` 构建。
-  访问ES部分采用官方 `RestClient` 构建通信。
-  数据导出方式为`Srcoll`方式,多线程的话通过`slice`对ES的数据切割数据。
-  线程池用 `BlockingQueue` 用作队列，如果队列使用完，则获取ES数据线程会阻塞等待新的队列。


# TODO LIST

* [x] 支持导出到文件，支持格式`txt`,`json`,`sql`。
* [x] 支持导出文件对文件进行切割处理，文件大于多少新建写入下一个文件。
* [x] 支持数据导出到数据库，支持所有主流数据库，连接池使用`druid`。
* [ ] 程序停止后重新启动能从停止的点导出。
* [ ] 配置参数动态输入。
* [ ] 支持从一个index导入另一个index，一个集群导入另一个集群ES。
* [ ] 定时脚本执行程序。
            
# Version
版本号说明:大版本.新增功能.提交次数

## V1.3.5

* 1.新增ES导数据入DB，支持大部分主流数据库。
* 1.新增支持导出SQL语句自定义sql。
* 2.修改配置文件的配置名，利于阅读。
* 3.修改文件分割策略，以文件大小分割取消以文件数量分割。
* 4.优化代码。

## V1.2.4

* 1.新增线程池监控,在数据导出结束后正确停止程序。
* 2.新增配置启动前验证配置是否正确,设置配置默认值。
* 3.优化异常日志输出,更好排查问题。

## V1.2.3

* 1.优化写文件操作,使用BlockingQueue队列缓存。
* 2.新增支持文件写到一定大小后进行文件切割。
* 3.新增支持SSL加密获取数据。

## V1.2.2

* 1.重构代码,取消自己封装的HTTP工具，使用官方RestClient工具。
* 2.新增支持多线程拉取ES数据。

## V1.0.1

* 1.实现单线程导出数据。

# Supported
| Elasticsearch version        | support   |
| --------   | -----:  | 
| >= 6.0.0     | yes |
| >= 5.0.0        |   not test| 
| >= 2.0.0        |   not test | 
| <= 1       |   not test |

# Running
可以直接取build文件夹下已经编译好的包，或者运行以下命令自行编译
```
$git clone git://github.com/760515805/es_data_export.git
$cd es_data_export
```
如果已经安装了ant环境和maven环境则可以使用以下操作
```
$ant 
$cd build
$vim export.properties
$./run.sh
```
如果只安装了maven环境则如下操作
```
$mvn clean package
$cp export.properties run.sh stop.sh logback.xml  target/
$cd target
$vim export.properties
$./run.sh
```
切记修改export.properties文件

# Development
## 1.运行环境
- IDE：IntelliJ IDEA或者Eclipse
- 项目构建工具：Maven

## 2.初始化项目
- 打开IntelliJ IDEA，将项目导入
- 修改export.properties文件配置
- 运行App.java执行

# 配置文件名词解释
## common.thread_size
    获取数据线程数据,最大不超过索引的shards数量和CPU数量,默认为1
## elasticsearch.index   
    数据索引
## elasticsearch.document_type
     索引type,无则可留空,ES7.0以后删除
## elasticsearch.query
    查询条件DSL,必须为ES的查询语句,可留空,默认:查询全部,条数1000
## elasticsearch.includes
    取哪些字段的数据,逗号隔开,如果全部取则设为空
## elasticsearch.hosts
    ES集群IP地址,逗号隔开,如:192.169.2.98:9200,192.169.2.156:9200,192.169.2.188:9200
## elasticsearch.username
    如有帐号密码则填写,如果无则留空
## elasticsearch.password
    如有帐号密码则填写,如果无则留空
## elasticsearch.ssl_type
    SSL类型
## elasticsearch.ssl_keystorepath
    密钥地址,文件地址
## elasticsearch.ssl_keystorepass
    密钥密码
## file.enabled
    是否启用写文件标志位，默认false
## file.datalayout
    输出源数据形式,目前支持json、txt、sql,如果为txt字段间是用逗号隔开,默认:json
## file.field_split
    当datalayout=txt时字段以什么分割,不设置则默认英文逗号隔开
## file.field_sort
     当datalayout=txt时字段输出顺序,必须和索引表字段名一样,有效防止数据混乱,逗号隔开逗号隔开
## file.need_field_name
    当datalayout=txt时是否需要字段名字,默认:false,设置为true时以此以下形式输出类似:fieldName1=fieldValue1,fieldName2=fieldValue2
## file.sql_format
    当datalayout=txt时所输出的sql格式,如:INSERT INTO test (phone,msgcode) VALUES (#param{phone},123);其中取ES参数为#param{字段名}
## file.linefeed
    导出数据写入文件每条数据是否需要换行,默认:true
## file.filepath
    数据输出文件路径,必填字段
## file.filename
    输出的文件名,无则取默认:index
## file.max_filesize
    每个文件多大进行分割,需要则设置该项,实际是有误差的,如果不需要分割文件留空即可,单位:KB
## file.custom_field_name
    自定义字段名,将库里该字段取出来后换为该字段名,原字段名:替换后的字段名,多个逗号隔开,如phone:telphone
## db.enabled
    是否启用写入数据库
## db.jdbc_driver_library
    驱动jar包地址，如lib/mysql-connector-java-5.1.47.jar，可以自定义自己数据库版本的驱动包，指定地址即可
## db.jdbc_connection_string
    数据库连接,如:jdbc:sqlserver://192.169.2.203:1433;DatabaseName=db_phone_sa_center
## db.jdbc_driver_class
    数据库驱动程序，如:com.mysql.jdbc.Driver
## db.jdbc_user
    数据库用户名
## db.jdbc_password
    数据库密码
## db.jdbc_template
    插入数据模版,其中#param{ES字段}来取ES的值，如：INSERT INTO test111 (name) VALUES (#param{simuid})
## db.jdbc_size
    单批次最大插入多少,默认10000
## db.jdbc_write_thread_size
    同时写DB线程数,默认1
# 线程池设置
关于threadSize设置设置为多少合适,这里给出的权重是</br>
CPU核数>Shards>配置设置</br>
意思是配置的设置不能大于CPU核数也不能大于索引的shards数量。</br>
比如我是8核的机器,shards为15,配置设置20,最后取的线程数是8</br>
如果我是8核的机器,shards为15,配置设置7，最后取的是 7

# 联系作者

## QQ:760515805
## wx:chj-95
