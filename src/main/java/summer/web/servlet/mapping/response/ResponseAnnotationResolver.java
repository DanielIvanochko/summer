package summer.web.servlet.mapping.response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletResponse;

public interface ResponseAnnotationResolver {
  Class<? extends Annotation> getAnnotation();
  HttpServletResponse handleAnnotation(HttpServletResponse response, Method method);
}
