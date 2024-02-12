package summer.web.servlet.exception;

public class MissingServletImplException extends RuntimeException {
  public MissingServletImplException(String message) {
    super(message);
  }
}
