package spoon.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import spoon.Launcher;
import spoon.processing.ProcessInterruption;

/**
 * On lance le téléchargement du Github de ce projet qu'on analyse, puis qu'on
 * pull request. Problème dans le futur, lié au collisions des noms(deux projets
 * de même nom et bim, aie) Sauf si on fait comme d'habitude avec un dossier du
 * nom de l'utilisateur qui contient un dossier du nom de son application Puis
 * un dossier de la version.
 * 
 * @author guillaume
 *
 */
public class App {
	public static String input;
	public static String out;
	public static String name;
	public static String nameUser;
	public static String branch;

	public static void main(String[] args) {

		String url = "https://github.com/Snrasha/spoon-test.git";
		String[] split = url.split("/");
		if (split.length < 5)
			return;
		App.name = split[4].substring(0, split[4].length() - 4);
		App.nameUser = split[3];
		App.input = "./input/" + App.name;
		App.out = "./output/" + App.name;
		if (split.length < 7) {
			branch = "master";
		} else
			branch = split[6];

		run();
	}

	private static void run() {
		try {
			before();
		} catch (IOException e) {
		}
		try {
			after();
		} catch (IOException e) {
		}
	}

	private static void remove(String path) throws IOException {
		FileUtils.deleteDirectory(new File(path));
	}

	private static void before() throws IOException {
		CloneCommand clone = Git.cloneRepository();
		clone.setDirectory(new File(input));
		try {
			Git git = clone.setURI("https://github.com/Snrasha/spoon-test.git").call();

		} catch (InvalidRemoteException e1) {
		} catch (TransportException e1) {
		} catch (GitAPIException e1) {
		}

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);

		// Analyze only file on the input directory
		launcher.addInputResource(input + "/src/main/java");
		// Put all analyzed file (transformed or not) on the ouput directory
		remove(out);

		launcher.setSourceOutputDirectory(out);
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

	private static void after() throws IOException {

		FileUtils.copyDirectory(new File(out), new File(input + "/src/main/java"));
		GitHub github = GitHub.connect();
		GHRepository repo = github.getRepository(input);
		repo.createPullRequest("Analyse of Paprika", App.nameUser + ":" + App.branch, "Analyse", "/src/main/java");
		
		
		
		
		
		remove(out);
		/* remove(input); */
		

	}
}
