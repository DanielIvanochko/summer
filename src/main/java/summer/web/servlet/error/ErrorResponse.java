package summer.web.servlet.error;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private LocalDateTime timestamp;
  private Integer code;
  private String status;
  private String message;
  private String stackTrace;
}
