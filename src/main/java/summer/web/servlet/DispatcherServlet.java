package summer.web.servlet;

import static summer.web.util.ParameterTypeUtils.parseToParameterType;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Component;
import summer.core.context.exception.SummerException;
import summer.core.utils.ReflectionsHelper;
import summer.web.servlet.annotation.PathVariable;
import summer.web.servlet.annotation.RequestBody;
import summer.web.servlet.annotation.RequestHeader;
import summer.web.servlet.annotation.RequestParam;
import summer.web.servlet.annotation.ResponseStatus;
import summer.web.servlet.exception.MissingMappingException;
import summer.web.servlet.exception.MissingRequestParamException;
import summer.web.servlet.http.HttpHeaders;
import summer.web.servlet.http.HttpStatus;
import summer.web.servlet.http.ResponseEntity;
import summer.web.servlet.mapping.RestControllerParams;
import summer.web.servlet.mapping.RestControllerProcessResult;

@Component
@Slf4j
public class DispatcherServlet extends FrameworkServlet {

  private static final String REST_CONTROLLER_PARAMS = "REST_CONTROLLER_PARAMS";
  private final ObjectMapper objectMapper;

  @Autowired
  public DispatcherServlet(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void processRequest(HttpServletRequest req, HttpServletResponse resp) {
    log.info("Got a {} request by path: {}", req.getMethod(), req.getRequestURI());
    RestControllerParams restControllerParams = getRestControllerParams(req);
    processRestControllerRequest(restControllerParams, req, resp);
  }

  private void processRestControllerRequest(RestControllerParams params, HttpServletRequest req, HttpServletResponse resp) {
    String requestPath = getRequestPath(req);
    Object controller = params.instance();
    Method method = params.method();

    Object[] args = prepareArgs(req, resp, requestPath, method);
    getRestControllerProcessResult(controller, method, resp, args);
  }

  private void getRestControllerProcessResult(Object controller, Method method, HttpServletResponse resp, Object[] args) {
    try {
      Optional.ofNullable(method.invoke(controller, args))
            .ifPresent(result -> performResponse(new RestControllerProcessResult(method, result), resp));
    } catch (Exception e) {
      throw new SummerException(e.getMessage());
    }
  }

  @SneakyThrows
  private void performResponse(RestControllerProcessResult processResult, HttpServletResponse resp) {
    Object response = processResult.result();
    Object body;
    if (response instanceof ResponseEntity<?> entity) {
      body = processResponseEntity(resp, entity);
    } else {
      body = response;
    }

    processResponseStatusAnnotation(processResult, resp);

    try (PrintWriter writer = resp.getWriter()) {
      if(!isBodySimple(body)) {
        body = objectMapper.writeValueAsString(body);
      }
      writer.print(body);
      writer.flush();
    }
  }

  private boolean isBodySimple(Object body) {
    return body instanceof CharSequence || body instanceof Number
          || body instanceof Boolean || body instanceof Character;
  }

  private void processResponseStatusAnnotation(RestControllerProcessResult processResult, HttpServletResponse resp) {
    Method method = processResult.method();

    if (method.isAnnotationPresent(ResponseStatus.class)) {
      ResponseStatus annotation = method.getAnnotation(ResponseStatus.class);
      int status = annotation.value().getValue();
      resp.setStatus(status);
    }
  }

  private Object processResponseEntity(HttpServletResponse resp, ResponseEntity<?> entity) {
    HttpStatus httpStatus = entity.getHttpStatus();
    resp.setStatus(httpStatus.getValue());

    HttpHeaders headers = entity.getHeaders();
    Optional.ofNullable(headers)
          .ifPresent(httpHeaders -> httpHeaders.getHeaders().forEach(resp::setHeader));
    return entity.getBody();
  }

  private Object[] prepareArgs(HttpServletRequest req, HttpServletResponse resp, String requestPath, Method method) {
    if (method.getParameterCount() == 0) {
      return new Object[0];
    }

    Parameter[] parameters = method.getParameters();
    Object[] args = new Object[parameters.length];

    for (int i = 0; i < parameters.length; i++) {
      setParameterValue(req, resp, requestPath, method, parameters, args, i);
    }

    return args;
  }

  private void setParameterValue(HttpServletRequest req, HttpServletResponse resp, String requestPath, Method method, Parameter[] parameters, Object[] args, int index) {
    if (parameters[index].isAnnotationPresent(PathVariable.class)) {
      extractPathVariable(requestPath, args, parameters, index);
    } else if (parameters[index].isAnnotationPresent(RequestParam.class)) {
      extractRequestParam(req, method, args, index, parameters);
    } else if (parameters[index].isAnnotationPresent(RequestBody.class)) {
      extractRequestBody(req, args, parameters, index);
    } else if (parameters[index].isAnnotationPresent(RequestHeader.class)) {
      extractRequestHeaderParam(req, args, parameters, index);
    } else if (parameters[index].getType().equals(HttpServletRequest.class)) {
      args[index] = req;
    } else if (parameters[index].getType().equals(HttpServletResponse.class)) {
      args[index] = resp;
    }
  }

  private void extractRequestHeaderParam(HttpServletRequest req, Object[] args, Parameter[] parameters, int index) {
    RequestHeader annotation = parameters[index].getAnnotation(RequestHeader.class);
    String value = annotation.value();
    String header = req.getHeader(value);
    Class<?> type = parameters[index].getType();
    args[index] = parseToParameterType(header, type);
  }

  @SneakyThrows
  private void extractRequestBody(HttpServletRequest req, Object[] args, Parameter[] parameters, int index) {
    Class<?> clazz = parameters[index].getType();

    BufferedReader reader = req.getReader();
    if (clazz.equals(String.class)) {
      args[index] = reader.lines().collect(Collectors.joining());
    } else if(clazz.equals(Map.class)) {
      args[index] = objectMapper.readValue(reader, Map.class);
    } else if (clazz.equals(byte[].class)) {
      args[index] = objectMapper.readValue(req.getInputStream(), byte[].class);
    } else {
      args[index] = objectMapper.readValue(reader, clazz);
    }
  }

  private void extractRequestParam(HttpServletRequest req, Method method, Object[] args, int index, Parameter[] parameters) {
    List<String> parameterNames = ReflectionsHelper.getParameterNames(method);
    String parameterName = parameterNames.get(index);
    Class<?> clazz = parameters[index].getType();
    String parameterValue = req.getParameter(parameterName);
    if (parameterValue != null) {
      args[index] = parseToParameterType(parameterValue, clazz);
    } else {
      throw new MissingRequestParamException(String.format("Required request parameter '%s' "
                  + "for method parameter type '%s' is not present",
            parameterName, clazz.getSimpleName()));
    }
  }

  private void extractPathVariable(String requestPath, Object[] args, Parameter[] parameters, int index) {
    int lastIndexOf = requestPath.lastIndexOf("/");
    String pathVariable = requestPath.substring(lastIndexOf + 1);
    Class<?> type = parameters[index].getType();
    args[index] = parseToParameterType(pathVariable, type);
  }

  private RestControllerParams getRestControllerParams(HttpServletRequest req) {
    String requestPath = getRequestPath(req);
    String method = req.getMethod();

    var restControllerParams = (Map<String, List<RestControllerParams>>) req.getServletContext().getAttribute(REST_CONTROLLER_PARAMS);

    return restControllerParams.getOrDefault(method, Collections.emptyList())
          .stream()
          .filter(params -> checkParams(requestPath, params))
          .findFirst()
          .orElseThrow(() -> new MissingMappingException("The application has no explicit mapping for " + getRequestPath(req)));
  }

  private boolean checkParams(String requestPath, RestControllerParams params) {
    Method method = params.method();
    Parameter[] parameters = method.getParameters();
    if (containsPathVariableAnnotation(parameters)) {
      String requestPathShortened = getShortenedPath(requestPath);
      return requestPathShortened.equals(params.path());
    }
    return requestPath.equals(params.path());
  }

  private String getShortenedPath(String requestPath) {
    return requestPath.substring(0, requestPath.lastIndexOf("/") + 1);
  }

  private boolean containsPathVariableAnnotation(Parameter[] parameters) {
    return Arrays.stream(parameters)
          .anyMatch(parameter -> parameter.isAnnotationPresent(PathVariable.class));
  }

  private String getRequestPath(HttpServletRequest req) {
    String contextPath = req.getContextPath();
    String requestUri = req.getRequestURI();

    if (requestUri.startsWith(contextPath)) {
      requestUri = requestUri.replace(contextPath, "");
    }
    return requestUri.equals("/") ? "" : requestUri;
  }
}
