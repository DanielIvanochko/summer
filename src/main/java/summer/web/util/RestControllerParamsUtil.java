package summer.web.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import summer.web.servlet.SummerServlet;
import summer.web.servlet.annotation.RequestMapping;
import summer.web.servlet.mapping.RestControllerParams;
import summer.web.servlet.mapping.request.RequestParamsResolver;

public class RestControllerParamsUtil {
  public static Map<String, List<RestControllerParams>> getRestControllerParams(SummerServlet summerServlet, List<RequestParamsResolver> requestParamsResolvers) {
    Map<String, List<RestControllerParams>> restControllerParamsMap = new HashMap<>();
    String requestMappingPath = getRequestMapping(summerServlet);

    for (Method method: summerServlet.getClass().getMethods()) {
      requestParamsResolvers.stream()
            .filter(resolver -> method.isAnnotationPresent(resolver.getAnnotation()))
            .findFirst()
            .ifPresent(resolver -> resolver.handleAnnotation(summerServlet, method, requestMappingPath, restControllerParamsMap));
    }

    return restControllerParamsMap;
  }

  private static String getRequestMapping(SummerServlet servlet) {
    return Optional.ofNullable(servlet.getClass().getAnnotation(RequestMapping.class))
          .map(RequestMapping::path)
          .orElse("");
  }
}
