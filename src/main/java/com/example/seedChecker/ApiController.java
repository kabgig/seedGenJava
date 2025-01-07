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
        String desktopPath = System.getProperty("user.home") + "/Desktop/result.txt";
        try {
            Files.write(Paths.get(desktopPath), "test".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
        try {
            connector.turnOn();
            connector.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Application started";
    }

    //curl http://localhost:8080/control/loadBase
    @GetMapping("/loadBase")
    public String loadBase() {
        String SOURCE_FILE_PATH = Paths.get("..", "allAddr.txt").toString();
        try {
            sorter.loadBase(SOURCE_FILE_PATH);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Done";
    }


    //curl http://localhost:8080/control/start2
    @GetMapping("/start2")
    public String start2() {
        String SOURCE_FILE_PATH = Paths.get("..", "allAddr.txt").toString();
        try {
            connector.turnOn();
            connector.connectList(SOURCE_FILE_PATH);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Application started";
    }

    // curl -X GET http://localhost:8080/control/sort
    @GetMapping("/sort")
    public void sort() {
        String TARGET_FILE_PATH = Paths.get("..", "allAddrSorted.txt").toString();
        String SOURCE_FILE_PATH = Paths.get("..", "allAddr.txt").toString();
        try {
            sorter.checkAndAddAddresses(SOURCE_FILE_PATH, TARGET_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // curl -X GET http://localhost:8080/control/extractTsv
    @GetMapping("/extractTsv")
    public void extractTsv() {
        String TARGET_FILE_PATH = Paths.get("..", "allAddrFromTsv.txt").toString();
        String SOURCE_FILE_PATH = Paths.get("..", "allAddr.tsv").toString();
        try {
            sorter.extractTsv(SOURCE_FILE_PATH, TARGET_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // curl -X GET http://localhost:8080/control/checkCut
    @GetMapping("/checkCut")
    public void checkCut() {
        String TARGET_FILE_PATH = Paths.get("..", "allAddrSorted.txt").toString();
        String SOURCE_FILE_PATH = Paths.get("..", "allAddr.txt").toString();
        try {
            sorter.checkCutAndAddAddresses(SOURCE_FILE_PATH, TARGET_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //curl -X GET http://localhost:8080/control/result
    @GetMapping("/result")
    public String getResult() {
        try {
            return new String(Files.readAllBytes(Paths.get("..", "result.txt")));
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