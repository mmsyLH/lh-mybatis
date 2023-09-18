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
