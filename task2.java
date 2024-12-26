import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class task2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Route> trainOption = CompletableFuture.supplyAsync(() -> {
            simulateDelay("Перевірка поїзда");
            return new Route("Поїзд", 100, 6);
        });

        CompletableFuture<Route> busOption = CompletableFuture.supplyAsync(() -> {
            simulateDelay("Перевірка автобуса");
            return new Route("Автобус", 50, 8);
        });

        CompletableFuture<Route> planeOption = CompletableFuture.supplyAsync(() -> {
            simulateDelay("Перевірка літака");
            return new Route("Літак", 300, 2);
        });

        CompletableFuture<Void> allOptions = CompletableFuture.allOf(trainOption, busOption, planeOption)
                .thenRun(() -> System.out.println("Усі варіанти транспорту перевірено."));

        allOptions.get();

        CompletableFuture<Route> bestGroundOption = trainOption.thenCombine(busOption, (train, bus) -> {
            return train.totalTime() < bus.totalTime() ? train : bus;
        });

        CompletableFuture<Route> bestOption = bestGroundOption.thenCombine(planeOption, (ground, plane) -> {
            return ground.totalCost() < plane.totalCost() ? ground : plane;
        });

        CompletableFuture<String> detailedInfo = bestOption.thenCompose(route -> CompletableFuture.supplyAsync(() -> {
            simulateDelay("Отримання детальної інформації для: " + route.getTransportType());
            return "Маршрут: " + route.getTransportType() + ", ціна: " + route.totalCost() +
                    ", час у дорозі: " + route.totalTime() + " годин.";
        }));

        CompletableFuture<Object> firstAvailableResult = CompletableFuture.anyOf(trainOption, busOption, planeOption)
                .thenApply(result -> "Найкращий варіант: " + ((Route) result).getTransportType());

        System.out.println(detailedInfo.get());
        System.out.println(firstAvailableResult.get());
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
}
