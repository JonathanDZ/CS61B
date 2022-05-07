package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.*;

import static gitlet.Utils.*;
import static gitlet.Utils.error;

/**
 * This class track some important status of gitlet, including:
 *   - pointers (master, HEAD, and other branches)
 *   - StagedForAddition (modified files which have been added)
 *   - StagedForRemoval (deleted files which have been added)
 */
public class StatusLog implements Serializable {
    /** pointer -> Commit */
    public Map<String, String> pointersMap;
    /** fileName -> blob */
    public Map<String, String> stagedForAddition;
    /** a set of will be deleted files */
    public Set<String> stagedForRemoval;

    public static final File STATUS_AREA_DIR = join(".gitlet", "StatusArea");

    public StatusLog(){
        pointersMap = new TreeMap<>();
        stagedForAddition = new TreeMap<>();
        stagedForRemoval = new TreeSet<>();
    }


    public void setPointer(String pointer, String sha1Commit) {
        pointersMap.put(pointer, sha1Commit);
    }

    public void addFileForAddition(String fileName, String blob) {
        stagedForAddition.put(fileName, blob);
    }

    public void saveStatus() {
        File statusLogSavedFile = join(STATUS_AREA_DIR, "statusLog");
        try {
            statusLogSavedFile.createNewFile();
            writeObject(statusLogSavedFile, this);
        } catch (IOException excp) {
            throw error("Can't save statusLog object file");
        }
    }

    public static StatusLog readStatus() {
        File statusLog = join(STATUS_AREA_DIR, "statusLog");
        return readObject(statusLog, StatusLog.class);
    }

    /**
     * deserialize the commit object which head points to.
     * @return
     */
    public Commit readCurrentCommit() {
        String fileName = this.pointersMap.get("HEAD");
        Commit currentCommit = Commit.readCommit(fileName);
        return currentCommit;
    }

}
