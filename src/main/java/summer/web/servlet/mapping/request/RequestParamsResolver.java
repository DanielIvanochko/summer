package summer.web.servlet.mapping.request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import summer.web.servlet.annotation.PathVariable;
import summer.web.servlet.mapping.RestControllerParams;

public interface RequestParamsResolver {

  Class<? extends Annotation> getAnnotation();
  void handleAnnotation(Object instance, Method method, String requestMappingPath,
                        Map<String, List<RestControllerParams>> restControllerParamsMap);
  default String getMethodPathWithoutVariables(String path) {
    if (path.contains("{") && path.contains("}")) {
      path = path.substring(0, path.lastIndexOf("{"));
    }
    return path;
  }

  default void addSorted(RestControllerParams restControllerParams,
                         List<RestControllerParams> methodParamsList) {
    Parameter[] parameters = restControllerParams.method().getParameters();

    for (Parameter parameter: parameters) {
      if (parameter.isAnnotationPresent(PathVariable.class)) {
        methodParamsList.add(restControllerParams);
        return;
      }
    }
    methodParamsList.add(0, restControllerParams);
  }
}
