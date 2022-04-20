package timingtest;
import edu.princeton.cs.algs4.Stopwatch;
import net.sf.saxon.expr.Component;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
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

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int N = 1000;
        AList<Integer> Ns = new AList();
        AList<Double> times = new AList();
        AList<Integer> opCounts = new AList();
        // SLList<String> testList = new SLList();
        SLList<Integer> testList = new SLList<>();
        for (int i = 0; i < 7; i += 1){
            for(int j = 0; j < N; j += 1){
                testList.addLast(233);
            }
            int M = 10000;
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < M; j += 1){
                int Data = testList.getLast();
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
