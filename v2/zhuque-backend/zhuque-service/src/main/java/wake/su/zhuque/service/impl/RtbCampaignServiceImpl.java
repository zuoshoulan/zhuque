package wake.su.zhuque.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.common.util.SecurityUtil;
import wake.su.zhuque.dao.mapper.RtbCampaignMapper;
import wake.su.zhuque.model.dto.CampaignCreateRequest;
import wake.su.zhuque.model.dto.CampaignQueryRequest;
import wake.su.zhuque.model.dto.CampaignUpdateRequest;
import wake.su.zhuque.model.entity.RtbCampaignDO;
import wake.su.zhuque.model.enums.CampaignObjectiveEnum;
import wake.su.zhuque.model.enums.CampaignStatusEnum;
import wake.su.zhuque.model.vo.CampaignListVO;
import wake.su.zhuque.model.vo.CampaignVO;
import wake.su.zhuque.service.RtbCampaignService;

import lombok.RequiredArgsConstructor;

/**
 * 投放活动Service实现 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RtbCampaignServiceImpl implements RtbCampaignService {

  private final RtbCampaignMapper campaignMapper;

  @Override
  @Transactional
  public Long create(CampaignCreateRequest request) {
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (currentUserId == null) {
      throw new RuntimeException("未登录或登录已过期");
    }

    // 校验时间范围
    if (request.getStartTime().isBefore(LocalDateTime.now())) {
      throw new RuntimeException("开始时间不能早于当前时间");
    }
    if (request.getEndTime().isBefore(request.getStartTime())) {
      throw new RuntimeException("结束时间必须晚于开始时间");
    }
    long days = ChronoUnit.DAYS.between(request.getStartTime(), request.getEndTime());
    if (days > 90) {
      throw new RuntimeException("活动时长不能超过90天");
    }

    // 校验预算
    if (request.getLifetimeBudget().compareTo(new BigDecimal("100")) < 0) {
      throw new RuntimeException("预算不能少于100元");
    }

    RtbCampaignDO campaign = new RtbCampaignDO();
    campaign.setAdvertiserId(currentUserId);
    campaign.setName(request.getName());
    campaign.setDescription(request.getDescription());
    campaign.setCampaignObjective(request.getCampaignObjective());
    campaign.setCampaignGoalType(request.getCampaignGoalType());
    campaign.setCampaignGoalValue(request.getCampaignGoalValue());
    campaign.setLifetimeBudget(request.getLifetimeBudget());
    campaign.setLifetimeBudgetUsed(BigDecimal.ZERO);
    campaign.setStartTime(request.getStartTime());
    campaign.setEndTime(request.getEndTime());
    campaign.setStatus(0); // 默认草稿
    campaign.setCreateTime(LocalDateTime.now());
    campaign.setUpdateTime(LocalDateTime.now());

    campaignMapper.insert(campaign);
    return campaign.getId();
  }

  @Override
  @Transactional
  public boolean update(Long id, CampaignUpdateRequest request) {
    RtbCampaignDO campaign = campaignMapper.selectById(id);
    if (campaign == null) {
      throw new RuntimeException("投放活动不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!campaign.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此投放活动");
    }

    // 进行中的活动修改关键字段需要重新审核（这里简化为允许修改）
    campaign.setName(request.getName());
    campaign.setDescription(request.getDescription());
    if (request.getCampaignObjective() != null) {
      campaign.setCampaignObjective(request.getCampaignObjective());
    }
    if (request.getCampaignGoalType() != null) {
      campaign.setCampaignGoalType(request.getCampaignGoalType());
    }
    if (request.getCampaignGoalValue() != null) {
      campaign.setCampaignGoalValue(request.getCampaignGoalValue());
    }
    if (request.getLifetimeBudget() != null) {
      campaign.setLifetimeBudget(request.getLifetimeBudget());
    }
    if (request.getStartTime() != null) {
      campaign.setStartTime(request.getStartTime());
    }
    if (request.getEndTime() != null) {
      campaign.setEndTime(request.getEndTime());
    }
    campaign.setUpdateTime(LocalDateTime.now());

    return campaignMapper.updateById(campaign) > 0;
  }

  @Override
  @Transactional
  public boolean delete(Long id) {
    RtbCampaignDO campaign = campaignMapper.selectById(id);
    if (campaign == null) {
      throw new RuntimeException("投放活动不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!campaign.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此投放活动");
    }

    // 只有草稿状态可以删除
    if (!Objects.equals(campaign.getStatus(), 0)) {
      throw new RuntimeException("只有草稿状态的投放活动可以删除");
    }

    return campaignMapper.deleteById(id) > 0;
  }

  @Override
  public CampaignVO detail(Long id) {
    RtbCampaignDO campaign = campaignMapper.selectById(id);
    if (campaign == null) {
      return null;
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!campaign.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限查看此投放活动");
    }

    CampaignVO vo = new CampaignVO();
    vo.setId(campaign.getId());
    vo.setAdvertiserId(campaign.getAdvertiserId());
    vo.setName(campaign.getName());
    vo.setDescription(campaign.getDescription());
    vo.setCampaignObjective(campaign.getCampaignObjective());
    vo.setCampaignObjectiveName(CampaignObjectiveEnum.getNameByCode(campaign.getCampaignObjective()));

    if (campaign.getCampaignGoalType() != null) {
      vo.setCampaignGoalType(campaign.getCampaignGoalType());
      vo.setCampaignGoalTypeName(getGoalTypeName(campaign.getCampaignGoalType()));
    }
    vo.setCampaignGoalValue(campaign.getCampaignGoalValue());

    vo.setLifetimeBudget(campaign.getLifetimeBudget());
    vo.setLifetimeBudgetUsed(campaign.getLifetimeBudgetUsed());

    // 计算消耗百分比
    if (campaign.getLifetimeBudget().compareTo(BigDecimal.ZERO) > 0) {
      int percent = campaign.getLifetimeBudgetUsed().multiply(new BigDecimal("100"))
          .divide(campaign.getLifetimeBudget(), 0, RoundingMode.HALF_UP).intValue();
      vo.setUsedPercent(percent);
    }

    // 计算剩余预算
    vo.setRemainingBudget(campaign.getLifetimeBudget().subtract(campaign.getLifetimeBudgetUsed()));

    vo.setStartTime(campaign.getStartTime());
    vo.setEndTime(campaign.getEndTime());

    // 计算投放天数
    long days = ChronoUnit.DAYS.between(campaign.getStartTime(), campaign.getEndTime()) + 1;
    vo.setDurationDays((int) days);

    vo.setStatus(campaign.getStatus());
    vo.setStatusName(CampaignStatusEnum.getNameByCode(campaign.getStatus()));

    // 计算显示状态（包含已完成判断）
    setDisplayStatus(vo, campaign);

    // TODO: 查询广告组数量
    vo.setAdGroupCount(0);

    vo.setCreateTime(campaign.getCreateTime());
    vo.setUpdateTime(campaign.getUpdateTime());
    vo.setCreateBy(campaign.getCreateBy());
    vo.setUpdateBy(campaign.getUpdateBy());

    return vo;
  }

  @Override
  public Result<List<CampaignListVO>> list(CampaignQueryRequest request) {
    Page<RtbCampaignDO> page = new Page<>(request.getCurrent(), request.getSize());

    // 获取当前登录用户的ID（即advertiserId）
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (currentUserId == null) {
      return Result.error("未登录或登录已过期");
    }

    LambdaQueryWrapper<RtbCampaignDO> wrapper = new LambdaQueryWrapper<>();
    // 强制过滤：只能查看当前登录用户的投放活动
    wrapper.eq(RtbCampaignDO::getAdvertiserId, currentUserId)
        .like(request.getName() != null, RtbCampaignDO::getName, request.getName())
        .eq(request.getCampaignObjective() != null, RtbCampaignDO::getCampaignObjective, request.getCampaignObjective())
        .eq(request.getStatus() != null, RtbCampaignDO::getStatus, request.getStatus())
        .orderByDesc(RtbCampaignDO::getCreateTime);

    campaignMapper.selectPage(page, wrapper);

    List<CampaignListVO> list = page.getRecords().stream().map(campaign -> {
      CampaignListVO vo = new CampaignListVO();
      vo.setId(campaign.getId());
      vo.setName(campaign.getName());
      vo.setCampaignObjective(campaign.getCampaignObjective());
      vo.setCampaignObjectiveName(CampaignObjectiveEnum.getNameByCode(campaign.getCampaignObjective()));

      vo.setLifetimeBudget(campaign.getLifetimeBudget());
      vo.setLifetimeBudgetUsed(campaign.getLifetimeBudgetUsed());

      // 计算消耗百分比
      if (campaign.getLifetimeBudget().compareTo(BigDecimal.ZERO) > 0) {
        int percent = campaign.getLifetimeBudgetUsed().multiply(new BigDecimal("100"))
            .divide(campaign.getLifetimeBudget(), 0, RoundingMode.HALF_UP).intValue();
        vo.setUsedPercent(percent);
      }

      vo.setStatus(campaign.getStatus());
      vo.setStatusName(CampaignStatusEnum.getNameByCode(campaign.getStatus()));

      // 计算显示状态（包含已完成判断）
      setDisplayStatus(vo, campaign);

      vo.setStartTime(campaign.getStartTime());
      vo.setEndTime(campaign.getEndTime());

      // 时间范围文本
      vo.setTimeRange(formatDate(campaign.getStartTime()) + "-" + formatDate(campaign.getEndTime()));

      vo.setCreateTime(campaign.getCreateTime());
      return vo;
    }).collect(Collectors.toList());

    PageInfo pageInfo = PageInfo.of(page.getCurrent(), page.getSize(), page.getTotal());
    return Result.success(list, pageInfo);
  }

  @Override
  @Transactional
  public boolean updateStatus(Long id, Integer status) {
    RtbCampaignDO campaign = campaignMapper.selectById(id);
    if (campaign == null) {
      throw new RuntimeException("投放活动不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!campaign.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此投放活动");
    }

    // 状态只能是：0=草稿/1=进行中/2=暂停
    if (status < 0 || status > 2) {
      throw new RuntimeException("无效的状态值");
    }

    campaign.setStatus(status);
    campaign.setUpdateTime(LocalDateTime.now());
    return campaignMapper.updateById(campaign) > 0;
  }

  @Override
  @Transactional
  public boolean start(Long id) {
    RtbCampaignDO campaign = campaignMapper.selectById(id);
    if (campaign == null) {
      throw new RuntimeException("投放活动不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!campaign.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此投放活动");
    }

    // 只有草稿或暂停状态可以启动
    if (!Objects.equals(campaign.getStatus(), 0) && !Objects.equals(campaign.getStatus(), 2)) {
      throw new RuntimeException("只有草稿或暂停状态的活动可以启动");
    }

    campaign.setStatus(1); // 进行中
    campaign.setUpdateTime(LocalDateTime.now());
    return campaignMapper.updateById(campaign) > 0;
  }

  @Override
  @Transactional
  public boolean pause(Long id) {
    RtbCampaignDO campaign = campaignMapper.selectById(id);
    if (campaign == null) {
      throw new RuntimeException("投放活动不存在");
    }

    // 权限校验
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (!campaign.getAdvertiserId().equals(currentUserId)) {
      throw new RuntimeException("无权限操作此投放活动");
    }

    // 只有进行中状态可以暂停
    if (!Objects.equals(campaign.getStatus(), 1)) {
      throw new RuntimeException("只有进行中的活动可以暂停");
    }

    campaign.setStatus(2); // 暂停
    campaign.setUpdateTime(LocalDateTime.now());
    return campaignMapper.updateById(campaign) > 0;
  }

  /**
   * 格式化日期为 MM.dd
   */
  private String formatDate(LocalDateTime dateTime) {
    return String.format("%02d.%02d", dateTime.getMonthValue(), dateTime.getDayOfMonth());
  }

  /**
   * 计算显示状态（包含已完成判断） 优先级：预算耗尽 > 投放时间耗尽 > 待开始 > 进行中 > 暂停 > 草稿
   */
  private void setDisplayStatus(CampaignListVO vo, RtbCampaignDO campaign) {
    LocalDateTime now = LocalDateTime.now();
    boolean budgetExhausted = campaign.getLifetimeBudgetUsed().compareTo(campaign.getLifetimeBudget()) >= 0;
    boolean timeExpired = now.isAfter(campaign.getEndTime());
    boolean notStarted = now.isBefore(campaign.getStartTime());

    // 1. 预算耗尽（最高优先级）
    if (budgetExhausted) {
      vo.setDisplayStatusName("预算耗尽");
      vo.setDisplayStatusType("danger");
      return;
    }

    // 2. 投放时间耗尽
    if (timeExpired) {
      vo.setDisplayStatusName("投放时间耗尽");
      vo.setDisplayStatusType("");
      return;
    }

    // 3. 根据数据库状态判断
    switch(campaign.getStatus()) {
    case 0:
      vo.setDisplayStatusName("草稿");
      vo.setDisplayStatusType("info");
      break;
    case 1:
      // 进行中状态，判断是否待开始
      if (notStarted) {
        vo.setDisplayStatusName("待开始");
        vo.setDisplayStatusType("info");
      } else {
        vo.setDisplayStatusName("进行中");
        vo.setDisplayStatusType("success");
      }
      break;
    case 2:
      vo.setDisplayStatusName("暂停");
      vo.setDisplayStatusType("warning");
      break;
    default:
      vo.setDisplayStatusName("未知");
      vo.setDisplayStatusType("info");
    }
  }

  /**
   * 计算显示状态（CampaignVO 版本） 优先级：预算耗尽 > 投放时间耗尽 > 待开始 > 进行中 > 暂停 > 草稿
   */
  private void setDisplayStatus(CampaignVO vo, RtbCampaignDO campaign) {
    LocalDateTime now = LocalDateTime.now();
    boolean budgetExhausted = campaign.getLifetimeBudgetUsed().compareTo(campaign.getLifetimeBudget()) >= 0;
    boolean timeExpired = now.isAfter(campaign.getEndTime());
    boolean notStarted = now.isBefore(campaign.getStartTime());

    // 1. 预算耗尽（最高优先级）
    if (budgetExhausted) {
      vo.setDisplayStatusName("预算耗尽");
      vo.setDisplayStatusType("danger");
      return;
    }

    // 2. 投放时间耗尽
    if (timeExpired) {
      vo.setDisplayStatusName("投放时间耗尽");
      vo.setDisplayStatusType("");
      return;
    }

    // 3. 根据数据库状态判断
    switch(campaign.getStatus()) {
    case 0:
      vo.setDisplayStatusName("草稿");
      vo.setDisplayStatusType("info");
      break;
    case 1:
      // 进行中状态，判断是否待开始
      if (notStarted) {
        vo.setDisplayStatusName("待开始");
        vo.setDisplayStatusType("info");
      } else {
        vo.setDisplayStatusName("进行中");
        vo.setDisplayStatusType("success");
      }
      break;
    case 2:
      vo.setDisplayStatusName("暂停");
      vo.setDisplayStatusType("warning");
      break;
    default:
      vo.setDisplayStatusName("未知");
      vo.setDisplayStatusType("info");
    }
  }

  /**
   * 获取目标类型名称
   */
  private String getGoalTypeName(Integer type) {
    if (type == null)
      return null;
    switch(type) {
    case 1:
      return "展示";
    case 2:
      return "点击";
    case 3:
      return "转化";
    default:
      return "未知";
    }
  }
}
