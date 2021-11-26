
package in.jaxer.core.utilities;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author Shakir Ansari
 */
public class ClipboardHandler
{

	private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	public static void copy(String string)
	{
		ClipboardHandler.clipboard.setContents(new StringSelection(string), null);
	}

	public static String paste() throws IOException, UnsupportedFlavorException
	{
		DataFlavor flavor = DataFlavor.stringFlavor;
		if (ClipboardHandler.clipboard.isDataFlavorAvailable(flavor))
		{
			return (String) ClipboardHandler.clipboard.getData(flavor);
		}
		return null;
	}
}
