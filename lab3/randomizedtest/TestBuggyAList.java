package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<String> NRAList = new AListNoResizing<>();
        BuggyAList<String> BAList = new BuggyAList<>();
        NRAList.addLast("I");
        NRAList.addLast("Love");
        NRAList.addLast("Dota");
        BAList.addLast("I");
        BAList.addLast("Love");
        BAList.addLast("Dota");

        assertEquals(NRAList.removeLast(), BAList.removeLast());
        assertEquals(NRAList.removeLast(), BAList.removeLast());
        assertEquals(NRAList.removeLast(), BAList.removeLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
                assertEquals(L.size(), B.size());
            } else if (operationNumber == 2){
                // removeLast
                if (L.size() <= 0){
                    continue;
                }
                int lastItem = L.removeLast();
                int BLastItem = B.removeLast();
//                System.out.println("removeLast -> item: " + lastItem);
                assertEquals(lastItem, BLastItem);
            }
        }
    }
}
