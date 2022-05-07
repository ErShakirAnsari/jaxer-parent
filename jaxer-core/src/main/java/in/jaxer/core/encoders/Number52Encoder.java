
package in.jaxer.core.encoders;

import in.jaxer.core.exceptions.ValidationException;
import in.jaxer.core.utilities.JValidator;

/**
 *
 * @author Shakir Ansari
 */
public class Number52Encoder extends Encoder
{

	private String numberScale;

	public Number52Encoder()
	{
		numberScale = initNumberScale();
		System.out.println("Number52Encoder.<init>() - numberScale: [" + numberScale + "]");
	}

	private String initNumberScale()
	{
		String _numberScale = "";
		/**
		 * ASKII
		 * A-Z = 65...90
		 * a-z = 97...122
		 * 0-9 = 48...57
		 */
		for (int i = 0; i < 26; i++)
		{
			_numberScale += "" + (char) ((int) 'A' + i);
		}
		for (int i = 0; i < 10; i++)
		{
			_numberScale += "" + (i);
		}
		for (int i = 0; i < 26; i++)
		{
			_numberScale += "" + (char) ((int) 'a' + i);
		}
		return _numberScale;
	}

	@Override
	public String convert(int x)
	{
		if (x == 0)
		{
			return String.valueOf(numberScale.charAt(0));
		}

		String num52 = "";
		int temp;

		while (x > 0)
		{
			temp = x % numberScale.length();
			num52 = numberScale.charAt(temp) + num52;
			x /= numberScale.length();
		}
		return num52;
	}

	@Override
	public int convert(String s)
	{
		int val = 0;

		for (int i = 0; i < s.length(); i++)
		{
			int d = numberScale.indexOf(s.charAt(i));
			if (d == -1)
			{
				throw new IllegalArgumentException("Invalid number format");
			}
			val = numberScale.length() * val + d;
		}
		return val;
	}

	@Override
	public String encode(String message)
	{
		JValidator.requireNotEmpty(message);

		int ch;
		int length = message.length();

		String encoded = "";

		for (int i = 0; i < length; i++)
		{
			ch = (int) message.charAt(i);
			int first = ch % 10;
			int second = ch / 10;
			encoded += first + convert(second);
		}

		return encoded;
	}

	@Override
	public String decode(String message)
	{
		JValidator.requireNotEmpty(message);

		String pattern = "^[0-9a-zA-Z]*$";
		if (!message.matches(pattern))
		{
			throw new ValidationException("Invalid enxryption format");
		}

		/**
		 * ASKII
		 * A-Z = 65...90
		 * a-z = 97...122
		 * 0-9 = 48...57
		 */
		String decoded = "";
		int length = message.length();
		for (int i = 0; i < length; i++)
		{
			char ch = message.charAt(i);

			if (ch >= '0' && ch <= '9')
			{
				int nextI = i;
				char nextChar = message.charAt(nextI++);
				while ((nextChar >= 'a' && nextChar <= 'z')
						|| (nextChar >= 'A' && nextChar <= 'Z'))
				{
					nextChar = message.charAt(nextI++);
				}

				String temp = "";
				while (nextI > i && nextI < length)
				{
					temp += message.charAt(nextI);
					nextI--;
				}
				decoded += (char) ((convert(temp) * 10) + (int) (ch - 48));
			}
		}

		return decoded;
	}
}
