package com.coderscampus.assignment8;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Assignment8Test {

    @Test
    void getNumbers() {
        final int ITERATION_COUNT = 1000;
        final int OUTPUT_TXT_LINE_COUNT = 1000000;
//        final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
        Assignment8 assignment = new Assignment8();
        ConcurrentMap<Integer, Integer> concurrentMap = new ConcurrentHashMap<>();

//        Executor threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
//        Executor threadPool = Executors.newWorkStealingPool();
        Executor threadPool = Executors.newCachedThreadPool();

        List<CompletableFuture<Void>> taskList = new ArrayList<>();

        for (int i = 0; i < ITERATION_COUNT; i++) {
            CompletableFuture<Void> task = CompletableFuture.supplyAsync(assignment::getNumbers, threadPool)
                    .thenAccept(list -> {
                        try {
                            // Experiment 1 - stream.forEach within .thenAccept
                            // Result - SUCCESS, 765ms runtime
                            list.forEach(num -> {
                                // concurrentMap.compute(num, (k, v) -> (v == null) ? 1 : v + 1);
                                concurrentMap.merge(num, 1, (Integer::sum));
                            });

//                            // Experiment 2 - parallelStream.forEach within .thenAccept
//                            // Result - SUCCESS, 1178ms runtime
//                            list.parallelStream().forEach(num -> {
//                                // concurrentMap.compute(num, (k, v) -> (v == null) ? 1 : v + 1);
//                                concurrentMap.merge(num, 1, (Integer::sum));
//                            });

//                            // Experiment 3 - threadPool.execute(stream.forEach) within .thenAccept
//                            // Result - FAILURE, 755ms runtime
//                            threadPool.execute(() -> list.forEach(num -> {
//                                // concurrentMap.compute(num, (k, v) -> (v == null) ? 1 : v + 1);
//                                synchronized (concurrentMap) {
//                                    concurrentMap.merge(num, 1, (Integer::sum));
//                                }
//                            }));


//                            // Experiment 4 - threadPool.execute(parallelStream.forEach) within .thenAccept
//                            // Result - FAILURE, 1348ms runtime
//                            threadPool.execute(() -> list.parallelStream().forEach(num -> {
//                                        // concurrentMap.compute(num, (k, v) -> (v == null) ? 1 : v + 1);
//                                        concurrentMap.merge(num, 1, (Integer::sum));
//                                    }
//                            ));
                        } catch (NullPointerException | UnsupportedOperationException | ClassCastException |
                                IllegalArgumentException | RejectedExecutionException e) {
                            System.err.println(e.getMessage());
                        }
                    });
//                    .thenAcceptAsync(list -> {
//                        try {
////                        // Experiment 5 - stream.forEach within .thenAcceptAsync
////                        // Result - SUCCESS, 780ms runtime
////                        list.forEach(num -> {
//////                            synchronized (concurrentMap) {
////                                // concurrentMap.compute(num, (k, v) -> (v == null) ? 1 : v + 1);
////                                concurrentMap.merge(num, 1, (Integer::sum));
//////                            }
////                        });
//
////                        // Experiment 6 - parallelStream.forEach within .thenAcceptAsync
////                        // Result - SUCCESS, 1849ms runtime
////                        list.parallelStream().forEach(num -> {
////                            // concurrentMap.compute(num, (k, v) -> (v == null) ? 1 : v + 1);
////                            concurrentMap.merge(num, 1, (Integer::sum));
////                        });
//
////                        // Experiment 7 - threadPool.execute(stream.forEach) within .thenAcceptAsync
////                        // Result - FAILURE, 766ms runtime
////                        threadPool.execute(() -> list.forEach(num -> {
////                                    // concurrentMap.compute(num, (k, v) -> (v == null) ? 1 : v + 1);
////                                    concurrentMap.merge(num, 1, (Integer::sum));
////                                }
////                        ));
//
////                        // Experiment 8 - threadPool.execute(parallelStream.forEach) within .thenAcceptAsync
////                        // Result - FAILURE, 989ms runtime
////                        threadPool.execute(() -> list.parallelStream().forEach(num -> {
////                                    // concurrentMap.compute(num, (k, v) -> (v == null) ? 1 : v + 1);
////                                    concurrentMap.merge(num, 1, (Integer::sum));
////                                }
////                        ));
//                        } catch (NullPointerException | UnsupportedOperationException | ClassCastException |
//                                 IllegalArgumentException | RejectedExecutionException e) {
//                            System.err.println(e.getMessage());
//                        }
//                    }, threadPool);
            try {
                taskList.add(task);
            } catch (UnsupportedOperationException | ClassCastException |
                     NullPointerException | IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            taskList.parallelStream().forEach(CompletableFuture::join);
        } catch (CancellationException | CompletionException e) {
            System.err.println(e.getMessage());
        }

        assertEquals("{0=66491, 1=66404, 2=66818, 3=66768, 4=66671, 5=66397, 6=66512, " +
                        "7=66564, 8=67454, 9=66732, 10=66628, 11=66688, 12=66578, 13=66699, 14=66596}",
                concurrentMap.toString());
        assertEquals(OUTPUT_TXT_LINE_COUNT,
                concurrentMap
                        .values()
                        .parallelStream()
                        .mapToInt(Integer::intValue)
                        .sum());
    }

}