package gitlet;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs("init", args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs("add", args, 2);
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length == 1) {
                    Utils.message("Please enter a commit message.");
                    System.exit(0);
                }
                validateNumArgs("commit", args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "checkout":
                if (args.length > 4 || args.length < 2) {
                    Utils.message("Incorrect operands");
                    System.exit(0);
                }
                if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        Utils.message("Incorrect operands");
                        System.exit(0);
                    }
                    Repository.checkoutToCommitID(args[1], args[3]);
                }
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        Utils.message("Incorrect operands");
                        System.exit(0);
                    }
                    Repository.checkoutToHead(args[2]);
                }
                if (args.length == 2) {
                    Repository.checkoutToBranch(args[1]);
                }
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                Repository.globalLog();
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            Utils.message("Incorrect operands");
            System.exit(0);
        }
    }

}
