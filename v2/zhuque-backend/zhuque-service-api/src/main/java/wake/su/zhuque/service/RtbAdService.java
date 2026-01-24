package wake.su.zhuque.service;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.AdCreateRequest;
import wake.su.zhuque.model.dto.AdQueryRequest;
import wake.su.zhuque.model.dto.AdUpdateRequest;
import wake.su.zhuque.model.vo.AdListVO;
import wake.su.zhuque.model.vo.AdVO;

import java.util.List;

/**
 * 广告Service接口
 * 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
public interface RtbAdService {

    /**
     * 创建广告
     *
     * @param request 创建请求
     * @return 广告ID
     */
    Long create(AdCreateRequest request);

    /**
     * 更新广告
     *
     * @param id      广告ID
     * @param request 更新请求
     * @return 是否成功
     */
    boolean update(Long id, AdUpdateRequest request);

    /**
     * 删除广告（仅草稿状态可删除）
     *
     * @param id 广告ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 广告详情
     *
     * @param id 广告ID
     * @return 广告详情
     */
    AdVO detail(Long id);

    /**
     * 分页查询广告
     *
     * @param request 查询请求
     * @return 分页结果
     */
    Result<List<AdListVO>> list(AdQueryRequest request);

    /**
     * 更新状态
     *
     * @param id     广告ID
     * @param status 状态：0=草稿/1=进行中/2=暂停
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 启动广告
     *
     * @param id 广告ID
     * @return 是否成功
     */
    boolean start(Long id);

    /**
     * 暂停广告
     *
     * @param id 广告ID
     * @return 是否成功
     */
    boolean pause(Long id);

    /**
     * 根据广告组ID查询广告列表
     *
     * @param adGroupId 广告组ID
     * @return 广告列表
     */
    List<AdListVO> listByAdGroupId(Long adGroupId);
}
