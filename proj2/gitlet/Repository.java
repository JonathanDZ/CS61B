package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import gitlet.StatusLog;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File StatusArea = join(GITLET_DIR, "StatusArea");
    public static final File Commits = join(GITLET_DIR, "Commits");
    public static final File Blobs = join(GITLET_DIR, "Blobs");


    /* TODO: fill in the rest of this class. */

    /* for init command */

    /** init command */
    public static void init() {
        if (GITLET_DIR.exists() == true) {
            Utils.error("A Gitlet version-control system already exists " +
                    "in the current directory.");
            return;
        }
        // make directories
        setupPersistence();
        // generate initial commit
        Commit initialCommit = new Commit("initial commit", null);
        initialCommit.saveCommit();
        // setup pointers
        StatusLog statusLog = new StatusLog();
        statusLog.setPointer("master", sha1(initialCommit));
        statusLog.setPointer("HEAD", sha1(initialCommit));
        statusLog.saveStatus();
    }

    private static void setupPersistence() {
        GITLET_DIR.mkdir();
        StatusArea.mkdir();
        Commits.mkdir();
        Blobs.mkdir();
    }

    /* for add command */

    public static void add(String fileName) {

    }


}
