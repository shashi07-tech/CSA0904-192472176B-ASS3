import java.util.*;
import java.util.concurrent.*;
class Result {
    int studentId;
    int marks;
    Result(int studentId, int marks) {
        this.studentId = studentId;
        this.marks = marks;
    }
}
class ResultProcessor implements Callable<Result> {
    private int studentId;
    private String data;
    ResultProcessor(int studentId, String data) {
        this.studentId = studentId;
        this.data = data;
    }
    @Override
    public Result call() throws Exception {
        // Simulate a corrupt file
        if (data.equals("CORRUPT")) {
            throw new Exception("Corrupt score file");
        }
        // Simulate a missing file
        if (data.equals("MISSING")) {
            throw new Exception("File is missing");
        }
        int marks = Integer.parseInt(data);
        if (marks < 0 || marks > 100) {
            throw new Exception("Invalid marks");
        }
        return new Result(studentId, marks);
    }
}
public class Main {
    public static void main(String[] args)
            throws InterruptedException {
        ExecutorService executor =
                Executors.newFixedThreadPool(4);
        List<Future<Result>> futures =
                new ArrayList<>();
        // 10 sample student files
        String[] files = {
            "85",
            "92",
            "CORRUPT",
            "76",
            "MISSING",
            "88",
            "65",
            "101",
            "95",
            "78"
        };
        // Submit tasks
        for (int i = 0; i < files.length; i++) {
            futures.add(
                    executor.submit(
                            new ResultProcessor(
                                    i + 1,
                                    files[i])));
        }
        int success = 0;
        int failure = 0;
        ConcurrentHashMap<Integer, Integer>
                successfulResults =
                new ConcurrentHashMap<>();
        // Get results
        for (int i = 0; i < futures.size(); i++) {
            try {
                Result result =
                        futures.get(i).get();
                successfulResults.put(
                        result.studentId,
                        result.marks);
                success++;
                System.out.println(
                        "Student " +
                        result.studentId +
                        " processed: " +
                        result.marks);
            } catch (ExecutionException e) {
                failure++;
                System.out.println(
                        "Student " +
                        (i + 1) +
                        " failed: " +
                        e.getCause().getMessage());
            }
        }
        executor.shutdown();
        executor.awaitTermination(
                1, TimeUnit.MINUTES);
        System.out.println("\nProcessing Summary");
        System.out.println(
                "Successful files: " + success);
        System.out.println(
                "Failed files: " + failure);
        System.out.println(
                "Total files: " + files.length);
        System.out.println(
                "Results stored: " +
                successfulResults.size());
        System.out.println(
                "\nAll files processed.");
    }
}
