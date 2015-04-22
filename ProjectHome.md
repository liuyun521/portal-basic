**最新版本请到 [GitHub](https://github.com/ldcsaa) 或 [OSChina](http://git.oschina.net/ldcsaa) 下载**


---


![http://bcs.duapp.com/jessma/blog%2F201306%2FLogo-Large.png](http://bcs.duapp.com/jessma/blog%2F201306%2FLogo-Large.png)
**`    JessMA（原名：Portal-Basic）是一套功能完备的高性能 Full-Stack Web 应用开发框架，内置可扩展的 MVC Web 基础架构和 DAO 数据库访问组件（内部已提供了 Hibernate、MyBatis 与 JDBC DAO 组件），集成了 Action 拦截、Form / Dao / Spring Bean 装配、国际化、文件上传下载和页面静态化等基础 Web 应用组件，提供高效灵活的纯 Jsp/Servlet API 编程模型，可完美整合 Spring，支持 Action Convention，能快速开发传统风格和 RESTful 风格应用程序，文档和示例完善，非常容易学习。`**

  * JessMA 官方网站：http://www.jessma.org
  * JessMA 在线示例：http://demo.jessma.org
  * JessMA 在线 API 文档：http://www.jessma.org/doc


---


> JessMA 在设计之初就充分注重功能、性能与使用体验。JessMA 主要特点：

**★ 功能全面：** 内置稳定高效的 MVC 基础架构和 DAO 框架，支持 Action 拦截、Form Bean / Dao Bean / Spring Bean 装配和声明式事务，提供国际化、文件上传下载、缓存和页面静态化等常用Web组件，能满足绝大部分 Web 应用的需要。

**★ 高度扩展：** JessMA 通过的 plug-in 机制可以灵活扩展，JessMA 发布包中自带的 jessma-ext-rest 和 jessma-ext-spring 均以插件的形式提供，用户可根据需要加载或卸载这些插件。应用程序开发者也可以根据实际需要编写自定义插件来扩展 JessMA。

**★ 强大的整合能力：** JessMA 是一个 Full-Stack 框架，同时也是一个开放式框架，可以以非常简单的方式整合第三方组件。本开发手册会详细阐述如何在 JessMA 中整合 Freemarker、 Velocity 、 Urlrewrite、 EHCache-Web 、 Spring 、 Hibernate 和 Mybaits 等常用框架和组件。

**★ 高性能：** 性能要求是 JessMA 的硬性指标，从每个模块的设计到每行代码的实现都力求简洁高效。另外，JessMA 并没有对 JSP/Servet API 进行过多封装，开发者仍然使用 JSP/Servet API 开发应用程序，没有过多的迂回，性能得到保证。

**★ 优秀的使用体验：** JessMA 的设计目标之一是提供良好的开发体验，尽量减少应用程序开发者的工作，API 的设计力求简单、完整、明确。同时，JessMA 为应用开发提供了大量 Util 工具，用来处理应用程序开发过程中通常会遇到的一般性问题，进一步减少应用程序开发者的工作负担。

**★ 平缓的学习曲线：** 学习使用 JessMA 只需掌握一定的 Core Java 与 JSP/Servlet 知识，本开发手册会循序渐进阐述每个知识点，每个知识点都会结合完整的示例进行讲述，知识点之间前后呼应，确保学习者在学习时温故知新，融会贯通。

**★ 完善的技术支持：** 除了提供完善的开发手册和示例代码以外，还提供博客和 QQ 群用于解答使用 JessMA 过程中碰到的所有问题，也可以访问 JessMA 官方网站（ http://www.jessma.org ）了解更多资讯。


**JessMA 总体架构**

![http://bcs.duapp.com/jessma/blog/201306/JessMA%E6%80%BB%E4%BD%93%E6%9E%B6%E6%9E%843.png](http://bcs.duapp.com/jessma/blog/201306/JessMA%E6%80%BB%E4%BD%93%E6%9E%B6%E6%9E%843.png)


**JessMA 应用程序依赖关系**

![http://bcs.duapp.com/jessma/blog/201306/JessMA%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F%E4%BE%9D%E8%B5%96%E5%85%B3%E7%B3%BB.png](http://bcs.duapp.com/jessma/blog/201306/JessMA%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F%E4%BE%9D%E8%B5%96%E5%85%B3%E7%B3%BB.png)