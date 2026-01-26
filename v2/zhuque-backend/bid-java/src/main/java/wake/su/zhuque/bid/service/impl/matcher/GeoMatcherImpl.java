package wake.su.zhuque.bid.service.impl.matcher;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.matcher.GeoMatcher;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

/**
 * 地域匹配器
 *
 * @author zhuque
 * @version 1.0
 */
@Service
public class GeoMatcherImpl implements GeoMatcher {

  @Override
  public boolean matches(BidContext context, RtbAdGroupDO adGroup) {
    String targetingGeo = adGroup.getTargetingGeo();
    if (targetingGeo == null || targetingGeo.isEmpty() || "[]".equals(targetingGeo)) {
      // 未设置地域定向，不限制
      return true;
    }

    String requestCountry = context.getCountryCode();
    String requestRegion = context.getRegionCode();

    if (requestCountry == null) {
      // 请求没有地域信息，不匹配
      return false;
    }

    // 解析定向配置: ["CN", "CN-11", "CN-31"]
    JSONArray geoArray = JSONUtil.parseArray(targetingGeo);
    List<String> countries = new ArrayList<>();
    List<String> regions = new ArrayList<>();

    for (int i = 0; i < geoArray.size(); i++) {
      String geo = geoArray.getStr(i);
      if (geo.contains("-")) {
        regions.add(geo);
      } else {
        countries.add(geo);
      }
    }

    // 检查国家匹配
    if (!countries.isEmpty() && !countries.contains(requestCountry)) {
      return false;
    }

    // 检查省份匹配
    if (!regions.isEmpty()) {
      String regionKey = requestCountry + "-" + requestRegion;
      boolean regionMatch = regions.contains(regionKey);
      if (!regionMatch) {
        return false;
      }
    }

    return true;
  }
}
