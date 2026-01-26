package wake.su.zhuque.bid.service.impl.matcher;

import java.util.List;

import org.springframework.stereotype.Service;

import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.matcher.DeviceMatcher;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

/**
 * 设备匹配器实现 @ @author zhuque
 *
 * @version 1.0
 */
@Service
public class DeviceMatcherImpl implements DeviceMatcher {

  // 设备类型: 1=手机, 2=平板, 3=桌面, 4=TV
  private static final int DEVICE_PHONE = 1;
  private static final int DEVICE_TABLET = 2;
  private static final int DEVICE_DESKTOP = 3;
  private static final int DEVICE_TV = 4;

  @Override
  public boolean matches(BidContext context, RtbAdGroupDO adGroup) {
    Integer requestDeviceType = context.getDeviceType();
    String requestOs = context.getOs();

    // 检查设备定向
    String targetingDevice = adGroup.getTargetingDevice();
    if (targetingDevice != null && !targetingDevice.isEmpty() && !"[]".equals(targetingDevice)) {
      JSONArray deviceArray = JSONUtil.parseArray(targetingDevice);
      List<Integer> allowedDevices = deviceArray.toList(Integer.class);

      if (requestDeviceType == null || !allowedDevices.contains(requestDeviceType)) {
        return false;
      }
    }

    // 检查OS定向
    String targetingOs = adGroup.getTargetingOs();
    if (targetingOs != null && !targetingOs.isEmpty() && !"[]".equals(targetingOs)) {
      JSONArray osArray = JSONUtil.parseArray(targetingOs);
      List<String> allowedOs = osArray.toList(String.class);

      if (requestOs == null || !isOsMatch(requestOs, allowedOs)) {
        return false;
      }
    }

    return true;
  }

  /** 检查OS是否匹配（支持模糊匹配） */
  private boolean isOsMatch(String requestOs, List<String> allowedOs) {
    String normalizedOs = requestOs.toLowerCase();

    for (String allowed : allowedOs) {
      String allowedLower = allowed.toLowerCase();
      if (normalizedOs.contains(allowedLower) || allowedLower.contains(normalizedOs)) {
        return true;
      }
    }
    return false;
  }
}
