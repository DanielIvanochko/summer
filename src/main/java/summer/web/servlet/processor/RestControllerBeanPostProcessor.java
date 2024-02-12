package summer.web.servlet.processor;

import summer.web.servlet.exception.MissingServletImplException;
import summer.core.context.annotation.BeanProcessor;
import summer.core.context.processor.BeanPostProcessor;
import summer.web.servlet.SummerServlet;
import summer.web.servlet.annotation.RestController;

@BeanProcessor
public class RestControllerBeanPostProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessBean(String beanName, Object bean) {

    Class<?> beanClass = bean.getClass();

    if (beanClass.isAnnotationPresent(RestController.class) &&
      !SummerServlet.class.isAssignableFrom(bean.getClass())) {
      throw new MissingServletImplException("Rest controller " + beanClass.getSimpleName() + " should implement SummerServlet interface");
    }

    return BeanPostProcessor.super.postProcessBean(beanName, bean);
  }
}
