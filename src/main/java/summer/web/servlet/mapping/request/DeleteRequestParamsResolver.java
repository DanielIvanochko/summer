package summer.web.servlet.mapping.request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import summer.core.context.annotation.Component;
import summer.web.servlet.annotation.DeleteMapping;
import summer.web.servlet.annotation.RequestMethod;
import summer.web.servlet.mapping.RestControllerParams;

@Component
public class DeleteRequestParamsResolver implements RequestParamsResolver {
  @Override
  public Class<? extends Annotation> getAnnotation() {
    return DeleteMapping.class;
  }

  @Override
  public void handleAnnotation(Object instance, Method method, String requestMappingPath, Map<String, List<RestControllerParams>> restControllerParamsMap) {
    DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
    String methodPath = getMethodPathWithoutVariables(annotation.path());
    String path = requestMappingPath + methodPath;
    RestControllerParams params = new RestControllerParams(instance,
          method, RequestMethod.DELETE, path);
    List<RestControllerParams> deleteMethodParamsList = restControllerParamsMap.getOrDefault(RequestMethod.DELETE.name(), new ArrayList<>());
    addSorted(params, deleteMethodParamsList);
    restControllerParamsMap.put(RequestMethod.DELETE.name(), deleteMethodParamsList);
  }
}
