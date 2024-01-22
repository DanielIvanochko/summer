package summer.core.context;

import org.reflections.Reflections;

public class BeanCreator {
  private final AnnotationBeanRegistry registry;
  private final Reflections reflections;

  public BeanCreator(AnnotationBeanRegistry registry, Reflections reflections) {
    this.registry = registry;
    this.reflections = reflections;
  }
  //and constructor based creation class

}
