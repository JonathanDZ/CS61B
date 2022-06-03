package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
        statusLog.setPointer("master", sha1(serialize(initialCommit)));
        statusLog.setPointer("HEAD", sha1(serialize(initialCommit)));
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
        String sha1BlobName = sha1(readContents(fileToSave));
        // check if the file is not changed
        String lastBlob = currentCommit.getBlob(fileName);
        if (sha1BlobName.equals(lastBlob)) {
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
            message("No changes added to the commit.");
            return;
        }
        Commit parentCommit = statusLog.readCurrentCommit();

        Commit newCommit = new Commit(commitMessage, parentCommit);
        newCommit.putAll(statusLog.stagedForAddition);
        newCommit.removeAll(statusLog.stagedForRemoval);
        statusLog.resetStaged();
        statusLog.setPointer("HEAD", sha1(serialize(newCommit)));

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

        // Save all change
        statusLog.saveStatus();
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

    public static void globalLog() {
        List<String> commitsList = plainFilenamesIn(Commits);
        for (String commitFilename: commitsList) {
            Commit commit = Commit.readCommit(commitFilename);
            System.out.print(commit);
        }
    }

    public static void find(String message) {
        List<String> commitsList = plainFilenamesIn(Commits);
        boolean commitFound = false;
        for (String commitFilename: commitsList) {
            Commit commit = Commit.readCommit(commitFilename);
            if (commit.getMessage().equals(message)) {
                System.out.println(sha1(serialize(commit)));
                commitFound = true;
            }
        }
        if (!commitFound) {
            error("Found no commit with that message.");
        }
    }

    public static void status() {
        StatusLog statusLog = StatusLog.readStatus();

        String headCommit = statusLog.pointersMap.get("HEAD");
        System.out.println("=== Branches ===");
        for (String branch: statusLog.pointersMap.keySet()) {
            if (branch.equals("HEAD")) {
                continue;
            }
            if (statusLog.pointersMap.get(branch).equals(headCommit)) {
                branch = "*" + branch;
            }
            System.out.println(branch);
        }
        System.out.println("");

        System.out.println("=== Staged Files ===");
        for (String filename: statusLog.stagedForAddition.keySet()) {
            System.out.println(filename);
        }
        System.out.println("");

        System.out.println("=== Removed Files ===");
        for (String filename: statusLog.stagedForRemoval) {
            System.out.println(filename);
        }
        System.out.println("");

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println("");

        System.out.println("=== Untracked Files ===");
        System.out.println("");
    }

    private static void checkout(Commit checkoutCommit, String fileName) {
        if (!checkoutCommit.containsFile(fileName)) {
            throw error("File does not exist in that commit.");
        }
        String blobName = checkoutCommit.getBlob(fileName);
        // the file in the current working directory
        File fileToSave = join(CWD, fileName);
        // the file in the blob
        File fileBlob = join(Blobs, blobName);
        if (!fileToSave.exists()) {
            try {
                fileToSave.createNewFile();
            } catch (IOException excp) {
                throw error("Can't create the file.");
            }
        }
        String fileContent = readContentsAsString(fileBlob);
        writeContents(fileToSave, fileContent);
    }

    private static String findMatchedCommitID(String briefCommitID) {
        List<String> commitList = plainFilenamesIn(Commits);
        String matchedCommitName = null;
        if (commitList == null) {
            throw error("No commit with that id exists.");
        }
        for (String commitName : commitList) {
            if (commitName.startsWith(briefCommitID)) {
                matchedCommitName = commitName;
                break;
            }
        }
        if (matchedCommitName == null) {
            throw error("No commit with that id exists.");
        }
        return matchedCommitName;
    }

    public static void checkoutToCommitID(String briefCommitID, String fileName) {
        String commitID = findMatchedCommitID(briefCommitID);
        Commit checkoutCommit = Commit.readCommit(commitID);
        checkout(checkoutCommit, fileName);
    }

    public static void checkoutToHead(String fileName) {
        StatusLog statusLog = StatusLog.readStatus();
        Commit checkoutCommit = Commit.readCommit(statusLog.pointersMap.get("HEAD"));
        checkout(checkoutCommit, fileName);
    }

    public static void checkoutToBranch(String branchName) {
        StatusLog statusLog = StatusLog.readStatus();
        String branchCommit = statusLog.pointersMap.get(branchName);
        if (branchCommit == null) {
            throw error("No such branch exists.");
        }
        String HEADCommit = statusLog.pointersMap.get("HEAD");
        if (branchCommit.equals(HEADCommit)) {
            throw error("No need to checkout the current branch");
        }
        Commit checkoutCommit = Commit.readCommit(branchCommit);
        Commit currentCommit = Commit.readCommit(HEADCommit);
        Map<String, String> checkoutFilesMap = checkoutCommit.getFilesMap();
        Map<String, String> currentFilesMap = currentCommit.getFilesMap();
        Set<String> replaceFilesSet = new TreeSet<>();
        for (String fileName : checkoutFilesMap.keySet()) {
            if (currentFilesMap.containsKey(fileName)) {
                replaceFilesSet.add(fileName);
            } else {
                File fileToCreate = join(CWD, fileName);
                if (fileToCreate.exists()) {
                    throw error("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                }
            }
        }
        Set<String> deleteFilesSet = currentFilesMap.keySet();
        deleteFilesSet.removeAll(replaceFilesSet);
        for (String fileName : deleteFilesSet) {
            File fileToDelete = join(CWD, fileName);
            if (!fileToDelete.delete()) {
                throw error("Can't delete file: " + fileName);
            }
        }
        Set<String> createFilesSet = checkoutFilesMap.keySet();
        createFilesSet.removeAll(replaceFilesSet);
        for (String fileName : createFilesSet) {
            String blobName = checkoutFilesMap.get(fileName);
            File fileToCreate = join(CWD, fileName);
            File contentSavedBlob = join(Blobs, blobName);
            try {
                fileToCreate.createNewFile();
                String content = readContentsAsString(contentSavedBlob);
                writeContents(fileToCreate, content);
            } catch (IOException excp) {
                throw error("Can't create file: " + fileName);
            }
        }
        for (String fileName : replaceFilesSet) {
            String blobName = checkoutFilesMap.get(fileName);
            File fileToCreate = join(CWD, fileName);
            File contentSavedBlob = join(Blobs, blobName);
            String content = readContentsAsString(contentSavedBlob);
            writeContents(fileToCreate, content);
        }
        statusLog.setPointer("HEAD", branchCommit);
        statusLog.resetStaged();
        // save changes
        statusLog.saveStatus();
    }

}
