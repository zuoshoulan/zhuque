package wake.su.zhuque.service;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.CampaignCreateRequest;
import wake.su.zhuque.model.dto.CampaignQueryRequest;
import wake.su.zhuque.model.dto.CampaignUpdateRequest;
import wake.su.zhuque.model.vo.CampaignListVO;
import wake.su.zhuque.model.vo.CampaignVO;

import java.util.List;

/**
 * 投放活动Service接口
 * 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
public interface RtbCampaignService {

    /**
     * 创建投放活动
     *
     * @param request 创建请求
     * @return 活动ID
     */
    Long create(CampaignCreateRequest request);

    /**
     * 更新投放活动
     *
     * @param id      活动ID
     * @param request 更新请求
     * @return 是否成功
     */
    boolean update(Long id, CampaignUpdateRequest request);

    /**
     * 删除投放活动（仅草稿状态可删除）
     *
     * @param id 活动ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 投放活动详情
     *
     * @param id 活动ID
     * @return 活动详情
     */
    CampaignVO detail(Long id);

    /**
     * 分页查询投放活动
     *
     * @param request 查询请求
     * @return 分页结果
     */
    Result<List<CampaignListVO>> list(CampaignQueryRequest request);

    /**
     * 更新状态
     *
     * @param id     活动ID
     * @param status 状态：0=草稿/1=进行中/2=暂停/3=已完成
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 启动投放活动
     *
     * @param id 活动ID
     * @return 是否成功
     */
    boolean start(Long id);

    /**
     * 暂停投放活动
     *
     * @param id 活动ID
     * @return 是否成功
     */
    boolean pause(Long id);
}
