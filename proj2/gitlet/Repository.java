package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        statusLog.currentBranch = "master";
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
        statusLog.setPointer("HEAD", sha1((Object) serialize(newCommit)));
        String currentBranch = statusLog.currentBranch;
        statusLog.setPointer(currentBranch, sha1((Object) serialize(newCommit)));

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
            throw error("Found no commit with that message.");
        }
    }

    public static void status() {
        StatusLog statusLog = StatusLog.readStatus();

        System.out.println("=== Branches ===");
        for (String branch: statusLog.pointersMap.keySet()) {
            if (branch.equals("HEAD")) {
                continue;
            }
            if (statusLog.currentBranch.equals(branch)) {
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

        for (String fileName : replaceFilesSet) {
            String blobName = checkoutFilesMap.get(fileName);
            File fileToCreate = join(CWD, fileName);
            File contentSavedBlob = join(Blobs, blobName);
            String content = readContentsAsString(contentSavedBlob);
            writeContents(fileToCreate, content);
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

        statusLog.setPointer("HEAD", branchCommit);
        statusLog.currentBranch = branchName;
        statusLog.resetStaged();
        // save changes
        statusLog.saveStatus();
    }

    public static void branch(String branchName) {
        StatusLog statusLog = StatusLog.readStatus();

        if (statusLog.pointersMap.containsKey(branchName)) {
            throw error("A branch with that name already exists.");
        }
        String currentCommitName = statusLog.pointersMap.get("HEAD");
        statusLog.pointersMap.put(branchName, currentCommitName);

        // save changes
        statusLog.saveStatus();
    }

    public static void rmBranch(String branchName) {
        StatusLog statusLog = StatusLog.readStatus();

        if (!statusLog.pointersMap.containsKey(branchName)) {
            throw error("A branch with that name does not exist.");
        }
        if (statusLog.currentBranch.equals(branchName)) {
            throw error("Cannot remove the current branch.");
        }
        statusLog.pointersMap.remove(branchName);

        // save changes
        statusLog.saveStatus();
    }

    public static void reset(String briefCommitID) {
        StatusLog statusLog = StatusLog.readStatus();

        String commitID = findMatchedCommitID(briefCommitID);
        String HEADCommit = statusLog.pointersMap.get("HEAD");
        Commit checkoutCommit = Commit.readCommit(commitID);
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

        for (String fileName : replaceFilesSet) {
            String blobName = checkoutFilesMap.get(fileName);
            File fileToCreate = join(CWD, fileName);
            File contentSavedBlob = join(Blobs, blobName);
            String content = readContentsAsString(contentSavedBlob);
            writeContents(fileToCreate, content);
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

        statusLog.setPointer("HEAD", commitID);
        String currentBranch = statusLog.currentBranch;
        statusLog.setPointer(currentBranch, commitID);
        statusLog.resetStaged();

        // save changes
        statusLog.saveStatus();
    }

    private static Commit findLCA(Commit A, Commit B) {
        Deque<String> AAncestors = new LinkedList<>();
        Deque<String> BAncestors = new LinkedList<>();

        while (A.getParentCommit() != null) {
            AAncestors.push(A.getParentCommit());
            A = Commit.readCommit(A.getParentCommit());
        }
        while (B.getParentCommit() != null) {
            BAncestors.push(B.getParentCommit());
            B = Commit.readCommit(B.getParentCommit());
        }

        String LCAName = null;
        while (!AAncestors.isEmpty() && !BAncestors.isEmpty()) {
            String AAncestor = AAncestors.pop();
            String BAncestor = BAncestors.pop();
            if (AAncestor.equals(BAncestor)) {
                LCAName = AAncestor;
            } else {
                break;
            }
        }
        return Commit.readCommit(LCAName);
    }

    public static void merge(String branchName) {
        // read the saved commit and status
        // get three needed commit nodes(head, branch, LCA)
        StatusLog statusLog = StatusLog.readStatus();
        if (statusLog.currentBranch.equals(branchName)) {
            throw error("Cannot merge a branch with itself.");
        }
        if (!statusLog.stagedForAddition.isEmpty() || !statusLog.stagedForRemoval.isEmpty()) {
            throw error("You have uncommitted changes.");
        }
        Commit currentCommit = statusLog.readCurrentCommit();
        String branchCommitName = statusLog.pointersMap.get(branchName);
        if (branchCommitName == null) {
            throw error("A branch with that name does not exist.");
        }
        Commit branchCommit = Commit.readCommit(branchCommitName);
        Commit LCACommit = findLCA(currentCommit, branchCommit);

        // handling the edge cases
        if (LCACommit.equals(branchCommit)) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }
        if (LCACommit.equals(currentCommit)) {
            checkoutToBranch(branchName);
            message("Current branch fast-forwarded.");
            return;
        }

        // get all needed fileMap
        Map<String, String> currentFileMap = currentCommit.getFilesMap();
        Map<String, String> branchFileMap = branchCommit.getFilesMap();
        Map<String, String> LCAFileMap = LCACommit.getFilesMap();
        Set<String> allFileSet = new TreeSet<>();
        allFileSet.addAll(currentFileMap.keySet());
        allFileSet.addAll(branchFileMap.keySet());
        allFileSet.addAll(LCAFileMap.keySet());

        // conflict flag
        boolean conflictFlag = false;

        // check the edge case when we want to create a file, but it already exists.
        for (String fileName: allFileSet) {
            if (!currentFileMap.containsKey(fileName) && branchFileMap.containsKey(fileName)) {
                if (!LCAFileMap.containsKey(fileName) || !LCAFileMap.get(fileName).equals(branchFileMap.get(fileName))) {
                    File fileToCreate = join(CWD, fileName);
                    if (fileToCreate.exists()) {
                        throw error("There is an untracked file in the way; " +
                                "delete it, or add and commit it first.");
                    }
                }
            }
        }

        // iterate through all file to find the correct modification
        for (String fileName: allFileSet) {
            String LCABlobName = LCAFileMap.get(fileName);
            String currentBlobName = currentFileMap.get(fileName);
            String branchBlobName = branchFileMap.get(fileName);
            // X X A -> A: create a file and stage it for addition
            if (LCABlobName == null && currentBlobName == null && branchBlobName != null) {
//                File fileToCreate = join(CWD, fileName);
//                File contentSavedBlob = join(Blobs, branchBlobName);
//                try {
//                    fileToCreate.createNewFile();
//                    String content = readContentsAsString(contentSavedBlob);
//                    writeContents(fileToCreate, content);
//                } catch (IOException excp) {
//                    throw error("Can't create file: " + fileName);
//                }
                checkout(branchCommit, fileName);
                // stage it for addition
                add(fileName);
                continue;
            }

            // X A X -> A: nothing changed

            // X A !A -> conflict
            // X A A -> A: nothing changed
            if (LCABlobName == null && currentBlobName != null && branchBlobName != null) {
                if (!currentBlobName.equals(branchName)) {
                    File fileToReplace = join(CWD, fileName);
                    File currentContentSavedBlob = join(Blobs, currentBlobName);
                    File branchContentSavedBlob = join(Blobs, branchBlobName);

                    String currentContent = readContentsAsString(currentContentSavedBlob);
                    String branchContent = readContentsAsString(branchContentSavedBlob);
                    String content = "<<<<<<< HEAD\n" + currentContent + "=======\n" + branchContent + ">>>>>>>";
                    writeContents(fileToReplace, content);

                    add(fileName);
                    conflictFlag = true;
                }
                continue;
            }

            // A A & -> &: turn file to branch's file, stage it
            assert LCABlobName != null;
            if (LCABlobName.equals(currentBlobName)) {
                if (branchBlobName == null) {
                    rm(fileName);
                } else if (!currentBlobName.equals(branchBlobName)) {
                    checkout(branchCommit, fileName);
                    add(fileName);
                }
                continue;
            }

            // A & A -> &: Nothing changed

            // A !A !A -> conflict or nothing changed
            if (!LCABlobName.equals(currentBlobName) && !LCABlobName.equals(branchBlobName)) {
                if (currentBlobName == null && branchBlobName != null) {
                    File fileToReplace = join(CWD, fileName);
                    File branchContentSavedBlob = join(Blobs, branchBlobName);

                    String branchContent = readContentsAsString(branchContentSavedBlob);
                    String content = "<<<<<<< HEAD\n" + "=======\n" + branchContent + ">>>>>>>";
                    writeContents(fileToReplace, content);

                    add(fileName);
                    conflictFlag = true;
                    continue;
                }
                if (currentBlobName != null && branchBlobName == null) {
                    File fileToReplace = join(CWD, fileName);
                    File currentContentSavedBlob = join(Blobs, currentBlobName);

                    String currentContent = readContentsAsString(currentContentSavedBlob);
                    String content = "<<<<<<< HEAD\n" + currentContent + "=======\n" + ">>>>>>>";
                    writeContents(fileToReplace, content);

                    add(fileName);
                    conflictFlag = true;
                    continue;
                }
                if (currentBlobName != null && !currentBlobName.equals(branchBlobName)) {
                    File fileToReplace = join(CWD, fileName);
                    File currentContentSavedBlob = join(Blobs, currentBlobName);
                    File branchContentSavedBlob = join(Blobs, branchBlobName);

                    String currentContent = readContentsAsString(currentContentSavedBlob);
                    String branchContent = readContentsAsString(branchContentSavedBlob);
                    String content = "<<<<<<< HEAD\n" + currentContent + "=======\n" + branchContent + ">>>>>>>";
                    writeContents(fileToReplace, content);

                    add(fileName);
                    conflictFlag = true;
                }
            }
        }
        // create new merge commit
        String message = "Merged " + branchName + " into " + statusLog.currentBranch + ".";
        MergedCommit newCommit = new MergedCommit(message, currentCommit, branchCommit);

        newCommit.putAll(statusLog.stagedForAddition);
        newCommit.removeAll(statusLog.stagedForRemoval);
        statusLog.resetStaged();
        statusLog.setPointer("HEAD", sha1((Object) serialize(newCommit)));
        String currentBranch = statusLog.currentBranch;
        statusLog.setPointer(currentBranch, sha1((Object) serialize(newCommit)));

        if (conflictFlag) {
            message("Encountered a merge conflict.");
        }

        // save all changes
        newCommit.saveCommit();
        statusLog.saveStatus();
    }

}
