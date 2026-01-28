package wake.su.zhuque.bid.helper;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import wake.su.zhuque.bid.dto.openrtb.BidRequest;

/** OpenRTB 测试数据加载器 从 classpath 资源文件加载测试数据 */
public class OpenRtbTestDataLoader {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String OPENRTB_DATA_PATH = "/openrtb/";

  /**
   * 加载 BidRequest 测试数据
   *
   * @param fileName
   *          文件名 (如: bid_request_minimal.json)
   * @return BidRequest 对象
   */
  public static BidRequest loadBidRequest(String fileName) {
    String fullPath = OPENRTB_DATA_PATH + fileName;
    try(InputStream inputStream = OpenRtbTestDataLoader.class.getResourceAsStream(fullPath)) {
      if(inputStream == null) {
        throw new IllegalArgumentException("测试数据文件不存在: " + fullPath);
      }
      return objectMapper.readValue(inputStream, BidRequest.class);
    } catch(IOException e) {
      throw new RuntimeException("加载测试数据失败: " + fullPath, e);
    }
  }

  /** 预定义的测试数据加载方法 */
  public static BidRequest minimalRequest() {
    return loadBidRequest("bid_request_minimal.json");
  }

  public static BidRequest fullRequest() {
    return loadBidRequest("bid_request_full.json");
  }

  public static BidRequest videoRequest() {
    return loadBidRequest("bid_request_video.json");
  }

  public static BidRequest noImpRequest() {
    return loadBidRequest("bid_request_no_imp.json");
  }

  public static BidRequest noGeoRequest() {
    return loadBidRequest("bid_request_no_geo.json");
  }
}
