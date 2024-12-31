package com.example.seedChecker;

import com.example.seedChecker.seedGenerator.Connector;
import com.example.seedChecker.seedGenerator.Generator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/control")
@AllArgsConstructor
public class ApiController {
    private final Connector connector;

    //curl http://localhost:8080/control/start
    @GetMapping("/start")
    public String start() {
        try {
            connector.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Application started";
    }

    @GetMapping("/stop")
    public String stop() {
        // Stop logic
        return "Application stopped";
    }

    //curl -X GET http://localhost:8080/control/result
    @GetMapping("/result")
    public String getResult() {
        try {
            return new String(Files.readAllBytes(Paths.get("src/main/java/com/example/seedChecker/result.txt")));
        } catch (IOException e) {
            throw new RuntimeException("Error reading result file: " + e.getMessage());
        }
    }

}