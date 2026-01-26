package wake.su.zhuque.bid.service.budget;

import wake.su.zhuque.model.entity.RtbAdGroupDO;

import java.math.BigDecimal;

/**
 * 预算控制服务
 * 使用 CAS (Compare And Set) 方式原子扣减预算
 *
 * @author zhuque
 * @version 1.0
 */
public interface BudgetControlService {

    /**
     * 预检查预算是否充足
     * 只查询不扣减，用于预过滤阶段
     *
     * @param adGroup   广告组
     * @param bidPrice  出价 (元)
     * @return true=预算充足, false=预算不足
     */
    boolean checkBudget(RtbAdGroupDO adGroup, BigDecimal bidPrice);

    /**
     * 尝试扣减预算 (原子操作)
     * 使用 CAS SQL 扣减，保证并发安全
     *
     * @param adGroup   广告组
     * @param bidPrice  出价 (元)
     * @return true=扣减成功, false=预算不足
     */
    boolean tryDeduct(RtbAdGroupDO adGroup, BigDecimal bidPrice);

    /**
     * 回滚预算
     * 当后续步骤失败时回滚已扣减的预算
     *
     * @param adGroup   广告组
     * @param bidPrice  出价 (元)
     */
    void rollback(RtbAdGroupDO adGroup, BigDecimal bidPrice);
}
