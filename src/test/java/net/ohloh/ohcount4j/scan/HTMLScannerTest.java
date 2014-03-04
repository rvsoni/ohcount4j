package net.ohloh.ohcount4j.scan;

import org.testng.annotations.Test;

import static net.ohloh.ohcount4j.Entity.*;
import net.ohloh.ohcount4j.Language;

public class HTMLScannerTest extends BaseScannerTest {

	@Test
	public void basic() {
		assertLine(Language.HTML, new Line(Language.HTML, BLANK),   "\n");
		assertLine(Language.HTML, new Line(Language.HTML, BLANK),   "     \n");
		assertLine(Language.HTML, new Line(Language.HTML, BLANK),   "\t\n");
		assertLine(Language.HTML, new Line(Language.HTML, CODE),    "<html>\n");
		assertLine(Language.HTML, new Line(Language.HTML, COMMENT), "<!-- comment -->\n");
		assertLine(Language.HTML, new Line(Language.HTML, CODE),    "<html><!-- with comment -->\n");
	}

	@Test
	public void eofHandling() {
		// Note lack of trailing \n in all cases below
		assertLine(Language.HTML, new Line(Language.HTML, BLANK),   "     ");
		assertLine(Language.HTML, new Line(Language.HTML, BLANK),   "\t");
		assertLine(Language.HTML, new Line(Language.HTML, CODE),    "<html>");
		assertLine(Language.HTML, new Line(Language.HTML, COMMENT), "<!-- comment -->");
		assertLine(Language.HTML, new Line(Language.HTML, CODE),    "<html><!-- with comment -->");
	}

	@Test
	public void helloWorld() {
		String code
			= "<!doctype HTML>\n"
			+ "<html lang='en'>\n"
			+ "<!-- A comment -->\n"
			+ "<body>\n"
			+ "\n"
			+ "<h1>Hello, world!</h1>\n"
			+ "\n"
			+ "</body>\n"
			+ "<html>";

		Line[] expected = {
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, COMMENT),
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, BLANK),
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, BLANK),
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, CODE)
		};
		assertLines(Language.HTML, expected, code);
	}

	@Test
	public void embeddedCSSOnSeparateLine() {
		String code
			= "<!doctype HTML>\n"
			+ "<style>\n"
			+ "  body:after { content:\"Hello, world!\"; }\n"
			+ "</style>\n"
			+ "<html>";

		Line[] expected = {
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, CODE),
			new Line(Language.CSS, CODE),
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, CODE)
		};
		assertLines(Language.HTML, expected, code);
	}

	@Test
	public void embeddedCSSOnSameLine() {
		String code
			= "<!doctype HTML>\n"
			+ "<style> body:after { content:\"Hello, world!\"; } </style>\n"
			+ "<html>";

		Line[] expected = {
			new Line(Language.HTML, CODE),
			new Line(Language.CSS, CODE),
			new Line(Language.HTML, CODE)
		};
		assertLines(Language.HTML, expected, code);
	}

	@Test
	public void emptyCSSOnSameLine() {
		String code
			= "<!doctype HTML>\n"
			+ "<style></style>\n"
			+ "<html>";

		Line[] expected = {
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, CODE)
		};
		assertLines(Language.HTML, expected, code);
	}

	@Test
	public void commentCSSOnSameLine() {
		String code
			= "<!doctype HTML>\n"
			+ "<style>/* No code just comment */</style>\n"
			+ "<html>";

		Line[] expected = {
			new Line(Language.HTML, CODE),
			new Line(Language.CSS, COMMENT),
			new Line(Language.HTML, CODE)
		};
		assertLines(Language.HTML, expected, code);
	}

	@Test
	public void embeddedJavaScriptOnSeparateLine() {
		String code
			= "<!doctype HTML>\n"
			+ "<script type=\"script/javascript\">\n"
			+ "  document.write(\"Hello, world!\\n\");\n"
			+ "</script>\n"
			+ "<html>";

		Line[] expected = {
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, CODE),
			new Line(Language.JAVASCRIPT, CODE),
			new Line(Language.HTML, CODE),
			new Line(Language.HTML, CODE)
		};
		assertLines(Language.HTML, expected, code);
	}

	@Test
	public void embeddedJavaScriptOnSameLine() {
		String code
			= "<!doctype HTML>\n"
			+ "<script type=\"script/javascript\">document.write(\"Hello, world!\\n\");</script>\n"
			+ "<html>";

		Line[] expected = {
			new Line(Language.HTML, CODE),
			new Line(Language.JAVASCRIPT, CODE),
			new Line(Language.HTML, CODE)
		};
		assertLines(Language.HTML, expected, code);
	}
}