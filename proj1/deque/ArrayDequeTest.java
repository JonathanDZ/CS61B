package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.StdRandom;

public class ArrayDequeTest{

    public static void main(String[] args) {
        timeAListConstruction();
//        timeGetLast();
    }

    @Test
    public void randomizedTest(){
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> B = new ArrayDeque<>();

        int TestScope = 7;
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, TestScope);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
//                System.out.println("size: " + size);
                assertEquals(L.size(), B.size());
            } else if (operationNumber == 2){
                // removeLast
                if (L.size() <= 0){
                    assertEquals(L.removeLast(), B.removeLast());
                    continue;
                }
                int lastItem = L.removeLast();
                int BLastItem = B.removeLast();
//                System.out.println("removeLast -> item: " + lastItem);
                assertEquals(lastItem, BLastItem);
            } else if (operationNumber == 3){
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                B.addFirst(randVal);
//                System.out.println("addLast(" + randVal + ")");
            }else if (operationNumber == 4){
                // removeFirst
                if (L.size() <= 0){
                    assertEquals(L.removeFirst(), B.removeFirst());
                    continue;
                }
                int firstItem = L.removeFirst();
                int BFirstItem = B.removeFirst();
//                System.out.println("removeLast -> item: " + lastItem);
                assertEquals(firstItem, BFirstItem);
            }else if (operationNumber == 5){
                // get
                if (L.size() == 0){
                    continue;
                }
                int getLItem = L.get(L.size() - 1);
                int getBItem = B.get(B.size() - 1);
                assertEquals(getLItem, getBItem);
            }else if (operationNumber == 6){
                boolean LEqualsB = L.equals(B);
                boolean BEqualsL = B.equals(L);
                assertEquals(LEqualsB, BEqualsL);
            }
        }
    }

    private static void printTimingTable(LinkedListDeque<Integer> Ns, LinkedListDeque<Double> times, LinkedListDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        int N = 1000;
        LinkedListDeque<Integer> Ns = new LinkedListDeque<>();
        LinkedListDeque<Double> times = new LinkedListDeque<>();
        LinkedListDeque<Integer> opCounts = new LinkedListDeque<>();
//        LinkedListDeque<String> testList = new LinkedListDeque<>();
        ArrayDeque<String> testList = new ArrayDeque<>();
        for (int i = 0; i < 8; i += 1){
            Stopwatch sw = new Stopwatch();
            for(int j = 0; j < N; j += 1){
                testList.addLast("Dota");
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(N);
            times.addLast(timeInSeconds);
            opCounts.addLast(N);
            N = N * 2;
        }
        printTimingTable(Ns, times, opCounts);
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int N = 1000;
        LinkedListDeque<Integer> Ns = new LinkedListDeque<>();
        LinkedListDeque<Double> times = new LinkedListDeque<>();
        LinkedListDeque<Integer> opCounts = new LinkedListDeque<>();
        ArrayDeque<Integer> testList = new ArrayDeque<>();
//        LinkedListDeque<Integer> testList = new LinkedListDeque<>();
        for (int i = 0; i < 7; i += 1){
            for(int j = 0; j < N; j += 1){
                testList.addLast(233);
            }
            int M = 10000;
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < M; j += 1){
                int Data = testList.get(N - 1);
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(N);
            times.addLast(timeInSeconds);
            opCounts.addLast(M);
            N = N * 2;
        }
        printTimingTable(Ns, times, opCounts);
    }

}