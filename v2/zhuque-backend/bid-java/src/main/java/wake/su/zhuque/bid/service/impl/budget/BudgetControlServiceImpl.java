package wake.su.zhuque.bid.service.impl.budget;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import wake.su.zhuque.bid.service.budget.BudgetControlService;
import wake.su.zhuque.dao.mapper.RtbAdGroupMapper;
import wake.su.zhuque.dao.mapper.RtbCampaignMapper;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import lombok.RequiredArgsConstructor;

/**
 * 预算控制服务实现 使用 CAS SQL 实现原子扣减
 *
 * @author zhuque
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class BudgetControlServiceImpl implements BudgetControlService {

  private static final Logger log = LoggerFactory.getLogger(BudgetControlServiceImpl.class);

  private final RtbAdGroupMapper adGroupMapper;
  private final RtbCampaignMapper campaignMapper;

  @Override
  public boolean checkBudget(RtbAdGroupDO adGroup, BigDecimal bidPrice) {
    // 检查广告组日预算
    if (adGroup.getDailyBudget() != null) {
      BigDecimal used = adGroup.getDailyBudgetUsed() != null ? adGroup.getDailyBudgetUsed() : BigDecimal.ZERO;
      if (used.add(bidPrice).compareTo(adGroup.getDailyBudget()) > 0) {
        log.debug("广告组日预算不足, adGroup={}, used={}, budget={}", adGroup.getId(), used, adGroup.getDailyBudget());
        return false;
      }
    }

    // 检查活动日预算
    // TODO: 实现活动预算检查
    return true;
  }

  @Override
  public boolean tryDeduct(RtbAdGroupDO adGroup, BigDecimal bidPrice) {
    // 广告组日预算扣减 (CAS)
    if (adGroup.getDailyBudget() != null) {
      int updated = adGroupMapper.update(null,
          new LambdaUpdateWrapper<RtbAdGroupDO>().eq(RtbAdGroupDO::getId, adGroup.getId())
              .le(RtbAdGroupDO::getDailyBudgetUsed, adGroup.getDailyBudget().subtract(bidPrice))
              .setSql("daily_budget_used = daily_budget_used + " + bidPrice));

      if (updated == 0) {
        log.debug("广告组日预算扣减失败(CAS), adGroup={}", adGroup.getId());
        return false;
      }
    }

    // TODO: 活动预算扣减

    return true;
  }

  @Override
  public void rollback(RtbAdGroupDO adGroup, BigDecimal bidPrice) {
    try {
      adGroupMapper.update(null, new LambdaUpdateWrapper<RtbAdGroupDO>().eq(RtbAdGroupDO::getId, adGroup.getId())
          .setSql("daily_budget_used = daily_budget_used - " + bidPrice));
    } catch(Exception e) {
      log.error("回滚预算失败, adGroup={}", adGroup.getId(), e);
    }
  }
}
