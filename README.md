

💡  **封装 Sqlsession 到执行器 + Mapper 接口和 Mapper.xml + MapperBean + 动态代理 **<br />**代理 Mapper 的方法**
GitHub地址：[https://github.com/1072344372/lh-mybatis](https://github.com/1072344372/lh-mybatis)<br />分阶段完成 每个阶段可以github提交记录
## MyBatis整体架构分析
### 一图胜千言
先来看看mybatis原本的执行流程<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1695039400173-2b7080cd-d545-42fd-a407-bbd3a5746a21.png#averageHue=%23f7f7db&clientId=u1c0b83bb-ce10-4&from=paste&height=359&id=ua5dae24f&originHeight=539&originWidth=1604&originalType=binary&ratio=1&rotation=0&showTitle=false&size=105747&status=done&style=none&taskId=uab874d94-dee7-473f-ba62-25060b097a9&title=&width=1069.3333333333333)<br />Mybatis的核心框架图<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694777416096-b58c724c-0b31-4e91-ae4d-0291117bc2d2.png#averageHue=%23f9f8f8&clientId=u54510a58-7092-4&from=paste&height=619&id=uadf84d09&originHeight=774&originWidth=1170&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=117833&status=done&style=none&taskId=ua8d51d66-be96-4a31-937a-93601a8b8c8&title=&width=936)<br />**对上图的解读**<br />**1) mybatis 的核心配置文件 **<br />**mybatis-config.xml: 进行全局配置，全局只能有一个这样的配置文件 **<br />**XxxMapper.xml 配置多个 SQL，可以有多个 XxxMappe.xml 配置文件 **<br />**2) 通过 mybatis-config.xml 配置文件得到 SqlSessionFactory **<br />**3) 通过 SqlSessionFactory 得到 SqlSession，用 SqlSession 就可以操作数据了 **<br />**4) SqlSession 底层是 Executor(执行器), 有 2个 重要的实现类, 有很多方法**<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694777940804-e2065e2b-202c-4ec2-a73e-a8bc079905e8.png#averageHue=%23f8f7f5&clientId=u54510a58-7092-4&from=paste&height=259&id=ufe832372&originHeight=388&originWidth=499&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=36677&status=done&style=none&taskId=u1eb3bd88-1c72-4d5b-9927-b46c5104714&title=&width=332.6666666666667)![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694778001867-111adaf6-09ff-4a6c-836b-f5f7ed99ef85.png#averageHue=%23f4f4f3&clientId=u54510a58-7092-4&from=paste&height=163&id=ua2b5a5fd&originHeight=244&originWidth=304&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=10418&status=done&style=none&taskId=u97641c08-d8c9-4de1-908a-9a5cfad4f6f&title=&width=202.66666666666666)![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694778084272-b1be77a1-b8fd-4c7b-bc2d-44ba8489936a.png#averageHue=%23f7f6f4&clientId=u54510a58-7092-4&from=paste&height=139&id=u62ffc11e&originHeight=209&originWidth=232&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=7934&status=done&style=none&taskId=u7c3721b9-9021-4cae-81c6-b6305c991c4&title=&width=154.66666666666666)<br />**5) MappedStatement 是通过 XxxMapper.xml 中定义, 生成的 statement 对象 **<br />**6) 参数输入执行并输出结果集, 无需手动判断参数类型和参数下标位置, 且自动将结果集 **<br />**映射为 Java 对象**
## Lh-mybatis的设计思路
### ![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694839911491-aca969ef-c89e-41a3-ac38-18c994d1b8aa.png#averageHue=%23f8f8f1&clientId=u5a64dcc7-8dfc-4&from=paste&height=483&id=u1c799dea&originHeight=725&originWidth=1359&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=164669&status=done&style=none&taskId=u868f0c96-8441-4400-9b12-7f686ded8c3&title=&width=906)

## 1.实现阶段1-完成读取配置文件，得到数据库连接
### 1.1 说明：通过配置文件，获取数据库连接
完成这2部分<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694840337815-39c7990d-a56a-489f-916a-c9444c2511e1.png#averageHue=%23f9f9f0&clientId=u5a64dcc7-8dfc-4&from=paste&height=391&id=u8b7c3fa6&originHeight=586&originWidth=1333&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=119798&status=done&style=none&taskId=u2f433985-7e50-4f7a-b49d-3d5041b40fb&title=&width=888.6666666666666)
### 1.2 分析+代码实现
```java
package asia.lhweb.lhmybatis.sqlsession;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * lh配置
 *
 * @author 罗汉
 * @date 2023/09/16
 */
public class LhConfiguration {

    //属性-类加载器
    private static ClassLoader loader=ClassLoader.getSystemClassLoader();

    /**
     * 读取xml文件信息，并处理
     *
     * @param resource 资源
     * @return {@link Connection}
     */
    public Connection build(String resource){
        Connection connection=null;
        try {
            //加载配置文件lh-mybatis.xml 获取到对应的InputStream
            InputStream stream = loader.getResourceAsStream(resource);

            //解析xml文件 dom4j
            SAXReader saxReader = new SAXReader();

            Document document = saxReader.read(stream);

            //获取lh-mybatis.xml的database标签
            Element rootElement = document.getRootElement();//拿到根元素
            // System.out.println(rootElement);
            //根据rootElement去解析
             connection = evalDataSource(rootElement);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 解析xml文件返回连接
     *
     * @param root 根元素
     * @return {@link Connection}
     */
    private Connection evalDataSource(Element root){
        if (!"database".equals(root.getName())){
            throw new RuntimeException("root节点不是<database>");
        }

        //用来储存name和value
        Map<String, String> properties = new HashMap<>();
        //遍历root下的节点
        for (Object element : root.elements("property")) {
            Element i=(Element)element;//i就是 对应property节点
            // <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
            String name = i.attributeValue("name");
            String value = i.attributeValue("value");

            //判断是否得到了name和value
            if (name==null||value==null){
                throw new  RuntimeException("property节点没有设置name或者value属性");
            }
            properties.put(name, value);
        }
        String driverClassName = properties.get("driverClassName");
        String url = properties.get("url");
        String username = properties.get("username");
        String password = properties.get("password");
        Connection connection=null;
        try {
            Class.forName(driverClassName);
            connection= DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}

```
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<database>
    <!--配置连接数据库的信息-->
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://127.0.0.1:3306/lh_mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8"/>
    <property name="username" value="root"/>
    <property name="password" value="root"/>
</database>
```
### 1.3 完成测试
注意：测试文件我并没有上传到github<br />在这里设置的<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694855740203-8903b7ba-ffec-44a4-9fa3-195682e5ed80.png#averageHue=%23f9f8f8&clientId=u5a64dcc7-8dfc-4&from=paste&height=693&id=u83602c6e&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=193838&status=done&style=none&taskId=u6612fd3e-b4cf-40ec-a0f9-c9cd766fb71&title=&width=1280)
### ![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694851170908-d93a7802-343a-49d1-b617-bfacc8bb0848.png#averageHue=%23f9f8f8&clientId=u5a64dcc7-8dfc-4&from=paste&height=693&id=u2eb34f1b&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=192212&status=done&style=none&taskId=ua6cfb94f-f00c-497b-be41-dc3432623c6&title=&width=1280)
## 2.实现阶段2-编写执行器，输入sql语句，完成操作
### 2.1 说明：通过实现执行器机制，对数据库操作
直接通过Executor对db进行操作，走的线路1（BaseExecutor）<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694852081437-90bd6491-30c0-4dd2-b8e3-015a58f68782.png#averageHue=%23f8f8f1&clientId=u5a64dcc7-8dfc-4&from=paste&height=489&id=ucc7ceb50&originHeight=734&originWidth=1377&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=172918&status=done&style=none&taskId=u8ca1a586-8446-44a3-a3a8-0da96e98531&title=&width=918)
### 2.1 分析+代码实现
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694855852900-572f02b9-e78d-4d22-a621-9a8af1a0615d.png#averageHue=%23f9f8f7&clientId=u5a64dcc7-8dfc-4&from=paste&height=507&id=uc0510d03&originHeight=761&originWidth=503&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=35598&status=done&style=none&taskId=u69c4c0ca-862f-4264-8335-b54d0991d55&title=&width=335.3333333333333)
```java
package asia.lhweb.entity;

import lombok.*;

import java.util.Date;

/**
 * 怪物
 *
 * @author 罗汉
 * @date 2023/09/16
 */
// @Getter
// @Setter
// @ToString

@Data//相当于上面全部的注解 不包括构造器
// @AllArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Monster {
    private Integer id;
    private Integer age;
    private Date birthday;
    private String email;
    private Integer gender;
    private String name;
    private Double salary;
}
```
```java
package asia.lhweb.lhmybatis.sqlsession;

/**
 * 遗嘱执行人
 *
 * @author 罗汉
 * @date 2023/09/16
 */
public interface Executor {
    /**
     * 查询
     *
     * @param statement 声明
     * @param parameter 参数
     * @return {@link T}
     */
    public <T>T query(String statement,Object parameter);
}

```
```java
package asia.lhweb.lhmybatis.sqlsession;

import asia.lhweb.entity.Monster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author :罗汉
 * @date : 2023/9/16
 */
public class LhExecutor implements Executor{
    private LhConfiguration lhConfiguration =new LhConfiguration();
    /**
     * 根据sql查询
     *
     * @param sql 声明
     * @param parameter 参数
     * @return {@link T}
     */
    @Override
    public <T> T query(String sql, Object parameter) {
        //得到一个连接
        Connection connection = getConnection();
        //查询返回的结果集
        ResultSet set=null;
        PreparedStatement pre=null;

        try {
            pre=connection.prepareStatement(sql);
            pre.setString(1, parameter.toString());
            set=pre.executeQuery();
            //把set数据封装到对象-monster
            //做了简化处理
            //认为返回的结果就是一个monster记录
            //完善的写法是一套完善的反射机制
            Monster monster = new Monster();

            //遍历结果集
            while (set.next()){
                monster.setId(set.getInt("id"));
                monster.setName(set.getString("name"));
                monster.setEmail(set.getString("email"));
                monster.setAge(set.getInt("age"));
                monster.setGender(set.getInt("gender"));
                monster.setSalary(set.getDouble("salary"));
                monster.setBirthday(set.getDate("birthday"));
            }
            return (T)monster;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (set!=null) set.close();
                if (pre!=null) pre.close();
                if (connection!=null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private Connection getConnection(){
        Connection connection = lhConfiguration.build("lh-mybatis.xml");
        return connection;
    }
}

```
### 2.1 完成测试
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694855692549-ecd85e2f-04e0-4c9a-a1f1-895fffe30ae6.png#averageHue=%23f6f5f5&clientId=u5a64dcc7-8dfc-4&from=paste&height=693&id=uf04ba14a&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=220696&status=done&style=none&taskId=u5da38745-9240-4b71-b71e-c8dbf92585d&title=&width=1280)
## 3.实现阶段3-将Sqlsession封装到执行器
### 3.1 说明
ctrl+n查找类<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694857362411-2aa10092-74e8-4513-a3d8-febd8d177469.png#averageHue=%23f9f8f6&clientId=u5a64dcc7-8dfc-4&from=paste&height=693&id=u46c473a0&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=286840&status=done&style=none&taskId=u182083e1-fecc-4398-8034-84df2953717&title=&width=1280)<br />会封装2个重要的属性<br />一个LhConfiguration和一个LhExecutor<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694857501315-5bfd8548-29f5-43a9-804c-2f1908a9c185.png#averageHue=%23f8f8f1&clientId=u5a64dcc7-8dfc-4&from=paste&height=458&id=AwqsC&originHeight=687&originWidth=1428&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=166090&status=done&style=none&taskId=u4702fc6a-3659-4e0f-aa57-a0e5be1f6b2&title=&width=952)
### 3.2 分析+代码实现
只有一个文件
```java
package asia.lhweb.lhmybatis.sqlsession;

/**
 * SQL会话
 * LhSqlSession:搭建Configuration(连接)和Executor之间的桥梁
 * 注意！！原生的有很多方法，这里只写一个 体验流程
 * @author 罗汉
 * @date 2023/09/16
 */
public class LhSqlSession {
    //执行器
    private Executor executor=new LhExecutor();

    //配置
    private LhConfiguration lhConfiguration=new LhConfiguration();

    //编写方法SelectOne 返回一条记录（一个对象）
    public <T>T selectOne(String statement, Object parameter){
        return executor.query(statement, parameter);
    }
}

```

### 3.3 完成测试
### ![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694858095491-6c6b026a-9dd2-470d-9839-b5363617e124.png#averageHue=%23f8f7f7&clientId=u5a64dcc7-8dfc-4&from=paste&height=594&id=u51d0a70a&originHeight=1040&originWidth=1920&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=230285&status=done&style=none&taskId=u15db8dc6-689f-4781-a893-6c5564ff1bb&title=&width=1097.142857142857)
## 4.实现阶段4-开发Mapper接口和Mapper.xml
### 4.1 说明
开发Mapper接口和Mapper.xml文件
### ![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694860006325-e66f5292-b494-4f51-b4d1-d265d21fc5bd.png#averageHue=%23f7f7f6&clientId=u5a64dcc7-8dfc-4&from=paste&height=71&id=u7f8561f7&originHeight=125&originWidth=618&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=14957&status=done&style=none&taskId=u1843012c-31ff-41c4-94f1-4244ff069c4&title=&width=353.14285714285717)
### 4.2 分析+代码实现
### ![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694860491060-ddd80035-f3a7-42f4-93ef-56a68bc6c28a.png#averageHue=%23f6f4f2&clientId=u5a64dcc7-8dfc-4&from=paste&height=287&id=ub5a19279&originHeight=502&originWidth=330&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=25446&status=done&style=none&taskId=u60055d70-664d-4144-9f36-e4947c7b4f8&title=&width=188.57142857142858)
```java
package asia.lhweb.mapper;

import asia.lhweb.entity.Monster;

/**
 * @author :罗汉
 * @date : 2023/9/16
 */
public interface MonsterMapper {

    //查询方法
    Monster getMonsterById(Integer id);
}

```
这里只是模仿过程，所以用"?"占位符
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="asia.lhweb.mapper.MonsterMapper">
    <select id="getMonsterById" resultType="asia.lhweb.entity.Monster">
        select * from monster where id=?
    </select>
</mapper>
```
### 4.3 完成测试
### 无
## 5.实现阶段5-开发和Mapper接口相映射的MapperBean
### 5.1 说明
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694861233779-e7359eb5-0c2e-4db5-b8ef-504796082077.png#averageHue=%23ccd887&clientId=u5a64dcc7-8dfc-4&from=paste&height=126&id=u95ee2d9b&originHeight=221&originWidth=842&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=33138&status=done&style=none&taskId=uee88b984-41ed-47ba-8e9c-8b0b357ace4&title=&width=481.14285714285717)

为什么要有这个MapperBean？<br />将来这个Mapper里面可能有很多方法（虽然目前只写了一个），最终是要用动态代理方法去联系Mapper接口和Mapper.xml。就用这个来记录Mapper里的所有信息。传统方法是通过一个实现类<br />![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1694861367227-c0234314-28b2-4432-8c45-58c157033fcd.png#averageHue=%23f8f8f7&clientId=u5a64dcc7-8dfc-4&from=paste&height=190&id=u95d7f7e2&originHeight=332&originWidth=361&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=22469&status=done&style=none&taskId=u1bf0d9db-c45d-4107-be30-9b7c4d08a7a&title=&width=206.28571428571428)
### 5.2 分析+代码实现
```java
package asia.lhweb.lhmybatis.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :罗汉
 * @date : 2023/9/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Function {
    //属性
    private String sqlType;//sql类型 比如select,insert,update,delete
    private String funcName;//方法名
    private String sql;//sql语句
    private Object resultType;//返回类型
    private String parameterType;//入参类型
}

```
```java
package asia.lhweb.lhmybatis.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 映射器bean
 * MapperBean:将Mapper信息进行封装
 *
 * @author 罗汉
 * @date 2023/09/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapperBean {
    private String interfaceName;// 接口全路径
    // 接口下的所有方法-集合
    private List<Function> functions;
}

```
### 5.3 完成测试
### 无
## 6.实现阶段6-在LhConfiguration读取XXXMapper.xml,能够创建MapperBean的对象
### 6.1 说明
要做到的效果 读取到以下设置<br />MapperBean(interfaceName=asia.lhweb.mapper.MonsterMapper, functions=[Function(sqlType=select, funcName=getMonsterById, sql=select * from monster where id=?, resultType=Monster(id=null, age=null, birthday=null, email=null, gender=null, name=null, salary=null), parameterType=null)])
### 6.2 分析+代码实现
LhConfiguration.java中添加如下方法readMapper（）
```java
public MapperBean readMapper(String path){
        MapperBean mapperBean = new MapperBean();
        //获取到xml对应的输入流
        InputStream resourceAsStream = loader.getResourceAsStream(path);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            /**
             * <mapper namespace="asia.lhweb.mapper.MonsterMapper">
             *     <select id="getMonsterById" resultType="asia.lhweb.entity.Monster">
             *         select * from monster where id=?
             *     </select>
             * </mapper>
             */
            Element rootElement = document.getRootElement();//得到文件的root元素
            //rootElement=mapper
            //设置mapperBean的InterfaceName 就是接口的全路径
            String namespace = rootElement.attributeValue("namespace").trim();
            mapperBean.setInterfaceName(namespace);
            //得到rootElement的迭代器
            Iterator rootIterator = rootElement.elementIterator();
            //保存接口信息
            ArrayList<Function> functions = new ArrayList<>();
            //开始遍历 生成function
            while (rootIterator.hasNext()) {
                /**
                 * 相当于拿到
                 * <select id="getMonsterById" resultType="asia.lhweb.entity.Monster">
                 *         select * from monster where id=?
                 *     </select>
                 */
                //取出一个子元素
                Element element = (Element) rootIterator.next();//dom4j下的
                // System.out.println("element-------------------:"+element);
                Function function = new Function();
                //设置属性
                String sqlType = element.getName().trim();
                String funName = element.attributeValue("id");
                String resultType = element.attributeValue("resultType");
                String sql = element.getText().trim();

                function.setFuncName(funName);
                function.setSql(sql);
                function.setSqlType(sqlType);
                //反射创建对象
                Class<?> aClass = Class.forName(resultType);
                Object instance = aClass.newInstance();
                function.setResultType(instance);
                functions.add(function);
            }
            mapperBean.setFunctions(functions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return mapperBean;
    }
```
### 6.3 完成测试
### ![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1695030762782-6bbb84b6-f4f6-48be-8905-1e0b809e4e08.png#clientId=u1c0b83bb-ce10-4&from=paste&height=1147&id=ucf82239b&originHeight=1032&originWidth=1904&originalType=binary&ratio=1&rotation=0&showTitle=false&size=260806&status=done&style=none&taskId=ue15cc104-8525-43b0-815a-d1e7c526287&title=&width=2115.55561159864)
## 7.实现阶段7-实现动态代理Mapper的方法
### 7.1 说明
### ![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1695034980534-e71c247d-18f0-434d-b686-ee12e55c506a.png#averageHue=%23f9f8f3&clientId=u1c0b83bb-ce10-4&from=paste&height=786&id=u5c20b88e&originHeight=786&originWidth=996&originalType=binary&ratio=1&rotation=0&showTitle=false&size=103950&status=done&style=none&taskId=u9798870d-753c-4795-8d82-7dbd641412b&title=&width=996)
### 7.2 分析+代码实现
新建LhMapperProxy.java
```java
package asia.lhweb.lhmybatis.sqlsession;

import asia.lhweb.lhmybatis.config.Function;
import asia.lhweb.lhmybatis.config.MapperBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Lh映射代理
 * 动态代理生成Mapper对象，调用LhExecutor方法
 *
 * @author 罗汉
 * @date 2023/09/18
 */
public class LhMapperProxy implements InvocationHandler {
    // 属性
    private LhSqlSession lhSqlSession;
    private String mapperFile;
    private LhConfiguration lhConfiguration;

    public LhMapperProxy(LhSqlSession lhSqlSession, Class aClass, LhConfiguration lhConfiguration) {
        this.lhSqlSession = lhSqlSession;
        this.mapperFile = aClass.getSimpleName() + ".xml";
        this.lhConfiguration = lhConfiguration;
    }

    /**
     * 调用
     * 当执行接口的动态代理对方方法时，会执行到invoke方法
     *
     * @param proxy  代理
     * @param method 方法
     * @param args   arg游戏
     * @return {@link Object}
     * @throws Throwable throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MapperBean mapperBean = lhConfiguration.readMapper(this.mapperFile);

        // 判断是否是xml文件对应的接口
        if (!method.getDeclaringClass().getName().equals(mapperBean.getInterfaceName())) {
            return null;
        }
        // 取出mapperBean的functions
        List<Function> functions = mapperBean.getFunctions();

        // 判断当前的mapperBean解析Mapper.xml后有方法
        if (null != functions && functions.size() != 0) {
            for (Function function : functions) {
                // 当前要执行的方法和function。getFuncName()一样
                // 说明我们可以从当前遍历的function对象中，取出一组相应的信息sql，并执行方法
                if (method.getName().equals(function.getFuncName())) {
                    // 如果我们当前的function 要执行的的sqlType是select
                    // 我们就去执行selectOne
                    /**
                     * 1.如果要执行的方法是select，就对应执行selectOne
                     * 2. 因为只是体验流程 这里就做了简化
                     * 3 主要讲解mybatis 生成mapper动态代理对象，调用方法的机制
                     *
                     */
                    if ("select".equals(function.getSqlType())) {
                        return lhSqlSession.selectOne(function.getSql(), String.valueOf(args[0]));
                    }
                }
            }
        }
        return null;
    }
}

```
在LhSqlSession中添加新方法getMapper（）
```java
    /**
     * 1 返回mapper的动态代理对象
     * 2 clazz到时传入的是MonsterMapper.class
     * 3 放回的就是MonsterMapper接口类型的代理对象
     * 4 当执行接口方法时（通过代理对象调用），根据动态代理机制会执行到invoke方法
     * @param clazz clazz
     * @return {@link T}
     */
    public <T> T getMapper(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}
                , new LhMapperProxy(this, clazz, lhConfiguration));
    }
```
新建LhSessionFactory.java
```java
package asia.lhweb.lhmybatis.sqlsession;

/**
 * Lh session工厂
 * 返回LhSqlSession
 * @author 罗汉
 * @date 2023/09/18
 */
public class LhSessionFactory {
    public static LhSqlSession openSession(){
        return new LhSqlSession();
    }
}

```
### 7.3 完成测试
![image.png](https://cdn.nlark.com/yuque/0/2023/png/35399149/1695038523047-6fe799d6-4552-47ee-a0bd-8f303cfea9d3.png#averageHue=%23f5f5f4&clientId=u1c0b83bb-ce10-4&from=paste&height=1040&id=u578e033c&originHeight=1040&originWidth=1920&originalType=binary&ratio=1&rotation=0&showTitle=false&size=257149&status=done&style=none&taskId=u6b48e756-9467-4461-b30c-217ceae3878&title=&width=1920)
## 总结
> 完善的写法是一套注解和反射机制-jdbc，这里只是体验流程。多debug体验过程就好了



