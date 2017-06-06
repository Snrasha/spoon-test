package spoon.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
		remove(input+"/"+name);
	}
	private static void remove(String path){
		// Clean the output directory
		try {
			FileUtils.deleteDirectory(new File(path));
		} catch (IOException e1) {
		}
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
		launcher.addInputResource(input + "/" + name + "/src/main/java");
		// Put all analyzed file (transformed or not) on the ouput directory
		remove(out);
		
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
}
