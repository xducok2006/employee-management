package com.example.employee_manage_project;

import com.example.employee_manage_project.repository.UserRepository;
import com.example.employee_manage_project.service.AttendanceService;
import com.example.employee_manage_project.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Equals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AttendanceServiceConcurrentTest {
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;
    @LocalServerPort
    private int port;
    @Test
    void concurrentTest() throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch go = new CountDownLatch(1);
        HttpClient client = HttpClient.newHttpClient();

        String url = "http://localhost:"+port+"/attendance/check-out";

        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc4OTAxNDQ1MiwiaWF0IjoxNzg5MDEwODUyLCJyb2xlcyI6WyJFTVBMT1lFRSIsIkFETUlOIiwiTUFOQUdFUiJdfQ.nPtVZcXK2dCFLMa9d7cMEnnhfVsQIB_1yiWORwCJl4U";

        Callable<Integer> request = ()->{
            ready.countDown();
            go.await();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).header("Token",accessToken).
                    PUT(HttpRequest.BodyPublishers.noBody()).build();

            HttpResponse<String> response = client.send(httpRequest,HttpResponse.BodyHandlers.ofString());

            System.out.println(Thread.currentThread().getName() +"->"+response.statusCode()+"->"+response.body());

            return response.statusCode();
        };
        Future<Integer> task1 = executor.submit(request);
        Future<Integer> task2 = executor.submit(request);
        ready.await();
        go.countDown();
        int status1 = task1.get();
        int status2 = task2.get();

        executor.shutdown();

        System.out.println("Request 1: " + status1);
        System.out.println("Request 2: " + status2);

        int successCount = 0;
        if(status1 == 200)
            successCount++;
        if(status2 == 200)
            successCount++;

        assertEquals(1,successCount);

    }
}
