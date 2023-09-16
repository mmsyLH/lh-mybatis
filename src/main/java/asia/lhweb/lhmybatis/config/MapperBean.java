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
