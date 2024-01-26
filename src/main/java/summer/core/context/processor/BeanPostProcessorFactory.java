package summer.core.context.processor;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.reflections.Reflections;
import summer.core.context.annotation.BeanProcessor;
import summer.core.utils.ReflectionsHelper;

import java.util.List;
import java.util.stream.Collectors;

@Log4j
@Getter
public class BeanPostProcessorFactory {
  private final List<BeanPostProcessor> beanPostProcessors;

  public BeanPostProcessorFactory(Reflections reflections) {
    this.beanPostProcessors = reflections.getSubTypesOf(BeanPostProcessor.class)
        .stream()
        .filter(clazz -> clazz.isAnnotationPresent(BeanProcessor.class))
        .map(clazz -> clazz.cast(ReflectionsHelper.createObjectWithoutParameters(clazz)))
        .collect(Collectors.toList());
  }
}
