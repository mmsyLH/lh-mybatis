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
