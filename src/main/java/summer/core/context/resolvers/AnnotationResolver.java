package summer.core.context.resolvers;

public interface AnnotationResolver {
  boolean isSupported(Class<?> clazz);
  String resolve(Class<?> clazz);

  default String getDefaultName(Class<?> clazz) {
    String className = clazz.getSimpleName();

    char[] nameArray = className.toCharArray();
    nameArray[0] = Character.toLowerCase(nameArray[0]);
    return new String(nameArray);
  }
}
