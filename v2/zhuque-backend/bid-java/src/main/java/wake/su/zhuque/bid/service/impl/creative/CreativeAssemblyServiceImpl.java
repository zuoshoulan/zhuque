package wake.su.zhuque.bid.service.impl.creative;

import org.springframework.stereotype.Service;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.creative.CreativeAssemblyService;
import wake.su.zhuque.model.entity.RtbAdDO;
import wake.su.zhuque.model.entity.RtbCreativeDO;

import cn.hutool.core.util.StrUtil;

/**
 * 创意组装服务实现
 *
 * @author zhuque
 * @version 1.0
 */
@Service
public class CreativeAssemblyServiceImpl implements CreativeAssemblyService {

  // 创意类型: 1=Banner, 2=Video, 3=Audio, 4=Native
  private static final int CREATIVE_TYPE_BANNER = 1;
  private static final int CREATIVE_TYPE_VIDEO = 2;
  private static final int CREATIVE_TYPE_AUDIO = 3;
  private static final int CREATIVE_TYPE_NATIVE = 4;

  @Override
  public String buildAdm(RtbAdDO ad, RtbCreativeDO creative, BidContext context) {
    if (creative == null) {
      return "";
    }

    // 简化实现：只支持 Banner
    return buildBannerAdm(ad, creative, context);
  }

  @Override
  public String buildClickUrl(String baseUrl, BidContext context) {
    if (StrUtil.isBlank(baseUrl)) {
      return "";
    }

    StringBuilder url = new StringBuilder(baseUrl);
    url.append("?utm_source=rtb");
    url.append("&request_id=").append(context.getRequest().getId());
    url.append("&imp_id=").append(context.getCurrentImp().getId());
    url.append("&timestamp=").append(System.currentTimeMillis());

    if (context.getUserId() != null) {
      url.append("&user_id=").append(context.getUserId());
    }

    return url.toString();
  }

  @Override
  public String buildImpressionUrl(String baseUrl, BidContext context) {
    if (StrUtil.isBlank(baseUrl)) {
      return "";
    }

    StringBuilder url = new StringBuilder(baseUrl);
    url.append("?event=impression");
    url.append("&request_id=").append(context.getRequest().getId());
    url.append("&imp_id=").append(context.getCurrentImp().getId());
    url.append("&timestamp=").append(System.currentTimeMillis());

    return url.toString();
  }

  @Override
  public String buildWinUrl(String baseUrl, BidContext context, Long price) {
    if (StrUtil.isBlank(baseUrl)) {
      return "";
    }

    StringBuilder url = new StringBuilder(baseUrl);
    url.append("?event=win");
    url.append("&request_id=").append(context.getRequest().getId());
    url.append("&imp_id=").append(context.getCurrentImp().getId());
    url.append("&price=").append(price);
    url.append("&timestamp=").append(System.currentTimeMillis());

    return url.toString();
  }

  /** 构建Banner ADM */
  private String buildBannerAdm(RtbAdDO ad, RtbCreativeDO creative, BidContext context) {
    String clickUrl = buildClickUrl(creative.getLandingPageUrl(), context);
    String impUrl = buildImpressionUrl("", context);
    // TODO: 从素材获取创意URL
    String creativeUrl = "https://via.placeholder.com/300x250";

    return String.format(
        """
                <a href="%s" target="_blank">
                    <img src="%s" alt="%s" border="0"/>
                </a>
                <img src="%s" width="1" height="1" style="display:none"/>
                """,
        clickUrl, creativeUrl, creative.getName(), impUrl);
  }

  /** 构建Video ADM (VAST XML) - TODO */
  @SuppressWarnings("unused")
  private String buildVideoAdm(RtbCreativeDO creative, BidContext context) {
    return "";
  }

  /** 构建Native ADM - TODO */
  @SuppressWarnings("unused")
  private String buildNativeAdm(RtbCreativeDO creative, BidContext context) {
    return "";
  }
}
