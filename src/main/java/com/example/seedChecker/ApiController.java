package com.example.seedChecker;

import com.example.seedChecker.addressSorter.Sorter;
import com.example.seedChecker.seedGenerator.Connector;
import com.example.seedChecker.seedGenerator.Generator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@RestController
@RequestMapping("/control")
@AllArgsConstructor
public class ApiController {
    private final Connector connector;
    private final Sorter sorter;

    //curl http://localhost:8080/control/start
    @GetMapping("/start")
    public String start() {
       // Files.write(Paths.get("../result.txt"), result.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        try {
            connector.turnOn();
            connector.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Application started";
    }

//    @GetMapping("/stop")
//    public String stop() {
//        // Stop logic
//        return "Application stopped";
//    }

    // curl -X GET http://localhost:8080/control/sort
    @GetMapping("/sort")
    public void sort() {
        String TARGET_FILE_PATH = Paths.get("..", "allAddrSorted.txt").toString();
        String SOURCE_FILE_PATH = Paths.get("..", "allAddr.txt").toString();
        //String SOURCE_FILE_PATH = "../allAddr.txt";
        //String TARGET_FILE_PATH = "../allAddrSorted.txt";
        try {
            sorter.checkAndAddAddresses(SOURCE_FILE_PATH, TARGET_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //curl -X GET http://localhost:8080/control/result
    @GetMapping("/result")
    public String getResult() {
        try {
            return new String(Files.readAllBytes(Paths.get("../result.txt")));
        } catch (IOException e) {
            throw new RuntimeException("Error reading result file: " + e.getMessage());
        }
    }

    //curl http://localhost:8080/control/turnOff
    @GetMapping("/turnOff")
    public String turnOff() {
        connector.turnOff();
        return "Switch turned off";
    }

}