package wake.su.zhuque.common.web.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson 配置 支持多种日期时间格式解析
 */
@Configuration
public class JacksonConfig {

  private static final List<String> DATE_TIME_FORMATS = Arrays.asList("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss",
      "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss.SSS");

  /**
   * 自定义 LocalDateTime 反序列化器
   */
  public static class MultiFormatLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    public MultiFormatLocalDateTimeDeserializer() {
      super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt)
        throws IOException {
      String dateTimeStr = p.getValueAsString();
      if(dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
        return null;
      }

      // 尝试使用多种格式解析
      for(String format : DATE_TIME_FORMATS) {
        try {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
          return LocalDateTime.parse(dateTimeStr, formatter);
        } catch(DateTimeParseException e) {
          // 继续尝试下一种格式
        }
      }

      throw new IOException("无法解析日期时间: " + dateTimeStr + ", 支持的格式: " + DATE_TIME_FORMATS);
    }
  }

  /**
   * 自定义 Jackson ObjectMapper
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer customizer() {
    return builder -> {
      // 注册 JavaTimeModule
      JavaTimeModule javaTimeModule = new JavaTimeModule();
      // 添加自定义的反序列化器
      javaTimeModule.addDeserializer(LocalDateTime.class, new MultiFormatLocalDateTimeDeserializer());
      builder.modules(javaTimeModule);
    };
  }
}
