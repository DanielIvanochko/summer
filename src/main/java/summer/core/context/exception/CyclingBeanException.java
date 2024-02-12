package summer.core.context.exception;

public class CyclingBeanException extends RuntimeException {
  public CyclingBeanException(String message) {
    super(message);
  }
}
