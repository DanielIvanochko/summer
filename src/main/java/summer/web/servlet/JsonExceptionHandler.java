package summer.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ErrorReportValve;
import summer.web.server.config.ServerProperties;
import summer.web.servlet.annotation.ResponseStatus;
import summer.web.servlet.error.ErrorResponse;
import summer.web.servlet.error.ErrorResponseCreator;
import summer.web.servlet.http.MediaType;

import java.util.Optional;

@RequiredArgsConstructor
public class JsonExceptionHandler extends ErrorReportValve {
  private final ObjectMapper objectMapper;
  private final ErrorResponseCreator errorResponseCreator;
  private final ServerProperties serverProperties;

  @Override
  protected void report(Request request, Response response, Throwable throwable) {
    Optional.ofNullable(throwable).ifPresent(thr -> setErrorResponse(response, thr));
    super.report(request, response, throwable);
  }

  @SneakyThrows
  private void setErrorResponse(Response response, Throwable thr) {
    var errorResponse = prepareBody(thr);
    setHeaders(response, errorResponse);
    response.getWriter().println(objectMapper.writeValueAsString(errorResponse));
    response.finishResponse();
  }

  private void setHeaders(Response response, ErrorResponse errorResponse) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(MediaType.UTF8_VALUE);
    response.setStatus(errorResponse.getCode());
  }

  private ErrorResponse prepareBody(Throwable throwable) {
    Boolean withStackTrace = serverProperties.isWithStackTrace();
    Throwable cause = getCause(throwable);

    return Optional.ofNullable(cause)
        .map(Throwable::getClass)
        .map(clazz -> clazz.getAnnotation(ResponseStatus.class))
        .map(responseStatus -> errorResponseCreator.prepareErrorResponse(responseStatus, cause, withStackTrace))
        .orElseGet(() -> errorResponseCreator.defaultStackTrace(cause, withStackTrace));
  }

  private Throwable getCause(Throwable throwable) {
    return Optional.ofNullable(throwable.getCause()).orElse(throwable);
  }

}
