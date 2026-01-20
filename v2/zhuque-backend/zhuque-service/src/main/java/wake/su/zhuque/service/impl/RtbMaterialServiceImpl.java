package wake.su.zhuque.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wake.su.zhuque.dao.mapper.*;
import wake.su.zhuque.model.dto.MaterialCreateRequest;
import wake.su.zhuque.model.dto.MaterialQueryRequest;
import wake.su.zhuque.model.dto.MaterialUpdateRequest;
import wake.su.zhuque.model.dto.PageResult;
import wake.su.zhuque.model.entity.*;
import wake.su.zhuque.model.enums.CreativeFormatEnum;
import wake.su.zhuque.model.vo.MaterialListVO;
import wake.su.zhuque.model.vo.MaterialVO;
import wake.su.zhuque.service.RtbMaterialService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RtbMaterialServiceImpl implements RtbMaterialService {

    private final RtbMaterialMapper materialMapper;
    private final RtbMaterialBannerMapper bannerMapper;
    private final RtbMaterialVideoMapper videoMapper;
    private final RtbMaterialAudioMapper audioMapper;
    private final RtbMaterialNativeMapper nativeMapper;
    private final RtbCreativeMapper creativeMapper;

    @Override
    @Transactional
    public Long create(MaterialCreateRequest request) {
        // 1. 创建主表
        RtbMaterialDO material = new RtbMaterialDO();
        material.setMaterialId("M" + IdUtil.getSnowflakeNextId());
        material.setCreativeId(request.getCreativeId());
        material.setName(request.getName());
        material.setFormat(request.getFormat() != null ? request.getFormat() : 1);
        material.setWidth(request.getWidth());
        material.setHeight(request.getHeight());
        material.setFileId(request.getFileId());
        material.setFileType(request.getFileType());
        material.setMimes(request.getMimes() != null ? String.join(",", request.getMimes()) : null);
        material.setDur(request.getDur());
        material.setCreateTime(LocalDateTime.now());
        material.setUpdateTime(LocalDateTime.now());
        materialMapper.insert(material);

        // 2. 根据format创建扩展表
        Integer format = request.getFormat() != null ? request.getFormat() : 1;
        if (format == 1) {
            // Banner
            RtbMaterialBannerDO banner = new RtbMaterialBannerDO();
            banner.setMaterialId(material.getId());
            banner.setPos(request.getPos());
            banner.setBtype(request.getBtype() != null ? request.getBtype().toString() : null);
            bannerMapper.insert(banner);
        } else if (format == 2) {
            // Video
            RtbMaterialVideoDO video = new RtbMaterialVideoDO();
            video.setMaterialId(material.getId());
            video.setLinearity(request.getLinearity() != null ? request.getLinearity() : 1);
            video.setStartdelay(request.getStartdelay());
            video.setPlaybackend(request.getPlaybackend());
            videoMapper.insert(video);
        } else if (format == 3) {
            // Audio
            RtbMaterialAudioDO audio = new RtbMaterialAudioDO();
            audio.setMaterialId(material.getId());
            audio.setSequence(request.getAudioSequence());
            audioMapper.insert(audio);
        } else if (format == 4) {
            // Native
            RtbMaterialNativeDO nat = new RtbMaterialNativeDO();
            nat.setMaterialId(material.getId());
            nat.setRequestJson(request.getNativeRequestJson());
            nativeMapper.insert(nat);
        }

        return material.getId();
    }

    @Override
    @Transactional
    public void update(MaterialUpdateRequest request) {
        RtbMaterialDO material = materialMapper.selectById(request.getId());
        if (material == null) {
            throw new RuntimeException("素材不存在");
        }
        material.setName(request.getName());
        material.setUpdateTime(LocalDateTime.now());
        materialMapper.updateById(material);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        materialMapper.deleteById(id);
    }

    @Override
    public MaterialVO detail(Long id) {
        RtbMaterialDO material = materialMapper.selectById(id);
        if (material == null) {
            return null;
        }

        // 查询创意名称
        RtbCreativeDO creative = creativeMapper.selectById(material.getCreativeId());
        String creativeName = creative != null ? creative.getName() : "";

        MaterialVO vo = new MaterialVO();
        vo.setId(material.getId());
        vo.setMaterialId(material.getMaterialId());
        vo.setCreativeId(material.getCreativeId());
        vo.setCreativeName(creativeName);
        vo.setName(material.getName());
        vo.setFormat(material.getFormat());
        vo.setFormatName(CreativeFormatEnum.getNameByCode(material.getFormat()));
        vo.setWidth(material.getWidth());
        vo.setHeight(material.getHeight());
        vo.setFileSize(material.getFileSize());
        vo.setFileType(material.getFileType());
        vo.setFileUrl("/api/file/" + material.getFileId());
        vo.setDur(material.getDur());
        vo.setCreateTime(material.getCreateTime());
        vo.setUpdateTime(material.getUpdateTime());

        // 查询扩展表
        if (material.getFormat() == 1) {
            RtbMaterialBannerDO banner = bannerMapper.selectOne(
                new LambdaQueryWrapper<RtbMaterialBannerDO>()
                    .eq(RtbMaterialBannerDO::getMaterialId, id)
            );
            if (banner != null) {
                Map<String, Object> ext = new HashMap<>();
                ext.put("pos", banner.getPos());
                ext.put("btype", banner.getBtype());
                vo.setBannerExt(ext);
            }
        } else if (material.getFormat() == 2) {
            RtbMaterialVideoDO video = videoMapper.selectOne(
                new LambdaQueryWrapper<RtbMaterialVideoDO>()
                    .eq(RtbMaterialVideoDO::getMaterialId, id)
            );
            if (video != null) {
                Map<String, Object> ext = new HashMap<>();
                ext.put("linearity", video.getLinearity());
                ext.put("startdelay", video.getStartdelay());
                ext.put("playbackend", video.getPlaybackend());
                vo.setVideoExt(ext);
            }
        }

        return vo;
    }

    @Override
    public PageResult<MaterialListVO> list(MaterialQueryRequest request) {
        Page<RtbMaterialDO> page = new Page<>(request.getCurrent(), request.getSize());

        LambdaQueryWrapper<RtbMaterialDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getCreativeId() != null, RtbMaterialDO::getCreativeId, request.getCreativeId())
               .eq(request.getFormat() != null, RtbMaterialDO::getFormat, request.getFormat())
               .eq(request.getWidth() != null, RtbMaterialDO::getWidth, request.getWidth())
               .eq(request.getHeight() != null, RtbMaterialDO::getHeight, request.getHeight())
               .orderByDesc(RtbMaterialDO::getCreateTime);

        materialMapper.selectPage(page, wrapper);

        PageResult<MaterialListVO> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(material -> {
            MaterialListVO vo = new MaterialListVO();
            vo.setId(material.getId());
            vo.setMaterialId(material.getMaterialId());
            vo.setCreativeId(material.getCreativeId());
            vo.setName(material.getName());
            vo.setFormat(material.getFormat());
            vo.setFormatName(CreativeFormatEnum.getNameByCode(material.getFormat()));
            vo.setWidth(material.getWidth());
            vo.setHeight(material.getHeight());
            vo.setFileUrl("/api/file/" + material.getFileId());
            vo.setCreateTime(material.getCreateTime());
            return vo;
        }).collect(java.util.stream.Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());

        return result;
    }

    @Override
    public String upload(MultipartFile file) {
        // TODO: 实现文件上传到OSS
        return "oss_" + System.currentTimeMillis();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        RtbMaterialDO material = new RtbMaterialDO();
        material.setId(id);
        material.setStatus(status);
        materialMapper.updateById(material);
    }
}
