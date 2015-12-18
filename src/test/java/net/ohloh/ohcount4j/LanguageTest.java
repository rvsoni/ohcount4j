package net.ohloh.ohcount4j;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import net.ohloh.ohcount4j.scan.AugeasScanner;
import net.ohloh.ohcount4j.scan.AutoconfScanner;
import net.ohloh.ohcount4j.scan.AutomakeScanner;
import net.ohloh.ohcount4j.scan.AwkScanner;
import net.ohloh.ohcount4j.scan.BfppScanner;
import net.ohloh.ohcount4j.scan.BrainfuckScanner;
import net.ohloh.ohcount4j.scan.CStyleScanner;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LanguageTest {

    @Test
    public void unameTest() {
        assertEquals("augeas", Language.AUGEAS.uname());// Augeas
        assertEquals("autoconf", Language.AUTOCONF.uname());
        assertEquals("awk", Language.AWK.uname());
        assertEquals("brainfuck", Language.BRAINFUCK.uname());
        assertEquals("bfpp", Language.BFPP.uname());
        assertEquals("c", Language.C.uname());
        assertEquals("golang", Language.GOLANG.uname());// GoLang
        assertEquals("ruby", Language.RUBY.uname());
    }

    @Test
    public void extensionsTest() {
        assertEquals(Language.AUGEAS.getExtensions().size(), 1);
        assertEquals(Language.AUGEAS.getExtensions().get(0), "aug");
        //
        assertTrue(Language.AUTOCONF.getExtensions().contains("ac"));
        assertTrue(Language.AUTOCONF.getExtensions().contains("autoconf"));
        assertTrue(Language.AUTOCONF.getExtensions().contains("m4"));
        //
        assertTrue(Language.AUTOMAKE.getExtensions().contains("am"));
        //
        assertTrue(Language.AWK.getExtensions().contains("awk"));

        // Brainfuck++
        assertEquals(Language.BFPP.getExtensions().size(), 1);
        assertEquals(Language.BFPP.getExtensions().get(0), "bfpp");

        // Brainfuck
        assertEquals(Language.BRAINFUCK.getExtensions().size(), 1);
        assertEquals(Language.BRAINFUCK.getExtensions().get(0), "bf");

        //
        assertEquals(Language.GOLANG.getExtensions().size(), 1);
        assertEquals(Language.GOLANG.getExtensions().get(0), "go");
        //
        assertTrue(Language.RUBY.getExtensions().contains("rb"));
        assertTrue(Language.RUBY.getExtensions().contains("ru"));
        assertFalse(Language.RUBY.getExtensions().contains("c"));
    }

    @Test
    public void filenamesTest() {
        assertTrue(Language.RUBY.getFilenames().contains("Rakefile"));
        assertFalse(Language.RUBY.getFilenames().contains("Makefile"));
    }

    @Test
    public void testCategory() {
        Assert.assertEquals(Language.AUGEAS.category(), LanguageCategory.LOGIC);
        Assert.assertEquals(Language.AUTOCONF.category(), LanguageCategory.BUILD);
        Assert.assertEquals(Language.AUTOMAKE.category(), LanguageCategory.BUILD);
        Assert.assertEquals(Language.AWK.category(), LanguageCategory.LOGIC);
        Assert.assertEquals(Language.BFPP.category(), LanguageCategory.LOGIC);
        Assert.assertEquals(Language.BRAINFUCK.category(), LanguageCategory.LOGIC);
        Assert.assertEquals(Language.GOLANG.category(), LanguageCategory.LOGIC);
    }

    @Test
    public void testScannerClass() {
        Assert.assertEquals(Language.AUGEAS.scannerClass(), AugeasScanner.class);
        Assert.assertEquals(Language.AUTOCONF.scannerClass(), AutoconfScanner.class);
        Assert.assertEquals(Language.AUTOMAKE.scannerClass(), AutomakeScanner.class);
        Assert.assertEquals(Language.AWK.scannerClass(), AwkScanner.class);
        Assert.assertEquals(Language.BFPP.scannerClass(), BfppScanner.class);
        Assert.assertEquals(Language.BRAINFUCK.scannerClass(), BrainfuckScanner.class);
        Assert.assertEquals(Language.GOLANG.scannerClass(), CStyleScanner.class);
    }

    @Test
    public void testNiceName() {
        Assert.assertEquals(Language.AUGEAS.niceName(), "Augeas");
        Assert.assertEquals(Language.AUTOCONF.niceName(), "Autoconf");
        Assert.assertEquals(Language.AUTOMAKE.niceName(), "Automake");
        Assert.assertEquals(Language.AWK.niceName(), "Awk");
        Assert.assertEquals(Language.BFPP.niceName(), "Brainfuck++");
        Assert.assertEquals(Language.BRAINFUCK.niceName(), "Brainfuck");
        Assert.assertEquals(Language.GOLANG.niceName(), "Go");
    }
}
