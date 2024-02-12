package summer.web.servlet.mapping.request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import summer.core.context.annotation.Component;
import summer.web.servlet.annotation.GetMapping;
import summer.web.servlet.annotation.RequestMethod;
import summer.web.servlet.mapping.RestControllerParams;

@Component
public class GetRequestParamsResolver implements RequestParamsResolver {
  @Override
  public Class<? extends Annotation> getAnnotation() {
    return GetMapping.class;
  }

  @Override
  public void handleAnnotation(Object instance, Method method, String requestMappingPath, Map<String, List<RestControllerParams>> restControllerParamsMap) {
    GetMapping annotation = method.getAnnotation(GetMapping.class);
    String methodPath = getMethodPathWithoutVariables(annotation.path());
    String path = requestMappingPath + methodPath;
    RestControllerParams params = new RestControllerParams(instance,
          method, RequestMethod.GET, path);
    List<RestControllerParams> getMethodParamsList = restControllerParamsMap.getOrDefault(RequestMethod.GET.name(), new ArrayList<>());
    addSorted(params, getMethodParamsList);
    restControllerParamsMap.put(RequestMethod.GET.name(), getMethodParamsList);
  }
}
