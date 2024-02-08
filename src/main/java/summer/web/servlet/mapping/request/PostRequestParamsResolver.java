package summer.web.servlet.mapping.request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import summer.core.context.annotation.Component;
import summer.web.servlet.annotation.PostMapping;
import summer.web.servlet.annotation.RequestMethod;
import summer.web.servlet.mapping.RestControllerParams;

@Component
public class PostRequestParamsResolver implements RequestParamsResolver {
  @Override
  public Class<? extends Annotation> getAnnotation() {
    return PostMapping.class;
  }

  @Override
  public void handleAnnotation(Object instance, Method method, String requestMappingPath, Map<String, List<RestControllerParams>> restControllerParamsMap) {
    PostMapping annotation = method.getAnnotation(PostMapping.class);
    String methodPath = getMethodPathWithoutVariables(annotation.path());
    String path = requestMappingPath + methodPath;
    RestControllerParams params = new RestControllerParams(instance,
          method, RequestMethod.POST, path);
    List<RestControllerParams> postMethodParamsList = restControllerParamsMap.getOrDefault(RequestMethod.POST.name(), new ArrayList<>());
    addSorted(params, postMethodParamsList);
    restControllerParamsMap.put(RequestMethod.POST.name(), postMethodParamsList);
  }
}
