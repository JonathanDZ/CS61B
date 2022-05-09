package gitlet;

// TODO: any imports you need here

import static gitlet.Utils.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Map;
import java.util.Set;

/** 
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date date;
    /** fileName -> blob */
    private Map<String, String> filesMap;
    private String parentCommit;

    public static final File COMMITS_DIR = join(".gitlet", "Commits");
    public static final File BLOBS_DIR = join(".gitlet", "Blobs");

    /* TODO: fill in the rest of this class. */

    /**
     * Commit object constructor;
     * Copy the fileMap of parent commit to its child commit,
     * if parent is null, then create an initial Commit
     * @param message commit message
     * @param parentCommit
     */
    public Commit(String message, Commit parentCommit) {
        this.message = message;
        if (parentCommit == null) {
            date.setTime(0);
            filesMap = null;
        }else {
            date = new Date();
            filesMap = parentCommit.filesMap;
            this.parentCommit = sha1(parentCommit);
        }
    }

    /**
     * Copies all of the mappings from the specified filesMap to this filesMap.
     * @param anotherMap
     */
    public void putAll(Map<String, String> anotherMap) {
        this.filesMap.putAll(anotherMap);
    }

    public void removeAll(Set<String> anotherSet) {
        for (String file : anotherSet) {
            this.filesMap.remove(file);
        }
    }

    /* Commit Serialization */

    /**
     * Commit object Serialization
     */
    public void saveCommit() {
        String sha1Code = sha1(this);
        File commitSavedFile = join(COMMITS_DIR, sha1Code);
        try {
            commitSavedFile.createNewFile();
            writeObject(commitSavedFile, this);
        } catch (IOException excp) {
            throw error("Can't save Commit object file");
        }
    }

    /**
     * Deserialize a commit object from a given file (named by hashcode).
     * @param fileName
     * @return
     */
    public static Commit readCommit(String fileName) {
        File commitSavedFile = join(COMMITS_DIR, fileName);
        return readObject(commitSavedFile, Commit.class);
    }

    /* Blob related operation */

    /**
     * Create a new blob, and save the content of the file in it.
     * @param blobName
     * @param fileName
     */
    public static void createBlob(String blobName, String fileName) {
        File blobSavedFile = join(BLOBS_DIR, blobName);
        File fileToSave = new File(fileName);
        try {
            blobSavedFile.createNewFile();
            String fileContent = readContentsAsString(fileToSave);
            writeContents(blobSavedFile, fileContent);
        } catch (IOException excp) {
            throw error("Can't save file's content to its blob");
        }
    }

    public static void deleteBlob(String blobName) {
        File blobSavedFile = join(BLOBS_DIR, blobName);
        restrictedDelete(blobSavedFile);
    }


    /**
     * Find which blob the file map to in this commit.
     * @param fileName
     * @return
     */
    public String getBlob(String fileName) {
        String blobName = this.filesMap.get(fileName);
        return blobName;
    }

}
