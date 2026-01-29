package wake.su.zhuque.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.common.util.SecurityUtil;
import wake.su.zhuque.dao.mapper.RtbAdGroupMapper;
import wake.su.zhuque.dao.mapper.RtbAdMapper;
import wake.su.zhuque.dao.mapper.RtbCampaignMapper;
import wake.su.zhuque.dao.mapper.RtbCreativeMapper;
import wake.su.zhuque.model.dto.AdCreateRequest;
import wake.su.zhuque.model.dto.AdQueryRequest;
import wake.su.zhuque.model.dto.AdUpdateRequest;
import wake.su.zhuque.model.entity.RtbAdDO;
import wake.su.zhuque.model.entity.RtbAdGroupDO;
import wake.su.zhuque.model.entity.RtbCampaignDO;
import wake.su.zhuque.model.entity.RtbCreativeDO;
import wake.su.zhuque.model.enums.AdStatusEnum;
import wake.su.zhuque.model.vo.AdListVO;
import wake.su.zhuque.model.vo.AdVO;
import wake.su.zhuque.service.RtbAdService;

import lombok.RequiredArgsConstructor;

/**
 * 广告Service实现 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RtbAdServiceImpl implements RtbAdService {

  private final RtbAdMapper adMapper;
  private final RtbAdGroupMapper adGroupMapper;
  private final RtbCampaignMapper campaignMapper;
  private final RtbCreativeMapper creativeMapper;

  @Override
  @Transactional
  public Long create(AdCreateRequest request) {
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (currentUserId == null) {
      throw new RuntimeException("未登录或登录已过期");
    }

    // 校验广告组是否存在且属于当前用户
    RtbAdGroupDO adGroup = adGroupMapper.selectById(request.getAdGroupId());
    if (adGroup == null) {
      throw new RuntimeException("广告组不存在");
    }
    if (!adGroup.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限在此广告组下创建广告");
    }

    // 校验创意是否存在且属于当前用户
    RtbCreativeDO creative = creativeMapper.selectById(request.getCreativeId());
    if (creative == null) {
      throw new RuntimeException("创意不存在");
    }
    if (!creative.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限使用此创意");
    }

    RtbAdDO ad = new RtbAdDO();
    ad.setCampaignId(adGroup.getCampaignId());
    ad.setAdGroupId(request.getAdGroupId());
    ad.setAdvertiserId(currentUserId);
    ad.setCreativeId(request.getCreativeId());
    ad.setName(request.getName());
    ad.setLandingPageUrl(request.getLandingPageUrl());
    ad.setDisplayUrl(request.getDisplayUrl());
    ad.setTrackingParams(request.getTrackingParams());
    ad.setWeight(request.getWeight() != null ? request.getWeight() : 100);
    ad.setStatus(request.getStatus() != null ? request.getStatus() : 0);

    adMapper.insert(ad);
    return ad.getId();
  }

  @Override
  @Transactional
  public boolean update(Long id, AdUpdateRequest request) {
    RtbAdDO ad = adMapper.selectById(id);
    if (ad == null) {
      throw new RuntimeException("广告不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!ad.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此广告");
    }

    // 更新字段
    ad.setName(request.getName());

    if (request.getCreativeId() != null) {
      // 校验创意是否存在且属于当前用户
      RtbCreativeDO creative = creativeMapper.selectById(request.getCreativeId());
      if (creative == null) {
        throw new RuntimeException("创意不存在");
      }
      if (!creative.getAdvertiserId().equals(currentUserId)) {
        throw new RuntimeException("无权限使用此创意");
      }
      ad.setCreativeId(request.getCreativeId());
    }

    if (request.getLandingPageUrl() != null) {
      ad.setLandingPageUrl(request.getLandingPageUrl());
    }
    if (request.getDisplayUrl() != null) {
      ad.setDisplayUrl(request.getDisplayUrl());
    }
    if (request.getTrackingParams() != null) {
      ad.setTrackingParams(request.getTrackingParams());
    }
    if (request.getWeight() != null) {
      ad.setWeight(request.getWeight());
    }
    if (request.getStatus() != null) {
      ad.setStatus(request.getStatus());
    }

    return adMapper.updateById(ad) > 0;
  }

  @Override
  @Transactional
  public boolean delete(Long id) {
    RtbAdDO ad = adMapper.selectById(id);
    if (ad == null) {
      throw new RuntimeException("广告不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!ad.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此广告");
    }

    // 只有草稿状态可以删除
    if (!Objects.equals(ad.getStatus(), 0)) {
      throw new RuntimeException("只有草稿状态的广告可以删除");
    }

    return adMapper.deleteById(id) > 0;
  }

  @Override
  public AdVO detail(Long id) {
    RtbAdDO ad = adMapper.selectById(id);
    if (ad == null) {
      return null;
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!ad.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限查看此广告");
    }

    // 获取关联信息
    RtbCampaignDO campaign = campaignMapper.selectById(ad.getCampaignId());
    RtbAdGroupDO adGroup = adGroupMapper.selectById(ad.getAdGroupId());
    RtbCreativeDO creative = creativeMapper.selectById(ad.getCreativeId());

    return convertToVO(ad, campaign, adGroup, creative);
  }

  @Override
  public Result<List<AdListVO>> list(AdQueryRequest request) {
    Page<RtbAdDO> page = new Page<>(request.getCurrent(), request.getSize());

    // 获取当前登录用户的ID
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (currentUserId == null) {
      return Result.error("未登录或登录已过期");
    }

    LambdaQueryWrapper<RtbAdDO> wrapper = new LambdaQueryWrapper<>();
    // 强制过滤：只能查看当前登录用户的广告
    wrapper.eq(RtbAdDO::getAdvertiserId, currentUserId)
        .eq(request.getCampaignId() != null, RtbAdDO::getCampaignId, request.getCampaignId())
        .eq(request.getAdGroupId() != null, RtbAdDO::getAdGroupId, request.getAdGroupId())
        .eq(request.getCreativeId() != null, RtbAdDO::getCreativeId, request.getCreativeId())
        .like(request.getName() != null, RtbAdDO::getName, request.getName())
        .eq(request.getStatus() != null, RtbAdDO::getStatus, request.getStatus()).orderByDesc(RtbAdDO::getWeight)
        .orderByDesc(RtbAdDO::getCreateTime);

    adMapper.selectPage(page, wrapper);

    List<AdListVO> list = page.getRecords().stream().map(ad -> {
      AdListVO vo = new AdListVO();
      vo.setId(ad.getId());
      vo.setCampaignId(ad.getCampaignId());
      vo.setAdGroupId(ad.getAdGroupId());
      vo.setAdvertiserId(ad.getAdvertiserId());
      vo.setCreativeId(ad.getCreativeId());
      vo.setName(ad.getName());
      vo.setLandingPageUrl(ad.getLandingPageUrl());
      vo.setWeight(ad.getWeight());
      vo.setStatus(ad.getStatus());
      vo.setStatusName(AdStatusEnum.getNameByCode(ad.getStatus()));
      vo.setDisplayStatusType(getDisplayStatusType(ad.getStatus()));

      // 获取关联名称
      RtbAdGroupDO adGroup = adGroupMapper.selectById(ad.getAdGroupId());
      vo.setAdGroupName(adGroup != null ? adGroup.getName() : "");

      RtbCreativeDO creative = creativeMapper.selectById(ad.getCreativeId());
      vo.setCreativeName(creative != null ? creative.getName() : "");

      // TODO: 查询统计数据
      vo.setTodayImpressions(0L);
      vo.setTodayClicks(0L);
      vo.setTodayCtr(BigDecimal.ZERO);
      vo.setTodayCost(BigDecimal.ZERO);

      vo.setCreateTime(ad.getCreateTime());
      return vo;
    }).collect(java.util.stream.Collectors.toList());

    PageInfo pageInfo = PageInfo.of(page.getCurrent(), page.getSize(), page.getTotal());
    return Result.success(list, pageInfo);
  }

  @Override
  @Transactional
  public boolean updateStatus(Long id, Integer status) {
    RtbAdDO ad = adMapper.selectById(id);
    if (ad == null) {
      throw new RuntimeException("广告不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!ad.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此广告");
    }

    // 状态只能是：0=草稿/1=进行中/2=暂停
    if (status < 0 || status > 2) {
      throw new RuntimeException("无效的状态值");
    }

    ad.setStatus(status);
    return adMapper.updateById(ad) > 0;
  }

  @Override
  @Transactional
  public boolean start(Long id) {
    RtbAdDO ad = adMapper.selectById(id);
    if (ad == null) {
      throw new RuntimeException("广告不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!ad.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此广告");
    }

    // 只有草稿或暂停状态可以启动
    if (!Objects.equals(ad.getStatus(), 0) && !Objects.equals(ad.getStatus(), 2)) {
      throw new RuntimeException("只有草稿或暂停状态的广告可以启动");
    }

    ad.setStatus(1); // 进行中
    return adMapper.updateById(ad) > 0;
  }

  @Override
  @Transactional
  public boolean pause(Long id) {
    RtbAdDO ad = adMapper.selectById(id);
    if (ad == null) {
      throw new RuntimeException("广告不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!ad.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此广告");
    }

    // 只有进行中状态可以暂停
    if (!Objects.equals(ad.getStatus(), 1)) {
      throw new RuntimeException("只有进行中的广告可以暂停");
    }

    ad.setStatus(2); // 暂停
    return adMapper.updateById(ad) > 0;
  }

  @Override
  public List<AdListVO> listByAdGroupId(Long adGroupId) {
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (currentUserId == null) {
      return Collections.emptyList();
    }

    LambdaQueryWrapper<RtbAdDO> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(RtbAdDO::getAdGroupId, adGroupId).eq(RtbAdDO::getAdvertiserId, currentUserId)
        .orderByDesc(RtbAdDO::getWeight).orderByDesc(RtbAdDO::getCreateTime);

    List<RtbAdDO> list = adMapper.selectList(wrapper);

    return list.stream().map(ad -> {
      AdListVO vo = new AdListVO();
      vo.setId(ad.getId());
      vo.setCampaignId(ad.getCampaignId());
      vo.setAdGroupId(ad.getAdGroupId());
      vo.setAdvertiserId(ad.getAdvertiserId());
      vo.setCreativeId(ad.getCreativeId());
      vo.setName(ad.getName());
      vo.setLandingPageUrl(ad.getLandingPageUrl());
      vo.setWeight(ad.getWeight());
      vo.setStatus(ad.getStatus());
      vo.setStatusName(AdStatusEnum.getNameByCode(ad.getStatus()));
      vo.setDisplayStatusType(getDisplayStatusType(ad.getStatus()));

      RtbCreativeDO creative = creativeMapper.selectById(ad.getCreativeId());
      vo.setCreativeName(creative != null ? creative.getName() : "");

      vo.setTodayImpressions(0L);
      vo.setTodayClicks(0L);
      vo.setTodayCtr(BigDecimal.ZERO);
      vo.setTodayCost(BigDecimal.ZERO);
      vo.setCreateTime(ad.getCreateTime());
      return vo;
    }).collect(java.util.stream.Collectors.toList());
  }

  /**
   * 转换为VO
   */
  private AdVO convertToVO(RtbAdDO ad, RtbCampaignDO campaign, RtbAdGroupDO adGroup, RtbCreativeDO creative) {
    AdVO vo = new AdVO();
    vo.setId(ad.getId());
    vo.setCampaignId(ad.getCampaignId());
    vo.setAdGroupId(ad.getAdGroupId());
    vo.setAdvertiserId(ad.getAdvertiserId());
    vo.setCreativeId(ad.getCreativeId());

    vo.setCampaignName(campaign != null ? campaign.getName() : "");
    vo.setAdGroupName(adGroup != null ? adGroup.getName() : "");
    vo.setCreativeName(creative != null ? creative.getName() : "");
    vo.setCreativeType(""); // TODO: 根据创意类型获取

    vo.setName(ad.getName());
    vo.setLandingPageUrl(ad.getLandingPageUrl());
    vo.setDisplayUrl(ad.getDisplayUrl());
    vo.setTrackingParams(ad.getTrackingParams());
    vo.setWeight(ad.getWeight());
    vo.setStatus(ad.getStatus());
    vo.setStatusName(AdStatusEnum.getNameByCode(ad.getStatus()));
    vo.setDisplayStatusType(getDisplayStatusType(ad.getStatus()));

    // TODO: 查询统计数据
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

    vo.setCreateTime(ad.getCreateTime());
    vo.setUpdateTime(ad.getUpdateTime());

    return vo;
  }

  /**
   * 获取显示状态类型
   */
  private String getDisplayStatusType(Integer status) {
    if (status == null) {
      return "info";
    }
    switch(status) {
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
