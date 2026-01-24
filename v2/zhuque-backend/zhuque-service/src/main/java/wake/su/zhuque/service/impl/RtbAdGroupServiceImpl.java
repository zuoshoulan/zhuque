package wake.su.zhuque.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.common.util.SecurityUtil;
import wake.su.zhuque.dao.mapper.RtbAdGroupMapper;
import wake.su.zhuque.dao.mapper.RtbCampaignMapper;
import wake.su.zhuque.model.dto.AdGroupCreateRequest;
import wake.su.zhuque.model.dto.AdGroupQueryRequest;
import wake.su.zhuque.model.dto.AdGroupUpdateRequest;
import wake.su.zhuque.model.entity.RtbAdGroupDO;
import wake.su.zhuque.model.entity.RtbCampaignDO;
import wake.su.zhuque.model.enums.*;
import wake.su.zhuque.model.vo.AdGroupListVO;
import wake.su.zhuque.model.vo.AdGroupVO;
import wake.su.zhuque.service.RtbAdGroupService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 广告组Service实现
 * 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RtbAdGroupServiceImpl implements RtbAdGroupService {

    private final RtbAdGroupMapper adGroupMapper;
    private final RtbCampaignMapper campaignMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Long create(AdGroupCreateRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("未登录或登录已过期");
        }

        // 校验投放活动是否存在且属于当前用户
        RtbCampaignDO campaign = campaignMapper.selectById(request.getCampaignId());
        if (campaign == null) {
            throw new RuntimeException("投放活动不存在");
        }
        if (!campaign.getAdvertiserId().equals(currentUserId)) {
            throw new RuntimeException("无权限在此投放活动下创建广告组");
        }

        // 校验出价
        if (request.getBaseBidPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("基础出价必须大于0");
        }
        if (request.getMaxBid() != null && request.getMaxBid().compareTo(request.getBaseBidPrice()) < 0) {
            throw new RuntimeException("最高出价不能低于基础出价");
        }
        if (request.getBidFloor() != null && request.getBaseBidPrice().compareTo(request.getBidFloor()) < 0) {
            throw new RuntimeException("基础出价不能低于竞价底价");
        }

        // 校验目标CPA配合出价策略
        if (Objects.equals(request.getBidStrategy(), 3) && request.getTargetCpa() == null) {
            throw new RuntimeException("目标CPA出价策略必须设置目标CPA值");
        }

        RtbAdGroupDO adGroup = new RtbAdGroupDO();
        adGroup.setCampaignId(request.getCampaignId());
        adGroup.setAdvertiserId(currentUserId);
        adGroup.setName(request.getName());
        adGroup.setDescription(request.getDescription());

        // 出价设置
        adGroup.setBidStrategy(request.getBidStrategy());
        adGroup.setBaseBidPrice(request.getBaseBidPrice());
        adGroup.setMaxBid(request.getMaxBid());
        adGroup.setBidFloor(request.getBidFloor());
        adGroup.setTargetCpa(request.getTargetCpa());
        adGroup.setTargetRoas(request.getTargetRoas());
        adGroup.setBidAdjustments(request.getBidAdjustments());

        // 预算控制
        adGroup.setDailyBudget(request.getDailyBudget());
        adGroup.setDailyBudgetUsed(BigDecimal.ZERO);

        // 投放速度
        adGroup.setDeliveryMode(request.getDeliveryMode());
        adGroup.setDeliveryPace(request.getDeliveryPace());

        // 定向设置
        adGroup.setTargetingGeo(request.getTargetingGeo());
        adGroup.setTargetingGeoExclude(request.getTargetingGeoExclude());
        adGroup.setTargetingDevice(request.getTargetingDevice());
        adGroup.setTargetingOs(request.getTargetingOs());
        adGroup.setTargetingOsVersion(request.getTargetingOsVersion());
        adGroup.setTargetingCarrier(request.getTargetingCarrier());
        adGroup.setTargetingConnectionType(request.getTargetingConnectionType());
        adGroup.setTargetingBrowser(request.getTargetingBrowser());
        adGroup.setTargetingKeywords(request.getTargetingKeywords());
        adGroup.setTargetingKeywordsExclude(request.getTargetingKeywordsExclude());
        adGroup.setTargetingIabCategories(request.getTargetingIabCategories());
        adGroup.setTargetingIabCategoriesExclude(request.getTargetingIabCategoriesExclude());
        adGroup.setTargetingUserSegments(request.getTargetingUserSegments());
        adGroup.setTargetingUserSegmentsExclude(request.getTargetingUserSegmentsExclude());
        adGroup.setTargetingAudienceType(request.getTargetingAudienceType());

        // 时段定向
        adGroup.setScheduleType(request.getScheduleType());
        adGroup.setScheduleConfig(request.getScheduleConfig());

        // 频次控制
        adGroup.setFrequencyCap(request.getFrequencyCap());
        adGroup.setFrequencyCapPeriod(request.getFrequencyCapPeriod());

        // 品牌安全
        adGroup.setBrandSafetyLevel(request.getBrandSafetyLevel());
        adGroup.setBrandSafetyCategoriesExclude(request.getBrandSafetyCategoriesExclude());

        // 状态和优先级
        adGroup.setStatus(0); // 默认草稿
        adGroup.setPriority(request.getPriority() != null ? request.getPriority() : 0);

        adGroupMapper.insert(adGroup);
        return adGroup.getId();
    }

    @Override
    @Transactional
    public boolean update(Long id, AdGroupUpdateRequest request) {
        RtbAdGroupDO adGroup = adGroupMapper.selectById(id);
        if (adGroup == null) {
            throw new RuntimeException("广告组不存在");
        }

        // 权限校验
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!adGroup.getAdvertiserId().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此广告组");
        }

        // 更新字段
        adGroup.setName(request.getName());
        adGroup.setDescription(request.getDescription());

        if (request.getBidStrategy() != null) {
            adGroup.setBidStrategy(request.getBidStrategy());
        }
        if (request.getBaseBidPrice() != null) {
            adGroup.setBaseBidPrice(request.getBaseBidPrice());
        }
        if (request.getMaxBid() != null) {
            adGroup.setMaxBid(request.getMaxBid());
        }
        if (request.getBidFloor() != null) {
            adGroup.setBidFloor(request.getBidFloor());
        }
        if (request.getTargetCpa() != null) {
            adGroup.setTargetCpa(request.getTargetCpa());
        }
        if (request.getTargetRoas() != null) {
            adGroup.setTargetRoas(request.getTargetRoas());
        }
        if (request.getBidAdjustments() != null) {
            adGroup.setBidAdjustments(request.getBidAdjustments());
        }
        if (request.getDailyBudget() != null) {
            adGroup.setDailyBudget(request.getDailyBudget());
        }
        if (request.getDeliveryMode() != null) {
            adGroup.setDeliveryMode(request.getDeliveryMode());
        }
        if (request.getDeliveryPace() != null) {
            adGroup.setDeliveryPace(request.getDeliveryPace());
        }
        if (request.getTargetingGeo() != null) {
            adGroup.setTargetingGeo(request.getTargetingGeo());
        }
        if (request.getTargetingGeoExclude() != null) {
            adGroup.setTargetingGeoExclude(request.getTargetingGeoExclude());
        }
        if (request.getTargetingDevice() != null) {
            adGroup.setTargetingDevice(request.getTargetingDevice());
        }
        if (request.getTargetingOs() != null) {
            adGroup.setTargetingOs(request.getTargetingOs());
        }
        if (request.getTargetingOsVersion() != null) {
            adGroup.setTargetingOsVersion(request.getTargetingOsVersion());
        }
        if (request.getTargetingCarrier() != null) {
            adGroup.setTargetingCarrier(request.getTargetingCarrier());
        }
        if (request.getTargetingConnectionType() != null) {
            adGroup.setTargetingConnectionType(request.getTargetingConnectionType());
        }
        if (request.getTargetingBrowser() != null) {
            adGroup.setTargetingBrowser(request.getTargetingBrowser());
        }
        if (request.getTargetingKeywords() != null) {
            adGroup.setTargetingKeywords(request.getTargetingKeywords());
        }
        if (request.getTargetingKeywordsExclude() != null) {
            adGroup.setTargetingKeywordsExclude(request.getTargetingKeywordsExclude());
        }
        if (request.getTargetingIabCategories() != null) {
            adGroup.setTargetingIabCategories(request.getTargetingIabCategories());
        }
        if (request.getTargetingIabCategoriesExclude() != null) {
            adGroup.setTargetingIabCategoriesExclude(request.getTargetingIabCategoriesExclude());
        }
        if (request.getTargetingUserSegments() != null) {
            adGroup.setTargetingUserSegments(request.getTargetingUserSegments());
        }
        if (request.getTargetingUserSegmentsExclude() != null) {
            adGroup.setTargetingUserSegmentsExclude(request.getTargetingUserSegmentsExclude());
        }
        if (request.getTargetingAudienceType() != null) {
            adGroup.setTargetingAudienceType(request.getTargetingAudienceType());
        }
        if (request.getScheduleType() != null) {
            adGroup.setScheduleType(request.getScheduleType());
        }
        if (request.getScheduleConfig() != null) {
            adGroup.setScheduleConfig(request.getScheduleConfig());
        }
        if (request.getFrequencyCap() != null) {
            adGroup.setFrequencyCap(request.getFrequencyCap());
        }
        if (request.getFrequencyCapPeriod() != null) {
            adGroup.setFrequencyCapPeriod(request.getFrequencyCapPeriod());
        }
        if (request.getBrandSafetyLevel() != null) {
            adGroup.setBrandSafetyLevel(request.getBrandSafetyLevel());
        }
        if (request.getBrandSafetyCategoriesExclude() != null) {
            adGroup.setBrandSafetyCategoriesExclude(request.getBrandSafetyCategoriesExclude());
        }
        if (request.getPriority() != null) {
            adGroup.setPriority(request.getPriority());
        }

        return adGroupMapper.updateById(adGroup) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        RtbAdGroupDO adGroup = adGroupMapper.selectById(id);
        if (adGroup == null) {
            throw new RuntimeException("广告组不存在");
        }

        // 权限校验
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!adGroup.getAdvertiserId().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此广告组");
        }

        // 只有草稿状态可以删除
        if (!Objects.equals(adGroup.getStatus(), 0)) {
            throw new RuntimeException("只有草稿状态的广告组可以删除");
        }

        return adGroupMapper.deleteById(id) > 0;
    }

    @Override
    public AdGroupVO detail(Long id) {
        RtbAdGroupDO adGroup = adGroupMapper.selectById(id);
        if (adGroup == null) {
            return null;
        }

        // 权限校验
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!adGroup.getAdvertiserId().equals(currentUserId)) {
            throw new RuntimeException("无权限查看此广告组");
        }

        // 获取投放活动名称
        RtbCampaignDO campaign = campaignMapper.selectById(adGroup.getCampaignId());
        String campaignName = campaign != null ? campaign.getName() : "";

        return convertToVO(adGroup, campaignName);
    }

    @Override
    public Result<List<AdGroupListVO>> list(AdGroupQueryRequest request) {
        Page<RtbAdGroupDO> page = new Page<>(request.getCurrent(), request.getSize());

        // 获取当前登录用户的ID
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error("未登录或登录已过期");
        }

        LambdaQueryWrapper<RtbAdGroupDO> wrapper = new LambdaQueryWrapper<>();
        // 强制过滤：只能查看当前登录用户的广告组
        wrapper.eq(RtbAdGroupDO::getAdvertiserId, currentUserId)
                .eq(request.getCampaignId() != null, RtbAdGroupDO::getCampaignId, request.getCampaignId())
                .like(request.getName() != null, RtbAdGroupDO::getName, request.getName())
                .eq(request.getStatus() != null, RtbAdGroupDO::getStatus, request.getStatus())
                .eq(request.getBidStrategy() != null, RtbAdGroupDO::getBidStrategy, request.getBidStrategy())
                .orderByDesc(RtbAdGroupDO::getPriority)
                .orderByDesc(RtbAdGroupDO::getCreateTime);

        adGroupMapper.selectPage(page, wrapper);

        List<AdGroupListVO> list = page.getRecords().stream().map(adGroup -> {
            AdGroupListVO vo = new AdGroupListVO();
            vo.setId(adGroup.getId());
            vo.setCampaignId(adGroup.getCampaignId());

            // 获取投放活动名称
            RtbCampaignDO campaign = campaignMapper.selectById(adGroup.getCampaignId());
            vo.setCampaignName(campaign != null ? campaign.getName() : "");

            vo.setName(adGroup.getName());
            vo.setBidStrategy(adGroup.getBidStrategy());
            vo.setBidStrategyName(BidStrategyEnum.getNameByCode(adGroup.getBidStrategy()));
            vo.setBaseBidPrice(adGroup.getBaseBidPrice());
            vo.setDailyBudget(adGroup.getDailyBudget());
            vo.setDailyBudgetUsed(adGroup.getDailyBudgetUsed());

            // 计算日消耗百分比
            if (adGroup.getDailyBudget() != null && adGroup.getDailyBudget().compareTo(BigDecimal.ZERO) > 0) {
                int percent = adGroup.getDailyBudgetUsed()
                        .multiply(new BigDecimal("100"))
                        .divide(adGroup.getDailyBudget(), 0, RoundingMode.HALF_UP)
                        .intValue();
                vo.setDailyUsedPercent(percent);
            }

            vo.setStatus(adGroup.getStatus());
            vo.setStatusName(AdGroupStatusEnum.getNameByCode(adGroup.getStatus()));
            vo.setDisplayStatusType(getDisplayStatusType(adGroup.getStatus()));

            // TODO: 查询广告数量和统计数据
            vo.setAdCount(0);
            vo.setTodayImpressions(0L);
            vo.setTodayClicks(0L);
            vo.setTodayCtr(BigDecimal.ZERO);
            vo.setTodayCost(BigDecimal.ZERO);

            vo.setCreateTime(adGroup.getCreateTime());
            return vo;
        }).collect(java.util.stream.Collectors.toList());

        PageInfo pageInfo = PageInfo.of(page.getCurrent(), page.getSize(), page.getTotal());
        return Result.success(list, pageInfo);
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        RtbAdGroupDO adGroup = adGroupMapper.selectById(id);
        if (adGroup == null) {
            throw new RuntimeException("广告组不存在");
        }

        // 权限校验
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!adGroup.getAdvertiserId().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此广告组");
        }

        // 状态只能是：0=草稿/1=进行中/2=暂停
        if (status < 0 || status > 2) {
            throw new RuntimeException("无效的状态值");
        }

        adGroup.setStatus(status);
        return adGroupMapper.updateById(adGroup) > 0;
    }

    @Override
    @Transactional
    public boolean start(Long id) {
        RtbAdGroupDO adGroup = adGroupMapper.selectById(id);
        if (adGroup == null) {
            throw new RuntimeException("广告组不存在");
        }

        // 权限校验
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!adGroup.getAdvertiserId().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此广告组");
        }

        // 只有草稿或暂停状态可以启动
        if (!Objects.equals(adGroup.getStatus(), 0) && !Objects.equals(adGroup.getStatus(), 2)) {
            throw new RuntimeException("只有草稿或暂停状态的广告组可以启动");
        }

        adGroup.setStatus(1); // 进行中
        return adGroupMapper.updateById(adGroup) > 0;
    }

    @Override
    @Transactional
    public boolean pause(Long id) {
        RtbAdGroupDO adGroup = adGroupMapper.selectById(id);
        if (adGroup == null) {
            throw new RuntimeException("广告组不存在");
        }

        // 权限校验
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!adGroup.getAdvertiserId().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此广告组");
        }

        // 只有进行中状态可以暂停
        if (!Objects.equals(adGroup.getStatus(), 1)) {
            throw new RuntimeException("只有进行中的广告组可以暂停");
        }

        adGroup.setStatus(2); // 暂停
        return adGroupMapper.updateById(adGroup) > 0;
    }

    @Override
    public List<AdGroupListVO> listByCampaignId(Long campaignId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<RtbAdGroupDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RtbAdGroupDO::getCampaignId, campaignId)
                .eq(RtbAdGroupDO::getAdvertiserId, currentUserId)
                .orderByDesc(RtbAdGroupDO::getPriority)
                .orderByDesc(RtbAdGroupDO::getCreateTime);

        List<RtbAdGroupDO> list = adGroupMapper.selectList(wrapper);

        return list.stream().map(adGroup -> {
            AdGroupListVO vo = new AdGroupListVO();
            vo.setId(adGroup.getId());
            vo.setCampaignId(adGroup.getCampaignId());
            vo.setName(adGroup.getName());
            vo.setBidStrategy(adGroup.getBidStrategy());
            vo.setBidStrategyName(BidStrategyEnum.getNameByCode(adGroup.getBidStrategy()));
            vo.setBaseBidPrice(adGroup.getBaseBidPrice());
            vo.setDailyBudget(adGroup.getDailyBudget());
            vo.setDailyBudgetUsed(adGroup.getDailyBudgetUsed());
            vo.setStatus(adGroup.getStatus());
            vo.setStatusName(AdGroupStatusEnum.getNameByCode(adGroup.getStatus()));
            vo.setDisplayStatusType(getDisplayStatusType(adGroup.getStatus()));
            vo.setAdCount(0);
            vo.setTodayImpressions(0L);
            vo.setTodayClicks(0L);
            vo.setTodayCtr(BigDecimal.ZERO);
            vo.setTodayCost(BigDecimal.ZERO);
            vo.setCreateTime(adGroup.getCreateTime());
            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private AdGroupVO convertToVO(RtbAdGroupDO adGroup, String campaignName) {
        AdGroupVO vo = new AdGroupVO();
        vo.setId(adGroup.getId());
        vo.setCampaignId(adGroup.getCampaignId());
        vo.setCampaignName(campaignName);
        vo.setAdvertiserId(adGroup.getAdvertiserId());
        vo.setName(adGroup.getName());
        vo.setDescription(adGroup.getDescription());

        // 出价设置
        vo.setBidStrategy(adGroup.getBidStrategy());
        vo.setBidStrategyName(BidStrategyEnum.getNameByCode(adGroup.getBidStrategy()));
        vo.setBaseBidPrice(adGroup.getBaseBidPrice());
        vo.setMaxBid(adGroup.getMaxBid());
        vo.setBidFloor(adGroup.getBidFloor());
        vo.setTargetCpa(adGroup.getTargetCpa());
        vo.setTargetRoas(adGroup.getTargetRoas());
        vo.setBidAdjustments(adGroup.getBidAdjustments());

        // 预算控制
        vo.setDailyBudget(adGroup.getDailyBudget());
        vo.setDailyBudgetUsed(adGroup.getDailyBudgetUsed());

        if (adGroup.getDailyBudget() != null && adGroup.getDailyBudget().compareTo(BigDecimal.ZERO) > 0) {
            int percent = adGroup.getDailyBudgetUsed()
                    .multiply(new BigDecimal("100"))
                    .divide(adGroup.getDailyBudget(), 0, RoundingMode.HALF_UP)
                    .intValue();
            vo.setDailyUsedPercent(percent);
        }

        if (adGroup.getDailyBudget() != null) {
            vo.setRemainingDailyBudget(adGroup.getDailyBudget().subtract(adGroup.getDailyBudgetUsed()));
        }

        // 投放速度
        vo.setDeliveryMode(adGroup.getDeliveryMode());
        vo.setDeliveryModeName(DeliveryModeEnum.getNameByCode(adGroup.getDeliveryMode()));
        vo.setDeliveryPace(adGroup.getDeliveryPace());

        // 定向设置
        vo.setTargetingGeo(adGroup.getTargetingGeo());
        vo.setTargetingGeoCount(parseJsonArraySize(adGroup.getTargetingGeo()));
        vo.setTargetingDevice(adGroup.getTargetingDevice());
        vo.setTargetingUserSegments(adGroup.getTargetingUserSegments());
        vo.setTargetingUserSegmentsCount(parseJsonArraySize(adGroup.getTargetingUserSegments()));

        // 时段定向
        vo.setScheduleType(adGroup.getScheduleType());
        vo.setScheduleTypeName(ScheduleTypeEnum.getNameByCode(adGroup.getScheduleType()));
        vo.setScheduleConfig(adGroup.getScheduleConfig());

        // 频次控制
        vo.setFrequencyCap(adGroup.getFrequencyCap());
        vo.setFrequencyCapPeriod(adGroup.getFrequencyCapPeriod());
        vo.setFrequencyCapPeriodName(FrequencyCapPeriodEnum.getNameByCode(adGroup.getFrequencyCapPeriod()));
        vo.setFrequencyCapDesc(buildFrequencyCapDesc(adGroup.getFrequencyCap(), adGroup.getFrequencyCapPeriod()));

        // 品牌安全
        vo.setBrandSafetyLevel(adGroup.getBrandSafetyLevel());
        vo.setBrandSafetyLevelName(BrandSafetyLevelEnum.getNameByCode(adGroup.getBrandSafetyLevel()));

        // 状态
        vo.setStatus(adGroup.getStatus());
        vo.setStatusName(AdGroupStatusEnum.getNameByCode(adGroup.getStatus()));
        vo.setDisplayStatusType(getDisplayStatusType(adGroup.getStatus()));
        vo.setPriority(adGroup.getPriority());

        // TODO: 查询统计数据
        vo.setAdCount(0);
        vo.setTodayImpressions(0L);
        vo.setTodayClicks(0L);
        vo.setTodayConversions(0L);
        vo.setTodayCtr(BigDecimal.ZERO);
        vo.setTodayCvr(BigDecimal.ZERO);
        vo.setTodayCost(BigDecimal.ZERO);
        vo.setTotalImpressions(0L);
        vo.setTotalClicks(0L);
        vo.setTotalConversions(0L);
        vo.setTotalCtr(BigDecimal.ZERO);
        vo.setTotalCvr(BigDecimal.ZERO);
        vo.setTotalCost(BigDecimal.ZERO);

        vo.setCreateTime(adGroup.getCreateTime());
        vo.setUpdateTime(adGroup.getUpdateTime());

        return vo;
    }

    /**
     * 解析JSON数组大小
     */
    private Integer parseJsonArraySize(String json) {
        if (json == null || json.isEmpty()) {
            return 0;
        }
        try {
            List<String> list = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return list.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 构建频次控制描述
     */
    private String buildFrequencyCapDesc(Integer cap, Integer period) {
        if (cap == null) {
            return "不限";
        }
        String periodName = FrequencyCapPeriodEnum.getNameByCode(period);
        return cap + "次/" + periodName;
    }

    /**
     * 获取显示状态类型
     */
    private String getDisplayStatusType(Integer status) {
        if (status == null) {
            return "info";
        }
        switch (status) {
            case 0:
                return "info";
            case 1:
                return "success";
            case 2:
                return "warning";
            default:
                return "info";
        }
    }
}
