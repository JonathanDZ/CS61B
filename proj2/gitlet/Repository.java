package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;


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
        if (GITLET_DIR.exists()) {
            throw Utils.error("A Gitlet version-control system already exists " +
                    "in the current directory.");
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
        File fileToSave = join(CWD, fileName);
        if (!fileToSave.exists()) {
            throw error("File does not exist.");
        }
        // read the saved commit and status
        StatusLog statusLog = StatusLog.readStatus();
        Commit currentCommit = statusLog.readCurrentCommit();
        // check if the file is in stagedForRemoval set, if it is, remove it.
        statusLog.stagedForRemoval.remove(fileName);
        // calculate sha1 code of the file
        String sha1BlobName = sha1(fileToSave);
        // check if the file is not changed
        String lastBlob = currentCommit.getBlob(fileName);
        if (lastBlob.equals(sha1BlobName)) {
            // if the file is the same, unstage the file.
            statusLog.stagedForAddition.remove(fileName);
            return;
        }
        // check if the modification is already added
        if (statusLog.stagedForAddition.containsKey(fileName)) {
            if (statusLog.stagedForAddition.get(fileName).equals(sha1BlobName)) {
                // if the file isn't modified again, then left without change anything.
                return;
            } else {
                // if the file is modified again, delete the old blob and create a new one.
                Commit.deleteBlob(statusLog.stagedForAddition.get(fileName));
                //Commit.createBlob(sha1BlobName, fileName);
            }
        }
        statusLog.stagedForAddition.put(fileName, sha1BlobName);
        Commit.createBlob(sha1BlobName, fileName);

        // save all changes
        statusLog.saveStatus();
    }

    public static void commit(String commitMessage) {
        // read the saved commit and status
        StatusLog statusLog = StatusLog.readStatus();
        if (statusLog.stagedForAddition.isEmpty() && statusLog.stagedForRemoval.isEmpty()) {
            message("No changes added to the commit");
            return;
        }
        Commit parentCommit = statusLog.readCurrentCommit();

        Commit newCommit = new Commit(commitMessage, parentCommit);
        newCommit.putAll(statusLog.stagedForAddition);
        newCommit.removeAll(statusLog.stagedForRemoval);
        statusLog.resetStaged();
        statusLog.setPointer("HEAD", sha1(newCommit));

        // save all changes
        newCommit.saveCommit();
        statusLog.saveStatus();
    }

    public static void rm(String fileName) {
        // read the saved commit and status
        StatusLog statusLog = StatusLog.readStatus();
        Commit currentCommit = statusLog.readCurrentCommit();
        if (statusLog.stagedForAddition.containsKey(fileName)) {
            // delete the blob the file link to
            Commit.deleteBlob(statusLog.stagedForAddition.get(fileName));
            statusLog.stagedForAddition.remove(fileName);
        } else if (currentCommit.containsFile(fileName)) {
            statusLog.stagedForRemoval.add(fileName);
            File fileToDelete = join(CWD, fileName);
            if (fileToDelete.exists() && fileToDelete.isFile()) {
                try {
                    fileToDelete.delete();
                } catch (Exception excp) {
                    throw error("Can't delete the given file");
                }
            }
        } else {
            throw error("No reason to remove the file.");
        }
    }

    public static void log() {
        // read the saved commit and status
        StatusLog statusLog = StatusLog.readStatus();
        Commit commitPointer = statusLog.readCurrentCommit();

        while (commitPointer != null) {
            System.out.print(commitPointer);
            commitPointer = commitPointer.findParent();
        }
    }

}
