package com.example.seedChecker;

import com.example.seedChecker.seedGenerator.Generator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/control")
@AllArgsConstructor
public class ApiController {
    private final Generator generator;

    @GetMapping("/start")
    public String start() {
        try {
            List<String> strings = generator.generateSeedPhrase();
            strings.forEach(System.out::println);
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
}