package spoon.main;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import spoon.Launcher;
import spoon.processing.ProcessInterruption;

public class App {
	public static void main(String[] args) {
		String input = "./input";
		String out = "./output";
		String name = "spoon-test";
		before(input,out,name);
		after(input,out);
	}

	
	public static void before(String input, String out, String name){
		File currentFile;
		CloneCommand clone = Git.cloneRepository();
		clone.setDirectory(new File(input + "/" + name));
		try {
			clone.setURI("https://github.com/Snrasha/spoon-test.git").call();
		} catch (InvalidRemoteException e1) {
		} catch (TransportException e1) {
		} catch (GitAPIException e1) {
		}

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);

		// Analyze only file on the input directory
		launcher.addInputResource(input + "/" + name);
		// Put all analyzed file (transformed or not) on the ouput directory

		// Clean the output directory
		File directory_out = new File(out);
		String[] entries = directory_out.list();
		for (String s : entries) {
			currentFile = new File(directory_out.getPath(), s);
			currentFile.delete();
		}
		launcher.setSourceOutputDirectory(out+"/"+name);
		// launcher.getEnvironment().setCommentEnabled(true);

		final MethodProcessor methodprocessor = new MethodProcessor();
		launcher.addProcessor(methodprocessor);
		final ClassProcessor classprocessor = new ClassProcessor();
		launcher.addProcessor(classprocessor);
		final InterfaceProcessor interfaceProcessor = new InterfaceProcessor();
		launcher.addProcessor(interfaceProcessor);
		try {
			launcher.run();
		} catch (ProcessInterruption e) {
			System.out.println("ok");
		}
		
	}
	public static void after(String input, String out){
		File currentFile;
		// delete all thing on the input
		File directory_out2 = new File(out);
		String[] entries2 = directory_out2.list();
		for (String s : entries2) {
			currentFile = new File(directory_out2.getPath(), s);
			currentFile.delete();
		}
		File directory_in = new File(input);
		String[] entries3 = directory_in.list();
		for (String s : entries3) {
			currentFile = new File(directory_in.getPath(), s);
			currentFile.delete();
		}
	}
}
