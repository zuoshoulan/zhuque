package wake.su.zhuque.bid.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * OpenRTB 2.6 Impression Object
 */
public class Imp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("banner")
    private Banner banner;

    @JsonProperty("video")
    private Video video;

    @JsonProperty("audio")
    private Audio audio;

    @JsonProperty("native")
    private NativeX nativeX;

    @JsonProperty("bidfloor")
    private BigDecimal bidFloor;

    @JsonProperty("bidfloorcur")
    private String bidFloorCurrency;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public NativeX getNativeX() {
        return nativeX;
    }

    public void setNativeX(NativeX nativeX) {
        this.nativeX = nativeX;
    }

    public BigDecimal getBidFloor() {
        return bidFloor;
    }

    public void setBidFloor(BigDecimal bidFloor) {
        this.bidFloor = bidFloor;
    }

    public String getBidFloorCurrency() {
        return bidFloorCurrency;
    }

    public void setBidFloorCurrency(String bidFloorCurrency) {
        this.bidFloorCurrency = bidFloorCurrency;
    }

    // 内部类定义
    public static class Banner {
        @JsonProperty("w")
        private Integer width;

        @JsonProperty("h")
        private Integer height;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }

    public static class Video {
    }

    public static class Audio {
    }

    public static class NativeX {
    }
}
