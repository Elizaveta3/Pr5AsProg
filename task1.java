import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Arrays;

public class task1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<int[]> source1 = CompletableFuture.supplyAsync(() -> {
            int[] data = generateRandomArray();
            simulateDelay("Дані з джерела 1");
            return data;
        });

        CompletableFuture<int[]> source2 = CompletableFuture.supplyAsync(() -> {
            int[] data = generateRandomArray();
            simulateDelay("Дані з джерела 2");
            return data;
        });

        // allOf() чекає, поки завершаться обидва завдання
        CompletableFuture<Void> allData = CompletableFuture.allOf(source1, source2)
                .thenRun(() -> System.out.println("Обидва джерела готові."));

        allData.get();

        CompletableFuture<Void> processedStage1 = source1.thenCompose(data1 -> {
            System.out.println("Дані з джерела 1: " + Arrays.toString(data1));
            return source2.thenCompose(data2 -> {
                System.out.println("Дані з джерела 2: " + Arrays.toString(data2));
                return CompletableFuture.completedFuture(null);
            });
        });

        processedStage1.get();

        CompletableFuture<int[]> combinedProcessedData = source1.thenCombine(source2, (data1, data2) -> {
            int[] combined = new int[data1.length + data2.length];
            System.arraycopy(data1, 0, combined, 0, data1.length);
            System.arraycopy(data2, 0, combined, data1.length, data2.length);
            return Arrays.stream(combined).map(num -> num + 10).toArray();
        });

        System.out.println("Оброблені дані: " + Arrays.toString(combinedProcessedData.get()));
    }

    private static void simulateDelay(String taskName) {
        try {
            System.out.println("Початок: " + taskName);
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Завершено: " + taskName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Генерація масиву з 5 випадкових чисел
    private static int[] generateRandomArray() {
        Random random = new Random();
        int[] array = new int[5];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(100);
        }
        return array;
    }
}
