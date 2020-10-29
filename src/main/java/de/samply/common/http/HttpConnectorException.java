package de.samply.common.http;

/**
 * The Class HttpConnectorException.
 */
public class HttpConnectorException extends Exception {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The throwable.
   */
  protected Throwable throwable;

  /**
   * Instantiates a new http connector exception.
   *
   * @param message the message
   */
  public HttpConnectorException(String message) {
    super(message);
  }

  /**
   * Instantiates a new http connector exception.
   *
   * @param message   the message
   * @param throwable the throwable
   */
  public HttpConnectorException(String message, Throwable throwable) {
    super(message);
    this.throwable = throwable;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Throwable#getCause()
   */
  @Override
  public Throwable getCause() {
    return throwable;
  }

}
