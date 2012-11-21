MyPortal 测试工程部署：
-----------------------------------------------
1、创建 mysql 数据库：myportal
2、执行脚本：myportal.sql
3、把 MyPortal 项目加入 MyEclipse 的 Workspace
4、加入 MyPortal 项目依赖的 jar 包：
    <A> 方式一：在 MyEclipse 中创建两个名称分别为“portal-basic-lib”和“spring-lib”的 User Library，并把 portal-basic-lib 和 spring-lib 文件夹下的 jar 包加入其中
    <B> 方式二：把 portal-basic-lib 和 spring-lib 文件夹下的 jar 包加入 MyPortal 项目的 lib 目录中，并取消 MyPortal 对“portal-basic-lib”和“spring-lib”这两个的 User Library 的依赖
4、修改 Hibernate、MyBatis 和 Jdbc 的数据库配置（proxool.xml、proxool-2.xml、mybatis.cfg.properties）
5、把 MyPortal 发布到 tomcat（默认发布目录为 portal）
6、启动 tomcat，检查启动日志，确保没有异常
7、访问：http://localhost:8080/portal