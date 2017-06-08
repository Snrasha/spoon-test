package spoon.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.*;
import org.json.JSONObject;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

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

	public static String ssh;

	public static void main(String[] args) {

		App.url = "https://github.com/Snrasha/spoon-test.git";
		String[] split = url.split("/");
		if (split.length < 5)
			return;
		if (split[4].contains(".git"))
			App.name = split[4].substring(0, split[4].length() - 4);
		else
			App.name = split[4];
		App.nameUser = split[3];
		App.input = "./input/" + App.name;
		App.out = "./output/" + App.name;
		if (split.length < 7) {
			branch = "master";
		} else
			branch = split[6];
		App.branch="PaprikaAnalyze";
		App.ssh = "git@github.com:Snrasha/spoon-test.git";
		run();
	}

	private static void run() {
		try {
			before();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			after();
		} catch (IOException e) {
			e.printStackTrace();
			return;

		}

	}

	private static void remove(String path) throws IOException {
		FileUtils.deleteDirectory(new File(path));
	}

	private static void before() throws IOException {

		try {
			Set<String> set = new HashSet<>();
			set.add("refs/heads/de");
			CloneCommand clone = Git.cloneRepository();
			Git git=clone.setDirectory(new File(input)).setURI(App.url).setBranchesToClone(set).setBranch("refs/heads/de")
					.call();
			CheckoutCommand checkout=git.checkout();
			checkout.setName(App.branch).setOrphan(true);
			checkout.call();
			git.close();
			
			/*
			 * 
			 * SshSessionFactory sshSessionFactory = new
			 * JschConfigSessionFactory() {
			 * 
			 * @Override protected void configure(Host host, Session session) {
			 * session.setConfig("StrictHostKeyChecking", "no"); } };
			 * CloneCommand cloneCommand = Git.cloneRepository(); cloneCommand
			 * .setDirectory(new File(input)) .setURI(App.ssh)
			 * .setBranchesToClone(set).setBranch("refs/heads/de")
			 * .setTransportConfigCallback(new TransportConfigCallback() {
			 * 
			 * @Override public void configure(Transport transport) {
			 * SshTransport sshTransport = (SshTransport) transport;
			 * sshTransport.setSshSessionFactory(sshSessionFactory); } });
			 * cloneCommand.call();
			 */

		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();

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
			e.printStackTrace();
		}

	}

	private static void after() throws IOException {

		try {
			FileUtils.copyDirectory(new File(out), new File(input + "/src/main/java"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

		Git git = null;
		try {
			git = Git.open(new File(App.input));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			git.add().addFilepattern(".").call();
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
			git.commit()
			.setMessage("test message").call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PushCommand push = git.push();

		try {

			InputStream is = new FileInputStream("./info.json");
			String jsonTxt = IOUtils.toString(is);
			System.out.println(jsonTxt);
			JSONObject json = new JSONObject(jsonTxt);

			String token=json.getString("token2");
			
			/*push.setRemote("origin")
			.setPushAll()
			.setCredentialsProvider(new UsernamePasswordCredentialsProvider("token", token)).call();
*/
			
			GitpullRequest pull = new GitpullRequest(App.nameUser,App.name,App.branch);
			pull.getData();
		
		/*	
			
			PullRequestService service = new PullRequestService();
			service.getClient().setOAuth2Token(token);
			PullRequest request= new PullRequest();
			request.setTitle("New test");
			request.setBody("Please put in");
			request.setBase(new PullRequestMarker().setRef("de"));
			request.setHead(new PullRequestMarker().)
			service.createPullRequest(repository, request);
			*/
	
			/*
			 * SshSessionFactory sshSessionFactory = new
			 * JschConfigSessionFactory() {
			 * 
			 * @Override protected void configure(Host host, Session session) {
			 * session.setConfig("StrictHostKeyChecking", "no"); } }; push
			 * .setRemote("origin") .setTransportConfigCallback(new
			 * TransportConfigCallback() {
			 * 
			 * @Override public void configure(Transport transport) {
			 * SshTransport sshTransport = (SshTransport) transport;
			 * sshTransport.setSshSessionFactory(sshSessionFactory); }
			 * }).call();
			 * 
			 */

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
			remove(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
