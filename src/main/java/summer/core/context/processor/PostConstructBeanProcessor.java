package summer.core.context.processor;


import summer.core.context.annotation.BeanProcessor;
import summer.core.context.annotation.PostConstruct;
import summer.core.context.exception.PostProcessException;
import summer.core.utils.ReflectionsHelper;

import java.lang.reflect.Method;

@BeanProcessor
public class PostConstructBeanProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessBean(String beanName, Object bean) {
    Method[] methods = bean.getClass().getMethods();

    try {
      ReflectionsHelper.processBeanPostProcessorAnnotation(bean, methods, PostConstruct.class);
    } catch (Exception e) {
      throw new PostProcessException("Exception occured during post processing bean " + beanName + " , exception:" + e.getMessage());
    }

    return BeanPostProcessor.super.postProcessBean(beanName, bean);
  }
}
