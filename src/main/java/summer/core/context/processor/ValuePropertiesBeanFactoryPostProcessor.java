package summer.core.context.processor;

import summer.core.context.factory.DefaultBeanFactory;
import summer.core.utils.PropertiesLoader;

import java.util.Properties;

public class ValuePropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

  private static final String APPLICATION_PROPERTIES_FILE = "application.properties";

  @Override
  public void postProcessBeanFactory(DefaultBeanFactory defaultBeanFactory) {
    Properties properties = PropertiesLoader.loadProperties(APPLICATION_PROPERTIES_FILE);
    defaultBeanFactory.setProperties(properties);
  }
}
