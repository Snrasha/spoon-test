package spoon.main;

import java.io.File;
import codesmells.annotations.Blob;
import spoon.processing.ProcessInterruption;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.api.CloneCommand;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.util.HashSet;
import java.io.IOException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import java.util.Set;
import spoon.Launcher;
import org.eclipse.jgit.api.PushCommand;

@Blob(currentLackOfCohesionMethods = 132, limitLackOfCohesionMethods = 40)
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
        String[] split = App.url.split("/");
        if ((split.length) < 5)
            return ;
        
        if (split[4].contains(".git"))
            App.name = split[4].substring(0, ((split[4].length()) - 4));
        else
            App.name = split[4];
        
        App.nameUser = split[3];
        App.input = "./input/" + (App.name);
        App.out = "./output/" + (App.name);
        if ((split.length) < 7) {
            App.branch = "master";
        }else
            App.branch = split[6];
        
        App.ssh = "git@github.com:Snrasha/spoon-test.git";
        App.run();
    }

    private static void run() {
        try {
            App.before();
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
        try {
            App.after();
        } catch (IOException e) {
            e.printStackTrace();
            return ;
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
            clone.setDirectory(new File(App.input)).setURI(App.url).setBranchesToClone(set).setBranch("refs/heads/de").call();
        } catch (InvalidRemoteException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        final Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(((App.input) + "/src/main/java"));
        App.remove(App.out);
        launcher.setSourceOutputDirectory(App.out);
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
            FileUtils.copyDirectory(new File(App.out), new File(((App.input) + "/src/main/java")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Git git = null;
        try {
            git = Git.open(new File(App.input));
        } catch (IOException e2) {
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
            e1.printStackTrace();
        }
        try {
            git.commit().setMessage("test message").call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PushCommand push = git.push();
        try {
            push.setRemote("origin").setPushAll().setCredentialsProvider(new UsernamePasswordCredentialsProvider("Snrasha", "****")).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        git.close();
        try {
            App.remove(App.out);
            App.remove(App.input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

