package summer.web.servlet.mapping.request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import summer.core.context.annotation.Component;
import summer.web.servlet.annotation.PutMapping;
import summer.web.servlet.annotation.RequestMethod;
import summer.web.servlet.mapping.RestControllerParams;

@Component
public class PutRequestParamsResolver implements RequestParamsResolver {
  @Override
  public Class<? extends Annotation> getAnnotation() {
    return PutMapping.class;
  }

  @Override
  public void handleAnnotation(Object instance, Method method, String requestMappingPath, Map<String, List<RestControllerParams>> restControllerParamsMap) {
    PutMapping annotation = method.getAnnotation(PutMapping.class);
    String methodPath = getMethodPathWithoutVariables(annotation.path());
    String path = requestMappingPath + methodPath;
    RestControllerParams params = new RestControllerParams(instance,
          method, RequestMethod.PUT, path);
    List<RestControllerParams> putMethodParamsList = restControllerParamsMap.getOrDefault(RequestMethod.PUT.name(), new ArrayList<>());
    addSorted(params, putMethodParamsList);
    restControllerParamsMap.put(RequestMethod.PUT.name(), putMethodParamsList);
  }
}
