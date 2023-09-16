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
