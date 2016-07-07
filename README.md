# rabbitmq-concurrent-service
Through the rabbitmq decoupling business processing, and in the back end to achieve multi-threaded concurrent processing

* Download RabbitMQ from http://www.rabbitmq.com/download.html[Binary tar.xz|.zip]
* Download RabbitMQ Java client

### 安装rabbitmq-service
* 安装erlang虚拟机：apt-get install erlang(ubuntu) or 下载源码安装 https://www.erlang.org/
* 直接解压 运行rabbitmq-server -detached 
* 停止rabbitmqctl stop_app
* 重启rabbitmqctl start_app
* 安装rabbitmq web管理插件./rabbitmq-plugins enable rabbitmq_management
* rabbitmq-plugins list 查看安装插件
* http://localhost:15672 登录rabbitmq web管理界面，默认用户名guest，密码guest

### 自己写了个列子：解耦数据库写操作和业务服务器
### 数据一致性问题可能存在问题
* Download mysql jdbc driver from http://dev.mysql.com/downloads/connector/j/
* 安装mysql-server from http://dev.mysql.com/downloads/mysql/
* 登录mysql终端，运行命令：source test_performance.sql 建数据库和表
* 将代码加到eclipse中运行
* MysqlDemoByDriect.java  : 直接插入数据库20000条数据
* MysqlDemoByRabbitmq.java: 发送20000条sql到rabbitmq
* server.java: 从rabbitmq接受数据执行sql，插入mysql数据库