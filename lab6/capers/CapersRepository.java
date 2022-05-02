package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = join(CWD,".capers"); // TODO Hint: look at the `join` function in Utils

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        // TODO
        if (CAPERS_FOLDER.exists() == false){
            CAPERS_FOLDER.mkdir();
        }
        File dogsFolder = join(CAPERS_FOLDER, "dogs");
        if (dogsFolder.exists() == false){
            dogsFolder.mkdir();
        }
        File storyFile = join(CAPERS_FOLDER, "story");
//        File storyFile = new File(CAPERS_FOLDER.toString() + "story");
        if (storyFile.exists() == false){
            try {
                storyFile.createNewFile();
            } catch (IOException excp){
                throw error("Can't create story file.");
            }
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO
        File story = join(CAPERS_FOLDER, "story");
        if (story.exists() == true){
            text = readContentsAsString(story) + text + "\n";
            writeContents(story, text);
            System.out.print(text);
        }else {
            throw error("Can't write into story file, because it's not exist");
        }
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO
        File dogFile = join(CAPERS_FOLDER, "dogs", name);
        if (dogFile.exists() == true) {
            throw error("This dog is already exist");
        }
        Dog d = new Dog(name, breed, age);
        d.saveDog();
        System.out.print(d.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // TODO
//        File dogFile = join(CAPERS_FOLDER, "dogs", name);
        Dog d = Dog.fromFile(name);
        d.haveBirthday();
        d.saveDog();
    }
}
