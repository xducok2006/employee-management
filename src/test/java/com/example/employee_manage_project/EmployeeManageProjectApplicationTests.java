package com.example.employee_manage_project;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

@SpringBootTest
class EmployeeManageProjectApplicationTests {

	@RepeatedTest(100)
	void concurrentTest() throws Exception
	{
		ExecutorService executorService = Executors.newFixedThreadPool(2);

		CountDownLatch start = new CountDownLatch(1);
		int[] count ={0};
		Runnable task = () -> {
			try {
				start.await();

				for (int i = 0; i < 1000; i++) {
					count[0]++;
				}

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		};

		executorService.submit(task);
		executorService.submit(task);
		start.countDown();
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
		System.out.println(count[0]);
	}

}

