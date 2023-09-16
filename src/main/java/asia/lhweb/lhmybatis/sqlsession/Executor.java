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
