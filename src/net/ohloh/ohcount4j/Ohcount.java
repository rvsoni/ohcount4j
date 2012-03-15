package net.ohloh.ohcount4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ohloh.ohcount4j.AnnotationWriter;
import net.ohloh.ohcount4j.detect.Detector;
import net.ohloh.ohcount4j.io.SourceFile;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Ohcount {

	public static void main(String[] args) {
		final OhcountOptions opts = new OhcountOptions();
		final CmdLineParser optParser = new CmdLineParser(opts);
		try {
			optParser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println("Error parsing options - " + e.getMessage());
			System.exit(-1);
		}

		if (opts.help) {
			optParser.printUsage(System.out);
			System.exit(0);
		}

		// Count the current directory by default
		if (opts.targets.size() == 0) {
			opts.targets.add(".");
		}

		try {
			FileFinder ff = new FileFinder();
			for (String path : opts.targets) {
				ff.addPath(path);
			}
			ArrayList<File> files = ff.getFiles();

			if (opts.annotate) {
				annotate(files);
			} else if (opts.detect) {
				detect(files);
			} else {
				summarize(files);
			}
			System.exit(0);
		} catch (OhcountException e) {
			System.err.println("Error - " + e.getMessage());
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("Error - " + e.getMessage());
			System.exit(-1);
		}
	}

	static void annotate(List<File> files) throws IOException, OhcountException {
		AnnotationWriter handler = new AnnotationWriter();
		for (File file : files) {
			SourceFile sourceFile = new SourceFile(file);
			Language language = Detector.detect(sourceFile);
			if (language != null) {
				language.makeScanner().scan(sourceFile, handler);
			}
		}
	}

	static void detect(List<File> files) throws IOException, OhcountException {
		for (File file : files) {
			SourceFile sourceFile = new SourceFile(file);
			Language language = Detector.detect(sourceFile);
			if (language != null) {
				System.out.printf("%s\t%s\n", language.niceName(), file.getPath());
			}
		}
	}

	static void summarize(List<File> files) throws IOException, OhcountException {
		SummaryWriter summary = new SummaryWriter();
		for (File file : files) {
			SourceFile sourceFile = new SourceFile(file);
			Language language = Detector.detect(sourceFile);
			if (language != null) {
				summary.beginFile();
				language.makeScanner().scan(sourceFile, summary);
				summary.endFile();
			}
		}
		summary.printResults();
	}

	static class OhcountOptions {
		@Argument(metaVar = "[file]", usage = "target")
		List<String> targets = new ArrayList<String>();

		@Option(name = "-h", usage = "display this message")
		boolean help = false;

		@Option(name = "-s", usage = "show line count summary (default)")
		boolean summary = true;

		@Option(name = "-a", usage = "show annotated source code")
		boolean annotate = false;

		@Option(name = "-d", usage = "show detected file types only")
		boolean detect = false;
	}

}
