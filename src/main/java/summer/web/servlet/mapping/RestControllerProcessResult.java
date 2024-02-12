package summer.web.servlet.mapping;

import java.lang.reflect.Method;

public record RestControllerProcessResult(Method method, Object result) {
}
