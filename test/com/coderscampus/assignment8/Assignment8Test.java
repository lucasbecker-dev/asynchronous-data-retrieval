package com.coderscampus.assignment8;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class Assignment8Test {

    @Test
    void getNumbers() {
        final int ITERATION_COUNT = 1000;
//        final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
        Assignment8 assignment = new Assignment8();
        ConcurrentMap<Integer, Integer> concurrentMap = new ConcurrentHashMap<>();

//        Executor threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
        Executor threadPool = Executors.newCachedThreadPool();
        List<CompletableFuture<List<Integer>>> taskList = new ArrayList<>();

        for (int i=0; i<ITERATION_COUNT; i++) {
            CompletableFuture<List<Integer>> task =
                    CompletableFuture.supplyAsync(() -> assignment.getNumbers(), threadPool);
                    // TODO: figure out how to modify the concurrent map in here correctly
            taskList.add(task);
        }

        while (taskList.stream().filter(CompletableFuture::isDone).count() < ITERATION_COUNT) {}
    }
}