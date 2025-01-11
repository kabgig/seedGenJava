package com.example.seedChecker.seedGenerator;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Connector {
    private final WalletValidator walletValidator;
    private final Generator generator;
    private boolean isOn;
    private final ExecutorService executorService1 = Executors.newSingleThreadExecutor();

    public Connector(WalletValidator walletValidator, Generator generator) {
        this.walletValidator = walletValidator;
        this.generator = generator;
        this.isOn = true;
    }

    public void connect() {
        int i = 0;
        try {
            while (isOn) {
                walletValidator.validateSeedPhrase(generator.generateSeedPhrase());
                System.out.println("Checked: " + i);
                i++;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void connectList() {
        executorService1.submit(() -> {
            long i = 0;
            try {
                while (isOn) {
                    walletValidator.validateSeedPhrase2(generator.generateSeedPhrase());
                    System.out.println("Checked seedphrases: " + i);
                    i++;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
    }

    public void turnOn() {
        isOn = true;
    }
    public void turnOff() {
        isOn = false;
    }
}
