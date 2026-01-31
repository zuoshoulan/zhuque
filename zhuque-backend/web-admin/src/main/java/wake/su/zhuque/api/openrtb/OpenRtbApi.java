package wake.su.zhuque.api.openrtb;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wake.su.zhuque.api.openrtb.dto.BidRequest;
import wake.su.zhuque.api.openrtb.dto.BidResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * OpenRTB 2.6 API 接口
 *
 * @author OpenRTB
 * @version 2.6
 * @see <a href="https://iabtechlab.com/standards/openrtb/">OpenRTB Specification</a>
 */
@Tag(name = "OpenRTB API", description = "OpenRTB 2.6 实时竞价接口")
@RestController
@RequestMapping("/api/openrtb")
public class OpenRtbApi {

  @Operation(summary = "竞价请求", description = "接收竞价请求并返回竞价响应")
  @PostMapping("/bid")
  public BidResponse bid(@RequestHeader(value = "x-openrtb-version", defaultValue = "2.6") String openRtbVersion,
      @RequestHeader(value = "Content-Type", defaultValue = "application/json") String contentType,
      @RequestBody BidRequest bidRequest) {
    // TODO: 实现竞价逻辑
    BidResponse response = new BidResponse();
    response.setId(bidRequest.getId());
    return response;
  }
}
