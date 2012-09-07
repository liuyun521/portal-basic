测试步骤：

1、创建 mysql 数据库：myportal
2、执行脚本：myportal.sql
3、把 MyPortal 项目加入 MyEclipse 的 Workspace
4、修改 Hibernate、MyBatis 和 Jdbc 的数据库配置（proxool.xml、proxool-2.xml、mybatis.cfg.properties）
5、把 MyPortal 发布到 tomcat（默认发布目录为 portal）
6、启动 tomcat，检查启动日志，确保没有异常
7、访问：http://localhost:8080/portal