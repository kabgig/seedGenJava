package com.example.seedChecker.seedGenerator;

import org.springframework.stereotype.Service;

@Service
public class Connector {
    private final WalletValidator walletValidator;
    private final Generator generator;
    private boolean isOn;

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

    public void turnOn() {
        isOn = true;
    }
    public void turnOff() {
        isOn = false;
    }
}
