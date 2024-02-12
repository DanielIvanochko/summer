package summer.web.servlet.mapping;

import java.lang.reflect.Method;

import summer.web.servlet.annotation.RequestMethod;

public record RestControllerParams(Object instance, Method method, RequestMethod requestMethod, String path) {
}
