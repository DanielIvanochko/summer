package summer.web.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Component;
import summer.web.servlet.mapping.RestControllerParams;
import summer.web.servlet.mapping.request.RequestParamsResolver;
import summer.web.util.RestControllerParamsUtil;

@Component
public class RestControllerContext {
  private final List<SummerServlet> summerServlets;
  private final List<RequestParamsResolver> requestParamsResolvers;

  @Autowired
  public RestControllerContext(List<SummerServlet> summerServlets, List<RequestParamsResolver> requestParamsResolvers) {
    this.summerServlets = summerServlets;
    this.requestParamsResolvers = requestParamsResolvers;
  }

  public Map<String, List<RestControllerParams>> getParamsMap() {
    Map<String, List<RestControllerParams>> restControllerParams = new HashMap<>();
    Map<String, List<String>> methodToPathsMap = new HashMap<>();

    for (SummerServlet summerServlet: summerServlets) {
      Map<String, List<RestControllerParams>> servletParams = RestControllerParamsUtil.getRestControllerParams(summerServlet, requestParamsResolvers);
      servletParams.forEach((key, value) -> addRestControllerParams(key, value, restControllerParams));
    }

    return restControllerParams;
  }

  private void addRestControllerParams(String method, List<RestControllerParams> restControllerParams, Map<String, List<RestControllerParams>> allRestControllerParams) {
    allRestControllerParams.computeIfAbsent(method, v -> new ArrayList<>())
          .addAll(restControllerParams);
  }
}
