package spoon.main;


@codesmells.annotations.Blob(currentLackOfCohesionMethods = 132, limitLackOfCohesionMethods = 40)
public class App {
    public static java.lang.String url;

    public static java.lang.String input;

    public static java.lang.String out;

    public static java.lang.String name;

    public static java.lang.String nameUser;

    public static java.lang.String branch;

    public static java.lang.String ssh;

    public static void main(java.lang.String[] args) {
        spoon.main.App.url = "https://github.com/Snrasha/spoon-test.git";
        java.lang.String[] split = spoon.main.App.url.split("/");
        if ((split.length) < 5)
            return ;
        
        if (split[4].contains(".git"))
            spoon.main.App.name = split[4].substring(0, ((split[4].length()) - 4));
        else
            spoon.main.App.name = split[4];
        
        spoon.main.App.nameUser = split[3];
        spoon.main.App.input = "./input/" + (spoon.main.App.name);
        spoon.main.App.out = "./output/" + (spoon.main.App.name);
        if ((split.length) < 7) {
            spoon.main.App.branch = "master";
        }else
            spoon.main.App.branch = split[6];
        
        spoon.main.App.ssh = "git@github.com:Snrasha/spoon-test.git";
        spoon.main.App.run();
    }

    private static void run() {
        try {
            spoon.main.App.before();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return ;
        }
        try {
            spoon.main.App.after();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return ;
        }
    }

    private static void remove(java.lang.String path) throws java.io.IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(new java.io.File(path));
    }

    private static void before() throws java.io.IOException {
        try {
            java.util.Set<java.lang.String> set = new java.util.HashSet<>();
            set.add("refs/heads/de");
            org.eclipse.jgit.api.CloneCommand clone = org.eclipse.jgit.api.Git.cloneRepository();
            clone.setDirectory(new java.io.File(spoon.main.App.input)).setURI(spoon.main.App.url).setBranchesToClone(set).setBranch("refs/heads/de").call();
        } catch (org.eclipse.jgit.api.errors.InvalidRemoteException e) {
            e.printStackTrace();
        } catch (org.eclipse.jgit.api.errors.TransportException e) {
            e.printStackTrace();
        } catch (org.eclipse.jgit.api.errors.GitAPIException e) {
            e.printStackTrace();
        }
        final spoon.Launcher launcher = new spoon.Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(((spoon.main.App.input) + "/src/main/java"));
        spoon.main.App.remove(spoon.main.App.out);
        launcher.setSourceOutputDirectory(spoon.main.App.out);
        final spoon.main.MethodProcessor methodprocessor = new spoon.main.MethodProcessor();
        launcher.addProcessor(methodprocessor);
        final spoon.main.ClassProcessor classprocessor = new spoon.main.ClassProcessor();
        launcher.addProcessor(classprocessor);
        final spoon.main.InterfaceProcessor interfaceProcessor = new spoon.main.InterfaceProcessor();
        launcher.addProcessor(interfaceProcessor);
        try {
            launcher.run();
        } catch (spoon.processing.ProcessInterruption e) {
            e.printStackTrace();
        }
    }

    private static void after() throws java.io.IOException {
        try {
            org.apache.commons.io.FileUtils.copyDirectory(new java.io.File(spoon.main.App.out), new java.io.File(((spoon.main.App.input) + "/src/main/java")));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        org.eclipse.jgit.api.Git git = null;
        try {
            git = org.eclipse.jgit.api.Git.open(new java.io.File(spoon.main.App.input));
        } catch (java.io.IOException e2) {
            e2.printStackTrace();
        }
        try {
            git.add().addFilepattern(".").call();
        } catch (org.eclipse.jgit.api.errors.NoFilepatternException e) {
            e.printStackTrace();
        } catch (org.eclipse.jgit.api.errors.GitAPIException e) {
            e.printStackTrace();
        }
        try {
            java.lang.System.out.println(git.getRepository().getFullBranch());
        } catch (java.io.IOException e1) {
            e1.printStackTrace();
        }
        try {
            git.commit().setMessage("test message").call();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        org.eclipse.jgit.api.PushCommand push = git.push();
        try {
            push.setRemote("origin").setPushAll().setCredentialsProvider(new org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider("Snrasha", "****")).call();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        git.close();
        try {
            spoon.main.App.remove(spoon.main.App.out);
            spoon.main.App.remove(spoon.main.App.input);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

