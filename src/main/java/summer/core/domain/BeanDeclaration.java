package summer.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeanDeclaration {
  private String factoryBeanName;
  private BeanScope scope;
  private BeanType type;
  private boolean isProxy;
  private Class<?> beanClass;
  private boolean isPrimary;
  private Method method;

  public boolean isPrototype() {
    return scope == BeanScope.PROTOTYPE;
  }

  public boolean isConfigurationBean() {
    return type == BeanType.CONFIGURATION;
  }
  public boolean isConfiguration() {
    return Objects.nonNull(method);
  }
}
