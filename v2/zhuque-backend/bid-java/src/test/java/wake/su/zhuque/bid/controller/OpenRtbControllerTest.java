package wake.su.zhuque.bid.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import wake.su.zhuque.bid.dto.openrtb.BidRequest;
import wake.su.zhuque.bid.dto.openrtb.BidResponse;
import wake.su.zhuque.bid.helper.OpenRtbTestDataLoader;
import wake.su.zhuque.bid.service.RtbBidService;

/** OpenRTB Controller API 测试 */
@WebMvcTest(OpenRtbController.class)
public class OpenRtbControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RtbBidService rtbBidService;

  private BidRequest validBidRequest;

  @BeforeEach
  void setUp() {
    validBidRequest = OpenRtbTestDataLoader.minimalRequest();
  }

  @Test
  @DisplayName("POST /openrtb/bid - 有效请求返回竞价响应")
  void testBidRequestWithValidResponse() throws Exception {
    // 模拟服务返回竞价响应
    BidResponse mockResponse = new BidResponse();
    mockResponse.setId(validBidRequest.getId());
    when(rtbBidService.processBid(any(BidRequest.class))).thenReturn(mockResponse);

    mockMvc
        .perform(post("/openrtb/bid").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validBidRequest)))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(validBidRequest.getId()));

    verify(rtbBidService, times(1)).processBid(any(BidRequest.class));
  }

  @Test
  @DisplayName("POST /openrtb/bid - 无竞价返回204")
  void testBidRequestWithNoBid() throws Exception {
    // 模拟服务返回 null (无竞价)
    when(rtbBidService.processBid(any(BidRequest.class))).thenReturn(null);

    mockMvc
        .perform(
            post("/openrtb/bid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBidRequest)))
        .andExpect(status().isNoContent());

    verify(rtbBidService, times(1)).processBid(any(BidRequest.class));
  }

  @Test
  @DisplayName("POST /openrtb/bid - 空imp数组返回400")
  void testBidRequestWithEmptyImp() throws Exception {
    BidRequest request = OpenRtbTestDataLoader.noImpRequest();

    mockMvc.perform(post("/openrtb/bid").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());

    // 服务不应该被调用
    verify(rtbBidService, never()).processBid(any(BidRequest.class));
  }

  @Test
  @DisplayName("POST /openrtb/bid - null请求返回400")
  void testBidRequestWithNullBody() throws Exception {
    mockMvc.perform(post("/openrtb/bid").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /openrtb/bid - 无效JSON返回400")
  void testBidRequestWithInvalidJson() throws Exception {
    mockMvc
        .perform(
            post("/openrtb/bid").contentType(MediaType.APPLICATION_JSON).content("{invalid json"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /openrtb/bid - 服务异常返回500")
  void testBidRequestWithServiceException() throws Exception {
    // 模拟服务抛出异常
    when(rtbBidService.processBid(any(BidRequest.class)))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc
        .perform(
            post("/openrtb/bid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBidRequest)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("GET /openrtb/health - 健康检查")
  void testHealthCheck() throws Exception {
    mockMvc.perform(get("/openrtb/health")).andExpect(status().isOk())
        .andExpect(content().string("OK"));
  }

  @Test
  @DisplayName("GET /openrtb/ready - 就绪检查")
  void testReadyCheck() throws Exception {
    mockMvc.perform(get("/openrtb/ready")).andExpect(status().isOk())
        .andExpect(content().string("Ready"));
  }

  @Test
  @DisplayName("POST /openrtb/bid - 完整请求处理")
  void testFullBidRequest() throws Exception {
    BidRequest fullRequest = OpenRtbTestDataLoader.fullRequest();

    BidResponse mockResponse = new BidResponse();
    mockResponse.setId(fullRequest.getId());
    when(rtbBidService.processBid(any(BidRequest.class))).thenReturn(mockResponse);

    mockMvc
        .perform(post("/openrtb/bid").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(fullRequest)))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(fullRequest.getId()));
  }

  @Test
  @DisplayName("POST /openrtb/bid - Content-Type不是JSON返回415")
  void testBidRequestWithWrongContentType() throws Exception {
    mockMvc.perform(post("/openrtb/bid").contentType(MediaType.TEXT_PLAIN).content("test"))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  @DisplayName("POST /openrtb/bid - 视频广告请求")
  void testVideoBidRequest() throws Exception {
    BidRequest videoRequest = OpenRtbTestDataLoader.videoRequest();

    BidResponse mockResponse = new BidResponse();
    mockResponse.setId(videoRequest.getId());
    when(rtbBidService.processBid(any(BidRequest.class))).thenReturn(mockResponse);

    mockMvc.perform(post("/openrtb/bid").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(videoRequest))).andExpect(status().isOk());
  }
}
