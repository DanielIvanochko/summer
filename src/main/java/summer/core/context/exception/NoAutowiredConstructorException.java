package summer.core.context.exception;

public class NoAutowiredConstructorException extends RuntimeException {
  public NoAutowiredConstructorException(String message) {
    super(message);
  }
}
