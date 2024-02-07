package summer.core.context.injection;

import lombok.RequiredArgsConstructor;

import summer.core.context.AnnotationBeanRegistry;
import summer.core.context.ClassPathScannerFactory;
import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Value;
import summer.core.utils.ReflectionsHelper;
import summer.core.utils.ValuePropertiesResolver;
import summer.core.context.exception.NoSuchBeanException;
import summer.core.context.exception.SummerException;

import java.lang.reflect.Field;

@RequiredArgsConstructor
public class FieldBasedInjection {
  private final AnnotationBeanRegistry beanRegistry;
  private final ClassPathScannerFactory classPathScannerFactory;

  public void injectFields(Object bean) {
    Field[] fields = bean.getClass().getDeclaredFields();
    for (Field field: fields) {
      if (field.isAnnotationPresent(Autowired.class)) {
        setBeanDependency(field, bean);
      } else if (field.isAnnotationPresent(Value.class)) {
        setValueDependency(field, bean);
      }
    }
  }

  private void setValueDependency(Field field, Object bean) {
    Object dependency = ValuePropertiesResolver.resolveValueForField(field, field.getAnnotation(Value.class), beanRegistry.getProperties());
    try {
      ReflectionsHelper.setField(bean, field, dependency);
    } catch (IllegalAccessException e) {
      throw new SummerException("Exception occurred while injection property " + field.getType().getSimpleName() + " in class " + bean.getClass().getSimpleName());
    }
  }

  private void setBeanDependency(Field field, Object bean) {
    String dependencyName = classPathScannerFactory.resolveBeanName(field.getType());
    checkFieldDependency(dependencyName, field);
    Object dependency = beanRegistry.getOrCreateBean(dependencyName);
    try {
      ReflectionsHelper.setField(bean, field, dependency);
    } catch (IllegalAccessException e) {
      throw new SummerException("Exception occurred while injecting field " + field.getType().getSimpleName() + " in class " + bean.getClass().getSimpleName());
    }
  }

  private void checkFieldDependency(String dependencyName, Field field) {
    if (dependencyName == null) {
      if (field.getType().isInterface()) {
        throw new NoSuchBeanException("There is no bean that implements " + field.getType().getSimpleName());
      }
      throw new NoSuchBeanException("Cannot find the bean with type : " + field.getType().getSimpleName());
    }
  }
}
