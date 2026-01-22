package wake.su.zhuque.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 图片工具类
 * 用于解析图片尺寸等信息
 */
public class ImageUtils {

    /**
     * 图片尺寸信息
     */
    @Data
    @AllArgsConstructor
    public static class ImageDimension {
        private Integer width;
        private Integer height;
    }

    /**
     * 从字节数组中读取图片尺寸
     * 支持的格式：jpg, png, gif, bmp, wbmp 等
     *
     * @param fileData 文件字节数组
     * @return 图片尺寸，如果解析失败返回 null
     */
    public static ImageDimension getImageDimension(byte[] fileData) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return new ImageDimension(image.getWidth(), image.getHeight());
            }
        } catch (IOException e) {
            // 不是支持的图片格式或读取失败
        }
        return null;
    }

    /**
     * 判断文件是否为图片（根据MIME类型）
     *
     * @param mimeType 文件MIME类型
     * @return 是否为图片
     */
    public static boolean isImage(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("image/");
    }
}
