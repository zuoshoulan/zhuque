package wake.su.zhuque.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 广告组表 Mapper
 * 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
public interface RtbAdGroupMapper extends BaseMapper<RtbAdGroupDO> {

}
