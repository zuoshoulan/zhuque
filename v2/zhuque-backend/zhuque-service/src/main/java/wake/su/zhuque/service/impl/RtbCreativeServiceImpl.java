package wake.su.zhuque.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.dao.mapper.*;
import wake.su.zhuque.model.dto.*;
import wake.su.zhuque.model.entity.*;
import wake.su.zhuque.model.enums.CreativeFormatEnum;
import wake.su.zhuque.model.enums.CreativeStatusEnum;
import wake.su.zhuque.model.vo.CreativeListVO;
import wake.su.zhuque.model.vo.CreativeVO;
import wake.su.zhuque.service.RtbCreativeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RtbCreativeServiceImpl implements RtbCreativeService {

    private final RtbCreativeMapper creativeMapper;
    private final RtbMaterialMapper materialMapper;

    @Override
    @Transactional
    public Long create(CreativeCreateRequest request) {
        RtbCreativeDO creative = new RtbCreativeDO();
        creative.setCreativeId("C" + IdUtil.getSnowflakeNextId());
        creative.setAdvertiserId(request.getAdvertiserId());
        creative.setName(request.getName());
        creative.setDescription(request.getDescription());
        creative.setFormat(request.getFormat());
        creative.setLandingPageUrl(request.getLandingPageUrl());
        creative.setDisplayUrl(request.getDisplayUrl());
        creative.setAdvertiserDomain(request.getAdvertiserDomain());
        creative.setCat(request.getCat() != null ? String.join(",", request.getCat()) : null);
        creative.setAttr(request.getAttr() != null ? request.getAttr().stream().map(String::valueOf).collect(Collectors.joining(",")) : null);
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
        vo.setFormat(creative.getFormat());
        vo.setFormatName(CreativeFormatEnum.getNameByCode(creative.getFormat()));
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
        Long count = materialMapper.selectCount(new LambdaQueryWrapper<RtbMaterialDO>()
            .eq(RtbMaterialDO::getCreativeId, id));
        vo.setMaterialCount(count.intValue());

        return vo;
    }

    @Override
    public Result<List<CreativeListVO>> list(CreativeQueryRequest request) {
        Page<RtbCreativeDO> page = new Page<>(request.getCurrent(), request.getSize());

        LambdaQueryWrapper<RtbCreativeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getAdvertiserId() != null, RtbCreativeDO::getAdvertiserId, request.getAdvertiserId())
               .like(request.getName() != null, RtbCreativeDO::getName, request.getName())
               .eq(request.getFormat() != null, RtbCreativeDO::getFormat, request.getFormat())
               .eq(request.getStatus() != null, RtbCreativeDO::getStatus, request.getStatus())
               .orderByDesc(RtbCreativeDO::getCreateTime);

        creativeMapper.selectPage(page, wrapper);

        Result<List<CreativeListVO>> result = new Result<>();
        result.setData(page.getRecords().stream().map(creative -> {
            CreativeListVO vo = new CreativeListVO();
            vo.setId(creative.getId());
            vo.setCreativeId(creative.getCreativeId());
            vo.setAdvertiserId(creative.getAdvertiserId());
            vo.setName(creative.getName());
            vo.setFormat(creative.getFormat());
            vo.setFormatName(CreativeFormatEnum.getNameByCode(creative.getFormat()));
            vo.setStatus(creative.getStatus());
            vo.setStatusName(CreativeStatusEnum.getNameByCode(creative.getStatus()));
            vo.setCreateTime(creative.getCreateTime());
            return vo;
        }).collect(Collectors.toList()));
        result.setPage(PageInfo.of(page.getCurrent(), page.getSize(), page.getTotal()));

        return result;
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
