package summer.web.servlet.mapping.response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletResponse;
import summer.core.context.annotation.Component;
import summer.web.servlet.annotation.ResponseStatus;

@Component
public class ResponseStatusAnnotationResolver implements ResponseAnnotationResolver {
  @Override
  public Class<? extends Annotation> getAnnotation() {
    return ResponseStatus.class;
  }

  @Override
  public HttpServletResponse handleAnnotation(HttpServletResponse response, Method method) {
    ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);
    int status = responseStatus.value().getValue();
    response.setStatus(status);
    return response;
  }
}
