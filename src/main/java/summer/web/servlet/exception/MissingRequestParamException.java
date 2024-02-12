package summer.web.servlet.exception;

public class MissingRequestParamException extends RuntimeException {
  public MissingRequestParamException(String message) {
    super(message);
  }
}
