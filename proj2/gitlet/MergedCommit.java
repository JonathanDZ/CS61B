package gitlet;

import static gitlet.Utils.*;

public class MergedCommit extends Commit {

    private String branchParent;
    /**
     * Commit object constructor;
     * Copy the fileMap of parent commit to its child commit,
     * if parent is null, then create an initial Commit
     *
     * @param message      commit message
     * @param firstParent
     */
    public MergedCommit(String message, Commit firstParent, Commit secondParent) {
        // the firstParent will decide which branch the "log" command will follow.
        super(message, firstParent);
        this.branchParent = sha1(secondParent);
    }

    @Override
    public String toString() {
        String message = "===" + "\n";
        message += "commit " + sha1(this) + "\n";
        message += "Merge: " + this.getParentCommit().substring(0,7) + " "
                + this.branchParent.substring(0,7) + "\n";
        message += "Date: " + this.getDate().toString() + "\n";
        message += this.getMessage() + "\n";
        return message;
    }

}
