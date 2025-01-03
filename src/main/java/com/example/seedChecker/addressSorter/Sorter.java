package com.example.seedChecker.addressSorter;

import com.example.seedChecker.seedGenerator.WalletValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.StandardOpenOption;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class Sorter {
    private final WalletValidator walletValidator;

    public void checkAndAddAddresses(String sourceFilePath, String targetFilePath) throws IOException {
        List<String> sourceAddresses = Files.readAllLines(Paths.get(sourceFilePath));
        Set<String> targetAddresses = new HashSet<>(Files.readAllLines(Paths.get(targetFilePath)));

        int count = 0;
        for (String address : sourceAddresses) {
            if (!targetAddresses.contains(address)) {
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
}
