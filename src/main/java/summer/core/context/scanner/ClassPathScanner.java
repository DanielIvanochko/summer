package summer.core.context.scanner;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface ClassPathScanner {
  Set<Class<?>> scan();

  Class<? extends Annotation> getSupportedAnnotation();
}
