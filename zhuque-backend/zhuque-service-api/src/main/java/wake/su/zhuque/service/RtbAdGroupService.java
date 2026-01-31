package wake.su.zhuque.service;

import java.util.List;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.AdGroupCreateRequest;
import wake.su.zhuque.model.dto.AdGroupQueryRequest;
import wake.su.zhuque.model.dto.AdGroupUpdateRequest;
import wake.su.zhuque.model.vo.AdGroupListVO;
import wake.su.zhuque.model.vo.AdGroupVO;

/**
 * 广告组Service接口 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
public interface RtbAdGroupService {

  /**
   * 创建广告组
   *
   * @param request
   *          创建请求
   * @return 广告组ID
   */
  Long create(AdGroupCreateRequest request);

  /**
   * 更新广告组
   *
   * @param id
   *          广告组ID
   * @param request
   *          更新请求
   * @return 是否成功
   */
  boolean update(Long id, AdGroupUpdateRequest request);

  /**
   * 删除广告组（仅草稿状态可删除）
   *
   * @param id
   *          广告组ID
   * @return 是否成功
   */
  boolean delete(Long id);

  /**
   * 广告组详情
   *
   * @param id
   *          广告组ID
   * @return 广告组详情
   */
  AdGroupVO detail(Long id);

  /**
   * 分页查询广告组
   *
   * @param request
   *          查询请求
   * @return 分页结果
   */
  Result<List<AdGroupListVO>> list(AdGroupQueryRequest request);

  /**
   * 更新状态
   *
   * @param id
   *          广告组ID
   * @param status
   *          状态：0=草稿/1=进行中/2=暂停
   * @return 是否成功
   */
  boolean updateStatus(Long id, Integer status);

  /**
   * 启动广告组
   *
   * @param id
   *          广告组ID
   * @return 是否成功
   */
  boolean start(Long id);

  /**
   * 暂停广告组
   *
   * @param id
   *          广告组ID
   * @return 是否成功
   */
  boolean pause(Long id);

  /**
   * 根据投放活动ID查询广告组列表
   *
   * @param campaignId
   *          投放活动ID
   * @return 广告组列表
   */
  List<AdGroupListVO> listByCampaignId(Long campaignId);
}
