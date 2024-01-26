package summer.core.context.exception;

public class NoUniqueBeanException extends RuntimeException {
  public NoUniqueBeanException(String message) {
    super(message);
  }
}
