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
