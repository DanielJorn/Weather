package example.site.lib2;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MyClass {

    public static void main(String[] args) throws Exception {

        Supplier newsSupplier = () -> NewsService.getMessage();

        CompletableFuture<String> reader = CompletableFuture.supplyAsync(newsSupplier);
        CompletableFuture.completedFuture("!!")
                .thenCombine(reader, (a, b) -> "a = " + a + " b = " + b)
                .thenAccept(result -> System.out.println(result))
                .get();
    }

    public static class NewsService {
        public static String getMessage() {
            try {
                Thread.currentThread().sleep(3000);
                return "Message";
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}