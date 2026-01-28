package wake.su.zhuque.service;

import java.util.List;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.CreativeCreateRequest;
import wake.su.zhuque.model.dto.CreativeQueryRequest;
import wake.su.zhuque.model.dto.CreativeUpdateRequest;
import wake.su.zhuque.model.vo.CreativeListVO;
import wake.su.zhuque.model.vo.CreativeVO;

/**
 * 创意Service接口
 */
public interface RtbCreativeService {

  /**
   * 创建创意
   */
  Long create(CreativeCreateRequest request);

  /**
   * 更新创意
   */
  void update(CreativeUpdateRequest request);

  /**
   * 删除创意
   */
  void delete(Long id);

  /**
   * 创意详情
   */
  CreativeVO detail(Long id);

  /**
   * 分页查询
   */
  Result<List<CreativeListVO>> list(CreativeQueryRequest request);

  /**
   * 更新状态
   */
  void updateStatus(Long id, Integer status);
}
