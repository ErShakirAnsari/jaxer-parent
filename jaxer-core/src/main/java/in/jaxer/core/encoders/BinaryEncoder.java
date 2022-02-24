
package in.jaxer.core.encoders;

import in.jaxer.core.utilities.JValidator;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Shakir Ansari
 */
@Log4j2
public class BinaryEncoder
{

	public static String convert(int x)
	{
		return Integer.toBinaryString(x);
	}

	public static int convert(String string)
	{
		return Integer.parseInt(string, 2);
	}

	public static String encode(String message)
	{
		JValidator.requireNotEmpty(message);

		String encoded = "";

		for (int i = 0; i < message.length(); i++)
		{
			encoded += (i % 2 == 0) ? "2" : "3";
			encoded += convert((int) message.charAt(i));
		}

		return encoded;
	}

	public static String decode(String message)
	{
		JValidator.requireNotEmpty(message);

		String pattern = "^[0-3]*$";
		if (!message.matches(pattern))
		{
			throw new IllegalArgumentException("Invalid number format");
		}

		//spliting with two delimiters [2 or 3]
		String[] charInt = message.split("[23]");
		log.debug(Arrays.toString(charInt));

		String decoded = "";

		//i starting from 1, bc charInt[]'s first value will be empty
		for (int i = 1; i < charInt.length; i++)
		{
			decoded += (char) convert(charInt[i]);
		}

		return JValidator.isEmpty(decoded) ? null : decoded;
	}
}
