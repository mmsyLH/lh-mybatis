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
