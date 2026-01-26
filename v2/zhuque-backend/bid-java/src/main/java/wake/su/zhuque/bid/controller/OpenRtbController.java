package wake.su.zhuque.bid.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.dto.openrtb.BidResponse;
import wake.su.zhuque.bid.service.RtbBidService;

/**
 * OpenRTB 竞价接口
 *
 * @author zhuque
 * @version 1.0
 */
@Tag(name = "OpenRTB", description = "OpenRTB 2.6 竞价接口")
@RestController
@RequestMapping("/openrtb")
@RequiredArgsConstructor
public class OpenRtbController {

    private static final Logger log = LoggerFactory.getLogger(OpenRtbController.class);

    private final RtbBidService rtbBidService;

    /**
     * 竞价接口
     *
     * @param request OpenRTB BidRequest
     * @return BidResponse 有竞价, 204 No Content 无竞价
     */
    @Operation(summary = "竞价请求", description = "接收 OpenRTB 2.6 竞价请求，返回竞价响应")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "有竞价，返回 BidResponse",
                content = @Content(schema = @Schema(implementation = BidResponse.class))),
        @ApiResponse(responseCode = "204", description = "无竞价，不返回内容"),
        @ApiResponse(responseCode = "400", description = "请求无效"),
        @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @PostMapping(value = "/bid", produces = "application/json")
    public ResponseEntity<BidResponse> bid(@RequestBody BidRequest request) {
        long startTime = System.currentTimeMillis();
        String requestId = request != null ? request.getId() : "unknown";

        log.info("[{}] 收到竞价请求", requestId);

        try {
            // 校验请求
            if (request == null || request.getImp() == null || request.getImp().isEmpty()) {
                log.warn("[{}] 请求无效: 缺少展示机会", requestId);
                return ResponseEntity.badRequest().build();
            }

            // 处理竞价
            BidResponse response = rtbBidService.processBid(request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] 竞价处理完成, duration={}ms, result={}",
                    requestId, duration, response != null ? "有竞价" : "无竞价");

            if (response == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[{}] 竞价处理异常", requestId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 健康检查
     */
    @Operation(summary = "健康检查", description = "检查服务是否健康运行")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    /**
     * 就绪检查
     */
    @Operation(summary = "就绪检查", description = "检查服务是否就绪接收流量")
    @GetMapping("/ready")
    public ResponseEntity<String> ready() {
        return ResponseEntity.ok("Ready");
    }
}
