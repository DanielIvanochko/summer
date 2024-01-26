package summer.core.context.factory;

import java.util.Map;

public interface BeanFactory {

    <T> T getBean(Class<T> type);

    <T> T getBean(Class<T> type, String name);

    <T> Map<String, T> getBeans(Class<T> type);

    <T> Map<String, T> getAllBeans();

    void close();
}

