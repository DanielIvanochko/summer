package summer.core.context.processor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import summer.core.utils.ReflectionsHelper;
import summer.core.context.annotation.BeanProcessor;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
