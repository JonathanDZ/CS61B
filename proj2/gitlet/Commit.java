package gitlet;

// TODO: any imports you need here

import static gitlet.Utils.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Formatter;
import java.util.Map;

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
    private Map<String, String> filesMap;
    private String parentCommit;

    public static final File COMMITS_DIR = join(".gitlet", "Commits");
    public static final File BLOBS_DIR = join(".gitlet", "Blobs");

    /* TODO: fill in the rest of this class. */

    /**
     * Commit object constructor;
     * Copy the fileMap of parent commit,
     * if parent is null, then create an initial Commit
     * @param message
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

}
