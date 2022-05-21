package gitlet;

// TODO: any imports you need here

import static gitlet.Utils.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

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
            date = new Date();
            date.setTime(0);
            filesMap = new TreeMap<>();
            this.parentCommit = null;
        }else {
            date = new Date();
            filesMap = parentCommit.filesMap;
            this.parentCommit = sha1(serialize(parentCommit));
        }
    }

    /**
     * Copies all of the mappings from the specified Map to this filesMap.
     * @param anotherMap
     */
    public void putAll(Map<String, String> anotherMap) {
        this.filesMap.putAll(anotherMap);
    }

    /**
     * removes all of the mappings from the specified Set to this filesMap.
     * @param anotherSet
     */
    public void removeAll(Set<String> anotherSet) {
        for (String file : anotherSet) {
            this.filesMap.remove(file);
        }
    }

    /**
     * Check if the commit tracks the given file
     * @param fileName
     * @return
     */
    public boolean containsFile(String fileName) {
        return this.filesMap.containsKey(fileName);
    }

    public Map<String, String> getFilesMap() {
        return this.filesMap;
    }

    /* Commit Serialization */

    /**
     * Commit object Serialization
     */
    public void saveCommit() {
        String sha1Code = sha1(serialize(this));
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
     * @param fileName given hash code name of a commit
     * @return the wanted commit object
     */
    public static Commit readCommit(String fileName) {
        if (fileName == null) {
            return null;
        }
        File commitSavedFile = join(COMMITS_DIR, fileName);
        return readObject(commitSavedFile, Commit.class);
    }

    /* Print the commits */

    @Override
    public String toString() {
        String message = "===" + "\n";
        message += "commit " + sha1(serialize(this)) + "\n";
        message += "Date: " + date.toString() + "\n";
        message += this.message + "\n";
        message += "\n";
        return message;
    }

    public Commit findParent() {
        Commit parentCommit = readCommit(this.parentCommit);
        return parentCommit;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getDate() {
        return this.date;
    }

    public String getParentCommit() {
        return this.parentCommit;
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
        // restrictedDelete(blobSavedFile);
        File fileToDelete = blobSavedFile;
        if (fileToDelete.exists() && fileToDelete.isFile()) {
            try {
                fileToDelete.delete();
            } catch (Exception excp) {
                throw error("Can't delete the given file");
            }
        }
    }


    /**
     * Find which blob the file map to in this commit.
     * @param fileName specific file in this commit
     * @return the blob hash code name
     */
    public String getBlob(String fileName) {
        String blobName = this.filesMap.get(fileName);
        return blobName;
    }

}
