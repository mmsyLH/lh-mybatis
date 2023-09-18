package asia.lhweb.lhmybatis.sqlsession;

import java.lang.reflect.Proxy;

/**
 * SQL会话
 * LhSqlSession:搭建Configuration(连接)和Executor之间的桥梁
 * 注意！！原生的有很多方法，这里只写一个 体验流程
 *
 * @author 罗汉
 * @date 2023/09/16
 */
public class LhSqlSession {
    // 执行器
    private Executor executor = new LhExecutor();

    // 配置
    private LhConfiguration lhConfiguration = new LhConfiguration();

    // 编写方法SelectOne 返回一条记录（一个对象）
    public <T> T selectOne(String statement, Object parameter) {
        return executor.query(statement, parameter);
    }

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
}
