package wake.su.zhuque.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.model.dto.MaterialCreateRequest;
import wake.su.zhuque.model.dto.MaterialQueryRequest;
import wake.su.zhuque.model.dto.MaterialUpdateRequest;
import wake.su.zhuque.model.vo.MaterialListVO;
import wake.su.zhuque.model.vo.MaterialVO;

/**
 * 素材Service接口
 */
public interface RtbMaterialService {

    /**
     * 创建素材
     */
    Long create(MaterialCreateRequest request);

    /**
     * 更新素材
     */
    void update(MaterialUpdateRequest request);

    /**
     * 删除素材
     */
    void delete(Long id);

    /**
     * 素材详情
     */
    MaterialVO detail(Long id);

    /**
     * 分页查询
     */
    Result<List<MaterialListVO>> list(MaterialQueryRequest request);

    /**
     * 上传文件
     */
    String upload(MultipartFile file);

    /**
     * 更新素材状态
     */
    void updateStatus(Long id, Integer status);
}
