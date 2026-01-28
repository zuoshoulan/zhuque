package wake.su.zhuque.common.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 图片工具类 用于解析图片和视频尺寸等信息
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
   * 从字节数组中读取图片尺寸 支持的格式：jpg, png, gif, bmp, wbmp 等
   *
   * @param fileData
   *          文件字节数组
   * @return 图片尺寸，如果解析失败返回 null
   */
  public static ImageDimension getImageDimension(byte[] fileData) {
    try(ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData)) {
      BufferedImage image = ImageIO.read(inputStream);
      if(image != null) {
        return new ImageDimension(image.getWidth(), image.getHeight());
      }
    } catch(IOException e) {
      // 不是支持的图片格式或读取失败
    }
    return null;
  }

  /**
   * 从字节数组中读取视频尺寸 使用 JavaCV (FFmpeg) 解析视频
   *
   * @param fileData
   *          文件字节数组
   * @return 视频尺寸，如果解析失败返回 null
   */
  public static ImageDimension getVideoDimension(byte[] fileData) {
    // 先将字节数组写入临时文件
    java.io.File tempFile = null;
    try {
      tempFile = java.io.File.createTempFile("video_", ".tmp");
      java.nio.file.Files.write(tempFile.toPath(), fileData);

      // 使用 FFprobe 解析视频信息
      return getVideoDimensionFromFile(tempFile);
    } catch(IOException e) {
      // 解析失败
      return null;
    } finally {
      // 清理临时文件
      if(tempFile != null && tempFile.exists()) {
        tempFile.delete();
      }
    }
  }

  /**
   * 从视频文件中读取尺寸
   *
   * @param videoFile
   *          视频文件
   * @return 视频尺寸，如果解析失败返回 null
   */
  private static ImageDimension getVideoDimensionFromFile(java.io.File videoFile) {
    try {
      // 使用 JavaCV 的 FFmpeg 解析
      FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
      grabber.start();

      int width = grabber.getImageWidth();
      int height = grabber.getImageHeight();

      grabber.stop();
      grabber.close();

      if(width > 0 && height > 0) {
        return new ImageDimension(width, height);
      }
    } catch(Exception e) {
      // 解析失败，返回 null
    }
    return null;
  }

  /**
   * 判断文件是否为图片（根据MIME类型）
   *
   * @param mimeType
   *          文件MIME类型
   * @return 是否为图片
   */
  public static boolean isImage(String mimeType) {
    if(mimeType == null) {
      return false;
    }
    return mimeType.startsWith("image/");
  }

  /**
   * 判断文件是否为视频（根据MIME类型）
   *
   * @param mimeType
   *          文件MIME类型
   * @return 是否为视频
   */
  public static boolean isVideo(String mimeType) {
    if(mimeType == null) {
      return false;
    }
    return mimeType.startsWith("video/");
  }
}
