package asia.lhweb.lhmybatis.sqlsession;

import asia.lhweb.lhmybatis.config.Function;
import asia.lhweb.lhmybatis.config.MapperBean;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    /**
     * 读取XXXMapper.xml,能够创建MapperBean的对象
     *
     * XXXMapper.xml如果在resources下就直接传文件名就可以了
     * @param path 就是xml的路径+文件名  是从类的加载路径计算的
     * @return {@link MapperBean}
     */
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
}
