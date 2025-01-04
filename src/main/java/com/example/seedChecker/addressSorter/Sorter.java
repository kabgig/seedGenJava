package com.example.seedChecker.addressSorter;

import com.example.seedChecker.seedGenerator.WalletValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class Sorter {
    private final WalletValidator walletValidator;
    private static final String LINE_NUMBER_FILE = "../lastProcessedLine.txt";

    public void checkAndAddAddresses(String sourceFilePath, String targetFilePath) throws IOException {
        List<String> sourceAddresses = Files.readAllLines(Paths.get(sourceFilePath));
        Set<String> targetAddresses = new HashSet<>(Files.readAllLines(Paths.get(targetFilePath)));
        int count = 0;
        for (String address : sourceAddresses) {
            if (!targetAddresses.contains(address)) {
                System.out.println("checking balance");
                BigInteger balance = walletValidator.hasBitcoinBalance(address);
                if (!balance.equals(BigInteger.ZERO)) {
                    Files.write(Paths.get(targetFilePath), (address + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    targetAddresses.add(address);
                    count++;
                    System.out.println("Count: " + count + " Added: " + address + " Balance: " + balance);
                }
            } else {
                System.out.println("Count: " + count + " Address already exists: " + address);
                count++;
            }
        }
    }

    public void extractTsv(String sourceFilePath, String targetFilePath) throws IOException {
        int count = 0;
        Path path = Paths.get(sourceFilePath);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\t");
                Files.write(Paths.get(targetFilePath), (values[0] + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                System.out.println("Count: " + count + " Address: " + values[0] + "  Balance: " + values[1] + "Added to file");
                count++;
            }
        } catch (IOException e) {
            System.out.println("Error reading TSV file: " + e.getMessage());
        }
    }

    public void checkCutAndAddAddresses(String sourceFilePath, String targetFilePath) throws IOException {
        int lastProcessedLine = readLastProcessedLine();
        int currentLine = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath, true))) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentLine++;
                if (currentLine <= lastProcessedLine) {
                    System.out.println("Skipping line: " + currentLine + " Last processed line: " + lastProcessedLine);
                    continue;
                } else {
                    System.out.println("Processing line: " + currentLine);
                }

                String[] values = line.split("\t");
                BigInteger balance = walletValidator.hasBitcoinBalance(values[0]);
                if (!balance.equals(BigInteger.ZERO)) {
                    Files.write(Paths.get(targetFilePath), (values[0] + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    System.out.println("Processed: " + values[0] + " Balance: " + balance);
                } else {
                    System.out.println("No balance: " + values[0]);
                }
                writeLastProcessedLine(currentLine);
            }
        } catch (IOException e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }
    private int readLastProcessedLine() {
        try {
            if (Files.exists(Paths.get(LINE_NUMBER_FILE))) {
                return Integer.parseInt(new String(Files.readAllBytes(Paths.get(LINE_NUMBER_FILE))).trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading last processed line number: " + e.getMessage());
        }
        return 0;
    }

    private void writeLastProcessedLine(int lineNumber) {
        try {
            Files.write(Paths.get(LINE_NUMBER_FILE), String.valueOf(lineNumber).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error writing last processed line number: " + e.getMessage());
        }
    }

}
