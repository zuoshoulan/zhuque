package wake.su.zhuque.bid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.dto.openrtb.BidResponse;
import wake.su.zhuque.bid.service.RtbBidService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * OpenRTB 竞价接口
 *
 * @author zhuque
 * @version 1.0
 */
@Tag(name = "OpenRTB", description = "OpenRTB 2.6 实时竞价接口")
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
  @Operation(
      summary = "竞价请求",
      description =
          """
      接收 OpenRTB 2.6 竞价请求，返回竞价响应。
      - 返回 200：有竞价，返回 BidResponse
      - 返回 204：无竞价，不返回内容
      - 处理时限：建议在 100ms 内完成
      """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "OpenRTB 2.6 竞价请求",
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = BidRequest.class),
              examples = {
                @ExampleObject(
                    name = "基础请求",
                    description = "最基础的竞价请求示例",
                    value =
                        """
          {
            "id": "req-20250126-001",
            "imp": [
              {
                "id": "imp-001",
                "banner": {
                  "w": 320,
                  "h": 50,
                  "pos": 1
                },
                "bidfloor": 0.5
              }
            ],
            "device": {
              "ua": "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36",
              "ip": "210.73.204.1",
              "geo": {
                "country": "CN",
                "region": "Beijing",
                "city": "Beijing"
              },
              "devicetype": 1,
              "os": "Android"
            },
            "user": {
              "id": "user-001"
            },
            "test": 0,
            "at": 1,
            "tmax": 100
          }
          """)
              }))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "有竞价，返回 BidResponse",
        content =
            @Content(
                schema = @Schema(implementation = BidResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
          {
            "id": "req-20250126-001",
            "seatbid": [
              {
                "seat": "seat-001",
                "bid": [
                  {
                    "id": "bid-001",
                    "impid": "imp-001",
                    "price": 1.25,
                    "adid": "ad-001",
                    "nurl": "https://ad.zhuque.com/win/${AUCTION_PRICE}"
                  }
                ]
              }
            ],
            "bidid": "bid-20250126-001",
            "cur": "CNY"
          }
          """))),
    @ApiResponse(responseCode = "204", description = "无竞价，不返回内容"),
    @ApiResponse(responseCode = "400", description = "请求无效（缺少必要参数）"),
    @ApiResponse(responseCode = "500", description = "服务器内部错误")
  })
  @PostMapping(value = "/bid", produces = "application/json", consumes = "application/json")
  public ResponseEntity<BidResponse> bid(
      @Parameter(description = "OpenRTB 竞价请求", required = true) @RequestBody BidRequest request) {
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
      log.info(
          "[{}] 竞价处理完成, duration={}ms, result={}",
          requestId,
          duration,
          response != null ? "有竞价" : "无竞价");

      if (response == null) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("[{}] 竞价处理异常", requestId, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /** 健康检查 */
  @Operation(summary = "健康检查", description = "检查服务是否健康运行")
  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("OK");
  }

  /** 就绪检查 */
  @Operation(summary = "就绪检查", description = "检查服务是否就绪接收流量")
  @GetMapping("/ready")
  public ResponseEntity<String> ready() {
    return ResponseEntity.ok("Ready");
  }
}
