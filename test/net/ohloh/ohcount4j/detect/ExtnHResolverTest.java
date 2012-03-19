package net.ohloh.ohcount4j.detect;

import net.ohloh.ohcount4j.Language;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import net.ohloh.ohcount4j.io.Source;
import net.ohloh.ohcount4j.io.SourceBuffer;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ExtnHResolverTest {

	private ExtnHResolver r;

	@BeforeTest()
	public void setup() {
		this.r = new ExtnHResolver();
	}

	@Test
	public void canResolvetest() {
		assertFalse(r.canResolve(Language.RUBY));
		assertTrue(r.canResolve(Language.C));
		assertTrue(r.canResolve(Language.CPP));
		assertTrue(r.canResolve(Language.OBJECTIVE_C));
	}

	@Test
	// With no other clues, the resolver should pick C by default
	public void returnsCByDefaultTest() throws IOException {
		assertEquals(Language.C, r.resolve(new SourceBuffer("main.h", "")));
	}

	@Test
	public void findIncludesTest() throws IOException {
		Source s;

		s = new SourceBuffer("main.h", "");
		assertEquals(0, r.findIncludes(s).size());

		s = new SourceBuffer("main.h", "#include <stdio.h>");
		assertEquals(1, r.findIncludes(s).size());
		assertTrue(r.findIncludes(s).contains("stdio.h"));

		s = new SourceBuffer("main.h", "#include \"stdio.h\"");
		assertEquals(1, r.findIncludes(s).size());
		assertTrue(r.findIncludes(s).contains("stdio.h"));

		s = new SourceBuffer("main.h",
				"/* Longer Example */\n" +
				"#include \"stdio.h\"\n" +
				"\n" +
				"#include <string.h>\n" +
				"#include <cassert>\n" +
				"//#include <foo.h>\n" +
				"\n" +
				"int main() {" +
				"  char *foo = \"#include <bar.h>\";\n" +
				")\n"
			);
		assertEquals(3, r.findIncludes(s).size());
		assertFalse(r.findIncludes(s).contains("stdio"));
		assertFalse(r.findIncludes(s).contains("string"));
		assertFalse(r.findIncludes(s).contains("foo"));
		assertFalse(r.findIncludes(s).contains("foo.h"));
		assertFalse(r.findIncludes(s).contains("bar"));
		assertFalse(r.findIncludes(s).contains("bar.h"));

		assertTrue(r.findIncludes(s).contains("stdio.h"));
		assertTrue(r.findIncludes(s).contains("string.h"));
		assertTrue(r.findIncludes(s).contains("cassert"));
	}

	@Test
	public void detectByIncludesTest() throws IOException {
		Source s;

		s = new SourceBuffer("main.h", "#include <foo.h>");
		assertEquals(Language.C, r.resolve(s));

		s = new SourceBuffer("main.h", "#include <string.h>");
		assertEquals(Language.C, r.resolve(s));

		s = new SourceBuffer("main.h", "#include <string>");
		assertEquals(Language.CPP, r.resolve(s));

		s = new SourceBuffer("main.h", "#include <string.h>\n#include<string>\n");
		assertEquals(Language.CPP, r.resolve(s));

		s = new SourceBuffer("main.h", "#include <tr1/memory>");
		assertEquals(Language.CPP, r.resolve(s));
	}

	@Test
	public void detectByKeywordsTest() throws IOException {
		Source s;

		s = new SourceBuffer("main.h", "namespace foo\n");
		assertEquals(Language.CPP, r.resolve(s));

		s = new SourceBuffer("main.h",
				"/* Multiline example */\n" +
				"namespace foo {\n" +
				"    template <typename> struct Foo;\n" +
				"}\n"
		);
		assertEquals(Language.CPP, r.resolve(s));
	}
}