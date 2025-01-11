package com.example.seedChecker.addressSorter;

import com.example.seedChecker.repo.Address;
import com.example.seedChecker.repo.AddressRepository;
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
    private final AddressRepository addressRepository;
    private static final String LINE_NUMBER_FILE = "../lastProcessedLine.txt";
    private static final String LINE_NUMBER_FILE2 = "../lastProcessedLine2.txt";

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
        int lastProcessedLine = readLastProcessedLine(1);
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
                writeLastProcessedLine(currentLine, 1);
            }
        } catch (IOException e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    private int readLastProcessedLine(int i) {
        boolean isExisting = switch (i) {
            case 1 -> Files.exists(Paths.get(LINE_NUMBER_FILE));
            case 2 -> Files.exists(Paths.get(LINE_NUMBER_FILE2));
            default -> false;
        };
        String path = switch (i) {
            case 1 -> LINE_NUMBER_FILE;
            case 2 -> LINE_NUMBER_FILE2;
            default -> "";
        };
        try {
            if (isExisting) {
                return Integer.parseInt(new String(Files.readAllBytes(Paths.get(path))).trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading last processed line number: " + e.getMessage());
        }
        return 0;
    }

    private void writeLastProcessedLine(int lineNumber, int i) {
        String path = switch (i) {
            case 1 -> LINE_NUMBER_FILE;
            case 2 -> LINE_NUMBER_FILE2;
            default -> "";
        };
        try {
            Files.write(Paths.get(path), String.valueOf(lineNumber).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error writing last processed line number: " + e.getMessage());
        }
    }

    public void loadBase(String sourceFilePath) {
        int lastProcessedLine = readLastProcessedLine(2);
        int currentLine = 0;
        int amount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentLine++;
                if (currentLine <= lastProcessedLine) {
                    System.out.println("Skipping line: " + currentLine + " Last processed line: " + lastProcessedLine);
                    continue;
                }

                String[] values = line.split("\t");
                var address = new Address();
                address.setAddress(values[0]);
                amount++;
                try {
                    addressRepository.save(address);
                    System.out.println("Loaded dataset amount: " + amount + " Saved address: " + address.getAddress());
                } catch (Exception e) {
                    System.out.println("Error saving address: " + e.getMessage());
                }
                writeLastProcessedLine(currentLine, 2);
            }
        } catch (IOException e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
//        addressRepository.dropAddressIndex();
//        addressRepository.createAddressIndex();
    }
}
