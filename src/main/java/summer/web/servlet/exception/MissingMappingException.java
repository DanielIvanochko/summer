package summer.web.servlet.exception;

public class MissingMappingException extends RuntimeException {
  public MissingMappingException(String message) {
    super(message);
  }
}
