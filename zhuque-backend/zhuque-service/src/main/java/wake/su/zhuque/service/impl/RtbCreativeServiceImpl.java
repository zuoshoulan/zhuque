package wake.su.zhuque.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.common.util.JwtUtil;
import wake.su.zhuque.common.util.SecurityUtil;
import wake.su.zhuque.dao.mapper.*;
import wake.su.zhuque.model.dto.*;
import wake.su.zhuque.model.entity.*;
import wake.su.zhuque.model.enums.CreativeStatusEnum;
import wake.su.zhuque.model.vo.CreativeListVO;
import wake.su.zhuque.model.vo.CreativeVO;
import wake.su.zhuque.service.RtbCreativeService;

import cn.hutool.core.util.IdUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RtbCreativeServiceImpl implements RtbCreativeService {

  private final RtbCreativeMapper creativeMapper;
  private final RtbMaterialMapper materialMapper;
  private final JwtUtil jwtUtil;

  @Override
  @Transactional
  public Long create(CreativeCreateRequest request) {
    // 获取当前登录用户ID，直接使用 userId 作为 advertiserId
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      throw new RuntimeException("无法获取请求信息");
    }

    HttpServletRequest httpRequest = attributes.getRequest();
    String token = httpRequest.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    Long userId = jwtUtil.getUserId(token);

    RtbCreativeDO creative = new RtbCreativeDO();
    creative.setCreativeId("C" + IdUtil.getSnowflakeNextId());
    creative.setAdvertiserId(userId); // 直接使用用户ID作为广告主ID
    creative.setName(request.getName());
    creative.setDescription(request.getDescription());
    creative.setLandingPageUrl(request.getLandingPageUrl());
    creative.setDisplayUrl(request.getDisplayUrl());
    creative.setAdvertiserDomain(request.getAdvertiserDomain());
    // JSON字段：空数组转换为null，避免MySQL JSON字段报错
    creative
        .setCat(request.getCat() != null && !request.getCat().isEmpty() ? String.join(",", request.getCat()) : null);
    creative.setAttr(request.getAttr() != null && !request.getAttr().isEmpty()
        ? request.getAttr().stream().map(String::valueOf).collect(Collectors.joining(","))
        : null);
    creative.setLanguage(request.getLanguage());
    creative.setStatus(0); // 默认草稿
    creative.setStartTime(request.getStartTime());
    creative.setEndTime(request.getEndTime());
    creative.setCreateTime(LocalDateTime.now());
    creative.setUpdateTime(LocalDateTime.now());
    creativeMapper.insert(creative);
    return creative.getId();
  }

  @Override
  @Transactional
  public void update(CreativeUpdateRequest request) {
    RtbCreativeDO creative = creativeMapper.selectById(request.getId());
    if (creative == null) {
      throw new RuntimeException("创意不存在");
    }
    creative.setName(request.getName());
    creative.setDescription(request.getDescription());
    creative.setLandingPageUrl(request.getLandingPageUrl());
    creative.setDisplayUrl(request.getDisplayUrl());
    creative.setAdvertiserDomain(request.getAdvertiserDomain());
    creative.setStartTime(request.getStartTime());
    creative.setEndTime(request.getEndTime());
    creative.setUpdateTime(LocalDateTime.now());
    creativeMapper.updateById(creative);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    creativeMapper.deleteById(id);
  }

  @Override
  public CreativeVO detail(Long id) {
    RtbCreativeDO creative = creativeMapper.selectById(id);
    if (creative == null) {
      return null;
    }
    CreativeVO vo = new CreativeVO();
    vo.setId(creative.getId());
    vo.setCreativeId(creative.getCreativeId());
    vo.setAdvertiserId(creative.getAdvertiserId());
    vo.setName(creative.getName());
    vo.setDescription(creative.getDescription());
    vo.setLandingPageUrl(creative.getLandingPageUrl());
    vo.setDisplayUrl(creative.getDisplayUrl());
    vo.setAdvertiserDomain(creative.getAdvertiserDomain());
    vo.setLanguage(creative.getLanguage());
    vo.setStatus(creative.getStatus());
    vo.setStatusName(CreativeStatusEnum.getNameByCode(creative.getStatus()));
    vo.setStartTime(creative.getStartTime());
    vo.setEndTime(creative.getEndTime());
    vo.setCreateTime(creative.getCreateTime());
    vo.setUpdateTime(creative.getUpdateTime());
    vo.setCreateBy(creative.getCreateBy());
    vo.setUpdateBy(creative.getUpdateBy());

    // 查询素材数量
    Long count = materialMapper
        .selectCount(new LambdaQueryWrapper<RtbMaterialDO>().eq(RtbMaterialDO::getCreativeId, id));
    vo.setMaterialCount(count.intValue());

    return vo;
  }

  @Override
  public Result<List<CreativeListVO>> list(CreativeQueryRequest request) {
    Page<RtbCreativeDO> page = new Page<>(request.getCurrent(), request.getSize());

    // 获取当前登录用户的ID（即advertiserId）
    Long currentUserId = SecurityUtil.getCurrentUserId();
    if (currentUserId == null) {
      return Result.error("未登录或登录已过期");
    }

    LambdaQueryWrapper<RtbCreativeDO> wrapper = new LambdaQueryWrapper<>();
    // 强制过滤：只能查看当前登录用户的创意（userId即advertiserId）
    wrapper.eq(RtbCreativeDO::getAdvertiserId, currentUserId)
        .eq(request.getAdvertiserId() != null, RtbCreativeDO::getAdvertiserId, request.getAdvertiserId())
        .like(request.getName() != null, RtbCreativeDO::getName, request.getName())
        .eq(request.getStatus() != null, RtbCreativeDO::getStatus, request.getStatus())
        .orderByDesc(RtbCreativeDO::getCreateTime);

    creativeMapper.selectPage(page, wrapper);

    List<CreativeListVO> list = page.getRecords().stream().map(creative -> {
      CreativeListVO vo = new CreativeListVO();
      vo.setId(creative.getId());
      vo.setCreativeId(creative.getCreativeId());
      vo.setAdvertiserId(creative.getAdvertiserId());
      vo.setName(creative.getName());
      vo.setStatus(creative.getStatus());
      vo.setStatusName(CreativeStatusEnum.getNameByCode(creative.getStatus()));

      // 查询素材数量
      Long materialCount = materialMapper
          .selectCount(new LambdaQueryWrapper<RtbMaterialDO>().eq(RtbMaterialDO::getCreativeId, creative.getId()));
      vo.setMaterialCount(materialCount.intValue());

      vo.setCreateTime(creative.getCreateTime());
      return vo;
    }).collect(Collectors.toList());

    PageInfo pageInfo = PageInfo.of(page.getCurrent(), page.getSize(), page.getTotal());
    return Result.success(list, pageInfo);
  }

  @Override
  @Transactional
  public void updateStatus(Long id, Integer status) {
    RtbCreativeDO creative = creativeMapper.selectById(id);
    if (creative == null) {
      throw new RuntimeException("创意不存在");
    }
    creative.setStatus(status);
    creative.setUpdateTime(LocalDateTime.now());
    creativeMapper.updateById(creative);
  }
}
