package wake.su.zhuque.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import wake.su.zhuque.common.core.result.PageInfo;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.dao.mapper.*;
import wake.su.zhuque.model.dto.MaterialCreateRequest;
import wake.su.zhuque.model.dto.MaterialQueryRequest;
import wake.su.zhuque.model.dto.MaterialUpdateRequest;
import wake.su.zhuque.model.entity.*;
import wake.su.zhuque.model.enums.MaterialFormatEnum;
import wake.su.zhuque.model.vo.MaterialListVO;
import wake.su.zhuque.model.vo.MaterialVO;
import wake.su.zhuque.service.RtbFileService;
import wake.su.zhuque.service.RtbMaterialService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
    private final RtbFileService fileService;
    private final RtbFileMapper fileMapper;

    @Override
    @Transactional
    public Long create(MaterialCreateRequest request) {
        // 从文件记录中获取文件信息
        RtbFileDO fileRecord = null;
        if (request.getFileId() != null) {
            fileRecord = fileMapper.selectById(request.getFileId());
            if (fileRecord == null) {
                throw new RuntimeException("文件不存在: " + request.getFileId());
            }
        }

        // 1. 创建主表
        RtbMaterialDO material = new RtbMaterialDO();
        material.setMaterialId("M" + IdUtil.getSnowflakeNextId());
        material.setCreativeId(request.getCreativeId());
        material.setName(request.getName());
        material.setFormat(request.getFormat() != null ? request.getFormat() : 1);
        material.setWidth(request.getWidth());
        material.setHeight(request.getHeight());
        material.setFileId(request.getFileId()); // 直接使用 fileId
        material.setFileType(fileRecord != null ? fileRecord.getFileType() : null);
        material.setFileSize(fileRecord != null ? fileRecord.getFileSize() : null);
        material.setMimes(request.getMimes() != null ? String.join(",", request.getMimes()) : null);
        material.setDur(request.getDur());
        material.setCreateTime(LocalDateTime.now());
        material.setUpdateTime(LocalDateTime.now());
        materialMapper.insert(material);

        // 2. 根据format创建扩展表
        Integer format = request.getFormat() != null ? request.getFormat() : 1;
        if (format == 1) {
            // Banner
            if (request.getBannerExt() != null) {
                RtbMaterialBannerDO banner = new RtbMaterialBannerDO();
                banner.setMaterialId(material.getId());
                banner.setPos(request.getBannerExt().getPos());
                banner.setBtype(request.getBannerExt().getBtype() != null
                    ? request.getBannerExt().getBtype().toString() : null);
                banner.setWmode(request.getBannerExt().getWmode());
                banner.setExt(request.getBannerExt().getExt());
                bannerMapper.insert(banner);
            }
        } else if (format == 2) {
            // Video
            if (request.getVideoExt() != null) {
                RtbMaterialVideoDO video = new RtbMaterialVideoDO();
                video.setMaterialId(material.getId());
                video.setLinearity(request.getVideoExt().getLinearity() != null
                    ? request.getVideoExt().getLinearity() : 1);
                video.setSequence(request.getVideoExt().getSequence());
                video.setMinDuration(request.getVideoExt().getMinDuration());
                video.setMaxDuration(request.getVideoExt().getMaxDuration());
                video.setStartdelay(request.getVideoExt().getStartdelay());
                video.setSkip(request.getVideoExt().getSkip());
                video.setSkipmin(request.getVideoExt().getSkipmin());
                video.setSkipafter(request.getVideoExt().getSkipafter());
                video.setPlacement(request.getVideoExt().getPlacement());
                video.setPlaybackend(request.getVideoExt().getPlaybackend());
                video.setPlayableafter(request.getVideoExt().getPlayableafter());
                video.setPodid(request.getVideoExt().getPodid());
                video.setPodsize(request.getVideoExt().getPodsize());
                video.setPodseq(request.getVideoExt().getPodseq());
                video.setMincpmpersec(request.getVideoExt().getMincpmpersec());
                video.setMaxseq(request.getVideoExt().getMaxseq());
                video.setRender(request.getVideoExt().getRender());
                video.setApi(request.getVideoExt().getApi() != null
                    ? request.getVideoExt().getApi().stream()
                        .map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) : null);
                video.setExt(request.getVideoExt().getExt());
                videoMapper.insert(video);
            }
        } else if (format == 3) {
            // Audio
            if (request.getAudioExt() != null) {
                RtbMaterialAudioDO audio = new RtbMaterialAudioDO();
                audio.setMaterialId(material.getId());
                audio.setSequence(request.getAudioExt().getSequence());
                audio.setMinDuration(request.getAudioExt().getMinDuration());
                audio.setMaxDuration(request.getAudioExt().getMaxDuration());
                audio.setStartdelay(request.getAudioExt().getStartdelay());
                audio.setApi(request.getAudioExt().getApi() != null
                    ? request.getAudioExt().getApi().stream()
                        .map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) : null);
                audio.setExt(request.getAudioExt().getExt());
                audioMapper.insert(audio);
            }
        } else if (format == 4) {
            // Native
            if (request.getNativeExt() != null) {
                RtbMaterialNativeDO nat = new RtbMaterialNativeDO();
                nat.setMaterialId(material.getId());
                nat.setRequestJson(request.getNativeExt().getRequestJson());
                nat.setVer(request.getNativeExt().getVer());
                nat.setExt(request.getNativeExt().getExt());
                nativeMapper.insert(nat);
            }
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

        // 注意：素材类型（format）不可变，扩展表的更新基于创建时的format

        // 从文件记录中获取文件信息（如果更新了文件）
        RtbFileDO fileRecord = null;
        if (request.getFileId() != null) {
            fileRecord = fileMapper.selectById(request.getFileId());
            if (fileRecord == null) {
                throw new RuntimeException("文件不存在: " + request.getFileId());
            }
        }

        // 更新主表
        if (request.getName() != null) {
            material.setName(request.getName());
        }
        if (request.getWidth() != null) {
            material.setWidth(request.getWidth());
        }
        if (request.getHeight() != null) {
            material.setHeight(request.getHeight());
        }
        if (request.getFileId() != null && fileRecord != null) {
            material.setFileId(request.getFileId()); // 直接使用 fileId
            material.setFileType(fileRecord.getFileType());
            material.setFileSize(fileRecord.getFileSize());
        }
        if (request.getMimes() != null) {
            material.setMimes(String.join(",", request.getMimes()));
        }
        if (request.getDur() != null) {
            material.setDur(request.getDur());
        }
        material.setUpdateTime(LocalDateTime.now());
        materialMapper.updateById(material);

        // 更新扩展表（删除旧的，创建新的）
        Integer format = material.getFormat();
        if (format == 1) {
            // Banner
            if (request.getBannerExt() != null) {
                bannerMapper.delete(new LambdaQueryWrapper<RtbMaterialBannerDO>()
                    .eq(RtbMaterialBannerDO::getMaterialId, material.getId()));
                RtbMaterialBannerDO banner = new RtbMaterialBannerDO();
                banner.setMaterialId(material.getId());
                banner.setPos(request.getBannerExt().getPos());
                banner.setBtype(request.getBannerExt().getBtype() != null
                    ? request.getBannerExt().getBtype().toString() : null);
                banner.setWmode(request.getBannerExt().getWmode());
                banner.setExt(request.getBannerExt().getExt());
                bannerMapper.insert(banner);
            }
        } else if (format == 2) {
            // Video
            if (request.getVideoExt() != null) {
                videoMapper.delete(new LambdaQueryWrapper<RtbMaterialVideoDO>()
                    .eq(RtbMaterialVideoDO::getMaterialId, material.getId()));
                RtbMaterialVideoDO video = new RtbMaterialVideoDO();
                video.setMaterialId(material.getId());
                video.setLinearity(request.getVideoExt().getLinearity() != null
                    ? request.getVideoExt().getLinearity() : 1);
                video.setSequence(request.getVideoExt().getSequence());
                video.setMinDuration(request.getVideoExt().getMinDuration());
                video.setMaxDuration(request.getVideoExt().getMaxDuration());
                video.setStartdelay(request.getVideoExt().getStartdelay());
                video.setSkip(request.getVideoExt().getSkip());
                video.setSkipmin(request.getVideoExt().getSkipmin());
                video.setSkipafter(request.getVideoExt().getSkipafter());
                video.setPlacement(request.getVideoExt().getPlacement());
                video.setPlaybackend(request.getVideoExt().getPlaybackend());
                video.setPlayableafter(request.getVideoExt().getPlayableafter());
                video.setPodid(request.getVideoExt().getPodid());
                video.setPodsize(request.getVideoExt().getPodsize());
                video.setPodseq(request.getVideoExt().getPodseq());
                video.setMincpmpersec(request.getVideoExt().getMincpmpersec());
                video.setMaxseq(request.getVideoExt().getMaxseq());
                video.setRender(request.getVideoExt().getRender());
                video.setApi(request.getVideoExt().getApi() != null
                    ? request.getVideoExt().getApi().stream()
                        .map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) : null);
                video.setExt(request.getVideoExt().getExt());
                videoMapper.insert(video);
            }
        } else if (format == 3) {
            // Audio
            if (request.getAudioExt() != null) {
                audioMapper.delete(new LambdaQueryWrapper<RtbMaterialAudioDO>()
                    .eq(RtbMaterialAudioDO::getMaterialId, material.getId()));
                RtbMaterialAudioDO audio = new RtbMaterialAudioDO();
                audio.setMaterialId(material.getId());
                audio.setSequence(request.getAudioExt().getSequence());
                audio.setMinDuration(request.getAudioExt().getMinDuration());
                audio.setMaxDuration(request.getAudioExt().getMaxDuration());
                audio.setStartdelay(request.getAudioExt().getStartdelay());
                audio.setApi(request.getAudioExt().getApi() != null
                    ? request.getAudioExt().getApi().stream()
                        .map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) : null);
                audio.setExt(request.getAudioExt().getExt());
                audioMapper.insert(audio);
            }
        } else if (format == 4) {
            // Native
            if (request.getNativeExt() != null) {
                nativeMapper.delete(new LambdaQueryWrapper<RtbMaterialNativeDO>()
                    .eq(RtbMaterialNativeDO::getMaterialId, material.getId()));
                RtbMaterialNativeDO nat = new RtbMaterialNativeDO();
                nat.setMaterialId(material.getId());
                nat.setRequestJson(request.getNativeExt().getRequestJson());
                nat.setVer(request.getNativeExt().getVer());
                nat.setExt(request.getNativeExt().getExt());
                nativeMapper.insert(nat);
            }
        }
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
        vo.setFormatName(MaterialFormatEnum.getNameByCode(material.getFormat()));
        vo.setWidth(material.getWidth());
        vo.setHeight(material.getHeight());
        vo.setFileSize(material.getFileSize());
        vo.setFileType(material.getFileType());

        // 管理后台使用 id
        if (material.getFileId() != null) {
            vo.setFileUrl("/api/file/by-id/" + material.getFileId());
        }

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
    public Result<List<MaterialListVO>> list(MaterialQueryRequest request) {
        Page<RtbMaterialDO> page = new Page<>(request.getCurrent(), request.getSize());

        LambdaQueryWrapper<RtbMaterialDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getCreativeId() != null, RtbMaterialDO::getCreativeId, request.getCreativeId())
               .eq(request.getFormat() != null, RtbMaterialDO::getFormat, request.getFormat())
               .eq(request.getWidth() != null, RtbMaterialDO::getWidth, request.getWidth())
               .eq(request.getHeight() != null, RtbMaterialDO::getHeight, request.getHeight())
               .orderByDesc(RtbMaterialDO::getCreateTime);

        materialMapper.selectPage(page, wrapper);

        List<MaterialListVO> list = page.getRecords().stream().map(material -> {
            MaterialListVO vo = new MaterialListVO();
            vo.setId(material.getId());
            vo.setMaterialId(material.getMaterialId());
            vo.setCreativeId(material.getCreativeId());

            // 查询创意名称
            RtbCreativeDO creative = creativeMapper.selectById(material.getCreativeId());
            vo.setCreativeName(creative != null ? creative.getName() : "");

            vo.setName(material.getName());
            vo.setFormat(material.getFormat());
            vo.setFormatName(MaterialFormatEnum.getNameByCode(material.getFormat()));
            vo.setWidth(material.getWidth());
            vo.setHeight(material.getHeight());
            vo.setFileUrl("/api/file/" + material.getFileId());
            vo.setCreateTime(material.getCreateTime());
            return vo;
        }).collect(java.util.stream.Collectors.toList());

        PageInfo pageInfo = PageInfo.of(page.getCurrent(), page.getSize(), page.getTotal());
        return Result.success(list, pageInfo);
    }

    @Override
    public String upload(MultipartFile file) {
        // 委托给文件服务处理
        return fileService.upload(file);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        // 素材不需要单独的状态管理，状态通过创意来控制
        // 这个方法暂时保留为空实现
    }
}
