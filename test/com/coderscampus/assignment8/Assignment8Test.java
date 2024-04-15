package com.coderscampus.assignment8;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assignment8Test {
    private final int ITERATION_COUNT = 1000;
    private final int OUTPUT_TXT_LINE_COUNT = 1000000;
    private final int EXPERIMENT_RUN_COUNT = 100;
    private Assignment8 assignment;
    private ConcurrentMap<Integer, Integer> concurrentMap;
    private ExecutorService threadPool;
    //    private Executor threadPoolWrite;
    private List<CompletableFuture<Void>> taskList;

    @BeforeEach
    void setUp() {
        assignment = new Assignment8();
        concurrentMap = new ConcurrentHashMap<>();
        threadPool = Executors.newCachedThreadPool();
//        threadPoolWrite = Executors.newCachedThreadPool();
        taskList = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        taskList.clear();
        concurrentMap.clear();
        threadPool.shutdown();
    }

    @Test
    void assignment8Solution() {
        runExperiment((list) -> list.forEach(num -> concurrentMap.merge(num, 1, Integer::sum)));
        printResult();
    }

//    @Test
//    void thenAcceptStream() {
//        for (int i = 0; i < EXPERIMENT_RUN_COUNT; i++) {
//            runExperiment((list) -> list.forEach(num -> concurrentMap.merge(num, 1, Integer::sum)));
//        }
//    }

//    @Test
//    void thenAcceptParallelStream() {
//        for (int i = 0; i < EXPERIMENT_RUN_COUNT; i++) {
//            runExperiment((list) -> list.parallelStream().forEach(num -> concurrentMap.merge(num, 1, Integer::sum)));
//        }
//    }

//    @Test
//    void thenAcceptThreadPoolExecuteStream() {
//        for (int i = 0; i < EXPERIMENT_RUN_COUNT; i++) {
//            runExperiment((list) -> threadPool.execute(() -> list.forEach(num -> concurrentMap.merge(num, 1, Integer::sum))));
//        }
//    }

//    @Test
//    void thenAcceptThreadPoolExecuteParallelStream() {
//        for (int i = 0; i < EXPERIMENT_RUN_COUNT; i++) {
//            runExperiment((list) -> threadPool.execute(() -> list.parallelStream().forEach(num -> concurrentMap.merge(num, 1, Integer::sum))));
//        }
//    }

//    @Test
//    void thenAcceptAsyncStream() {
//        for (int i = 0; i < EXPERIMENT_RUN_COUNT; i++) {
//            runExperimentAsync((list) -> list.forEach(num -> concurrentMap.merge(num, 1, Integer::sum)));
//        }
//    }

//    @Test
//    void thenAcceptAsyncParallelStream() {
//        for (int i = 0; i < EXPERIMENT_RUN_COUNT; i++) {
//            runExperimentAsync((list) -> list.parallelStream().forEach(num -> concurrentMap.merge(num, 1, Integer::sum)));
//        }
//    }

//    @Test
//    void thenAcceptAsyncThreadPoolExecuteStream() {
//        for (int i = 0; i < EXPERIMENT_RUN_COUNT; i++) {
//            runExperimentAsync((list) -> threadPool.execute(() -> list.forEach(num -> concurrentMap.merge(num, 1, Integer::sum))));
//        }
//    }

//    @Test
//    void thenAcceptAsyncThreadPoolExecuteParallelStream() {
//        for (int i = 0; i < EXPERIMENT_RUN_COUNT; i++) {
//            runExperimentAsync((list) -> threadPool.execute(() -> list.parallelStream().forEach(num -> concurrentMap.merge(num, 1, Integer::sum))));
//        }
//    }

    private void runExperiment(Consumer<List<Integer>> consumer) {
        for (int i = 0; i < ITERATION_COUNT; i++) {
            try {
                CompletableFuture<Void> task = CompletableFuture.supplyAsync(assignment::getNumbers, threadPool)
                        .thenAccept(consumer);
                taskList.add(task);
            } catch (UnsupportedOperationException | ClassCastException | NullPointerException |
                     IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            taskList.parallelStream()
                    .forEach(CompletableFuture::join);
        } catch (CancellationException | CompletionException e) {
            System.err.println(e.getMessage());
        }

        assertEquals(OUTPUT_TXT_LINE_COUNT, concurrentMap.values()
                .parallelStream()
                .mapToInt(Integer::intValue)
                .sum());
    }

    private void runExperimentAsync(Consumer<List<Integer>> consumer) {
        for (int i = 0; i < ITERATION_COUNT; i++) {
            try {
                CompletableFuture<Void> task = CompletableFuture.supplyAsync(assignment::getNumbers, threadPool)
                        .thenAcceptAsync(consumer, threadPool);
                taskList.add(task);
            } catch (UnsupportedOperationException | ClassCastException | NullPointerException |
                     IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            taskList.parallelStream()
                    .forEach(CompletableFuture::join);
        } catch (CancellationException | CompletionException e) {
            System.err.println(e.getMessage());
        }

        assertEquals(OUTPUT_TXT_LINE_COUNT, concurrentMap.values()
                .parallelStream()
                .mapToInt(Integer::intValue)
                .sum()
        );
    }

    private void printResult() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<Integer, Integer>> iterator = concurrentMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());

            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }

        System.out.println(sb);
    }
}
