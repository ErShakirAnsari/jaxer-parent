
package in.jaxer.api.exceptions;

import in.jaxer.core.exceptions.JaxerCoreException;

/**
 *
 * @author Shakir
 * Date 22 Dec, 2021 - 12:58:40 AM
 */
public class JaxerApiException extends JaxerCoreException
{

	public JaxerApiException()
	{
	}

	public JaxerApiException(String message)
	{
		super(message);
	}

	public JaxerApiException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public JaxerApiException(Throwable cause)
	{
		super(cause);
	}
}
