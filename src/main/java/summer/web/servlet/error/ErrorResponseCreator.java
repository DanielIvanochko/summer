package summer.web.servlet.error;

import summer.core.context.annotation.Component;
import summer.web.servlet.annotation.ResponseStatus;
import summer.web.servlet.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

@Component
public class ErrorResponseCreator {
  public ErrorResponse prepareErrorResponse(ResponseStatus responseStatus, Throwable throwable, Boolean withStackTrace) {
    return ErrorResponse.builder()
        .code(responseStatus.value().getValue())
        .message(getMessage(responseStatus, throwable))
        .stackTrace(getStackTrace(throwable, withStackTrace))
        .status(responseStatus.value().getReasonPhrase())
        .timestamp(LocalDateTime.now())
        .build();
  }

  public ErrorResponse defaultStackTrace(Throwable throwable, Boolean withStackTrace) {
    return ErrorResponse.builder()
        .stackTrace(getStackTrace(throwable, withStackTrace))
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .code(HttpStatus.INTERNAL_SERVER_ERROR.getValue())
        .message(throwable.getMessage())
        .build();
  }

  private String getMessage(ResponseStatus responseStatus, Throwable throwable) {
    return responseStatus.reason().isEmpty() ? throwable.getMessage() : responseStatus.reason();
  }

  private String getStackTrace(Throwable throwable, Boolean withStackTrace) {
    return Boolean.TRUE.equals(withStackTrace) ? getStackTrace(throwable) : null;
  }

  private String getStackTrace(Throwable throwable) {
    return Arrays.stream(throwable.getStackTrace())
        .map(StackTraceElement::toString)
        .collect(Collectors.joining(lineSeparator()));
  }
}
