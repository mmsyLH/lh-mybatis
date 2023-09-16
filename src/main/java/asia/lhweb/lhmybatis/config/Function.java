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
