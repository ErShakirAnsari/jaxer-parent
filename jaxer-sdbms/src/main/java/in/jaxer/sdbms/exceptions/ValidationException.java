package in.jaxer.sdbms.exceptions;

/**
 * @author Shakir
 */
public class ValidationException extends JaxerSDBMSException
{
	public ValidationException()
	{
		super();
	}

	public ValidationException(String message)
	{
		super(message);
	}

	public ValidationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ValidationException(Throwable cause)
	{
		super(cause);
	}

	public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
