package net.ohloh.ohcount4j.scan;

import java.io.IOException;

import net.ohloh.ohcount4j.Language;
import net.ohloh.ohcount4j.LanguageEntity;
import net.ohloh.ohcount4j.io.Blob;

public abstract class BaseScanner implements Scanner {

	// Ragel variables.
	protected char[] data = null;
	protected int cs;
	protected int top;
	protected int[] stack = new int[32];
	protected int p = 0;
	protected int eof = 0;
	protected int pe = eof;
	protected int mark = 0;
	protected int ts = 0;
	protected int te = 0;
	protected int act = 0;

	protected int mymark = 0;
	protected boolean inCode = false;

	protected EventHandler handler = null;

	protected Language language = null;

	// abstract method to be implemented by scanners
	public abstract void doScan();

	public abstract Language getLanguage();

	@Override
	public final void scan(Blob blob, EventHandler handler) throws IOException {
		handler.scanStart();
		data = blob.charContents();
		scan(data, handler);
		handler.scanEnd(data, p);
	}

	protected final void init() {
		pe = eof = data.length;
	}

	@Override
	public final void scan(char[] data, EventHandler handler) {
		this.data = data;
		this.handler = handler;
		this.language = getLanguage();
		pe = eof = data.length;
		handler.languageStart(new ScanEvent(getLanguage(), data, p));
		doScan();
		handler.languageEnd(new ScanEvent(getLanguage(), data, p));
	}

	protected void notifyNewline() {
		if (inCode) {
			notifyCodeEnd();
		}
		handler.newline(new EntityScanEvent(getLanguage(), LanguageEntity.NEWLINE, data, p));
	}

	protected void notifyBlanks() {
		if (inCode) {
			notifyCodeEnd();
		}
		handler.entityStart(new EntityScanEvent(getLanguage(), LanguageEntity.BLANK, data, ts));
		handler.entityEnd(new EntityScanEvent(getLanguage(), LanguageEntity.BLANK, data, te));
	}

	protected void notifyStartComment() {
		if (inCode) {
			notifyCodeEnd();
		}
		mymark = p;
		handler.entityStart(new EntityScanEvent(getLanguage(), LanguageEntity.COMMENT, data, mymark));
	}

	protected void notifyEndComment() {
		handler.entityEnd(new EntityScanEvent(getLanguage(), LanguageEntity.COMMENT, data, p));
	}

	protected void notifyStartString() {
		if (inCode) {
			notifyCodeEnd();
		}
		mymark = p;
		handler.entityStart(new EntityScanEvent(getLanguage(), LanguageEntity.CODE, data, mymark));
	}

	protected void notifyEndString() {
		handler.entityEnd(new EntityScanEvent(getLanguage(), LanguageEntity.CODE, data, p));
	}

	protected void notifyCodeCharacter() {
		if (inCode) {
			// Do nothing
		} else {
			inCode = true;
			mymark = p;
			handler.entityStart(new EntityScanEvent(getLanguage(), LanguageEntity.CODE, data, mymark));
		}
	}

	protected void notifyCodeEnd() {
		inCode = false;
		handler.entityEnd(new EntityScanEvent(getLanguage(), LanguageEntity.CODE, data, p));
	}
}
