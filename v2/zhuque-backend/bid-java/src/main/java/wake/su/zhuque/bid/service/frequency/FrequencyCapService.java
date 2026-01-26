package wake.su.zhuque.bid.service.frequency;

import wake.su.zhuque.model.entity.RtbAdGroupDO;

/**
 * 频次控制服务 使用 Redis Lua 脚本保证原子性
 *
 * @author zhuque
 * @version 1.0
 */
public interface FrequencyCapService {

  /**
   * 预检查频次是否超限 只查询不累加，用于预过滤阶段
   *
   * @param userId 用户ID
   * @param adGroup 广告组
   * @return true=未超限, false=已超限
   */
  boolean checkFrequency(String userId, RtbAdGroupDO adGroup);

  /**
   * 尝试记录展示 (原子操作) 使用 Lua 脚本 检查+累加，保证并发安全
   *
   * @param userId 用户ID
   * @param adGroup 广告组
   * @return true=记录成功, false=已超限
   */
  boolean tryRecord(String userId, RtbAdGroupDO adGroup);

  /**
   * 构建频次 Key
   *
   * @param adGroupId 广告组ID
   * @param userId 用户ID
   * @return Redis Key
   */
  String buildKey(Long adGroupId, String userId);

  /**
   * 回滚展示记录 当后续步骤失败时回滚已记录的展示
   *
   * @param userId 用户ID
   * @param adGroup 广告组
   */
  void rollback(String userId, RtbAdGroupDO adGroup);
}
