package spoon.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.*;

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
	public static String url;
	public static String input;
	public static String out;
	public static String name;
	public static String nameUser;
	public static String branch;

	public static void main(String[] args) {

		App.url = "https://github.com/Snrasha/spoon-test.git";
		String[] split = url.split("/");
		if (split.length < 5)
			return;
		if(split[4].contains(".git"))
		App.name = split[4].substring(0, split[4].length() - 4);
		else App.name=split[4];
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void remove(String path) throws IOException {
		FileUtils.deleteDirectory(new File(path));
	}

	private static void before() throws IOException {
		CloneCommand clone = Git.cloneRepository();
		clone.setDirectory(new File(input));
		try {
			clone.setURI(App.url).call();

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

		try {
			FileUtils.copyDirectory(new File(out), new File(input + "/src/main/java"));
		} catch (IOException e) {
			e.printStackTrace();
		}

        
		Git git=null;
		try {
			git = Git.open(new File(App.input));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		try {
			git.add().addFilepattern(App.input+"/.").call();
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(git.getRepository().getFullBranch());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			git.commit().setMessage("test message").call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PushCommand push = git.push();
		push.setRemote(App.url);
		/*
		push.getPushOptions().add("-u");
		push.getPushOptions().add("origin");
		push.getPushOptions().add("de");*/
		try {
			push.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		git.close();

		/*
		 * RepositoryService service = new RepositoryService(); Repository repo
		 * = service.getRepository(App.nameUser, App.name);
		 * System.out.println(repo.getName() + " Watchers: " +
		 * repo.getWatchers());
		 * 
		 * GistFile file = new GistFile();
		 * file.setContent("System.out.println(\"Hello World\");"); Gist gist =
		 * new Gist(); gist.setDescription("Prints a string to standard out");
		 * gist.setFiles(Collections.singletonMap("Hello.java", file));
		 * GistService service2 = new GistService();
		 * service2.getClient().setOAuth2Token(App.nameUser); gist =
		 * service2.createGist(gist);
		 * 
		 * System.out.println(gist.getGitPullUrl() +
		 * " and "+gist.getGitPushUrl());
		 */
		try {
			remove(out);
			//remove(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
