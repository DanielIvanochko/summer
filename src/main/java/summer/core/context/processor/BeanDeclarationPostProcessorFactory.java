package summer.core.context.processor;

import lombok.Getter;
import org.reflections.Reflections;

import summer.core.utils.ReflectionsHelper;

import java.util.List;
import java.util.stream.Collectors;

@Getter

public class BeanDeclarationPostProcessorFactory {
  private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors;

  public BeanDeclarationPostProcessorFactory(Reflections reflections) {

    beanFactoryPostProcessors = reflections.getSubTypesOf(BeanFactoryPostProcessor.class)
        .stream()
        .map(clazz -> clazz.cast(ReflectionsHelper.createObjectWithoutParameters(clazz)))
        .collect(Collectors.toList());
  }
}
