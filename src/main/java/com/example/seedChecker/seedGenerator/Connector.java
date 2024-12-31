package com.example.seedChecker.seedGenerator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class Connector {
    private final WalletValidator walletValidator;
    private final Generator generator;

    public void connect() {
        int i = 0;
        try {
            while (i < 100) {
                System.out.println("Checked: " + i);
                walletValidator.validateSeedPhrase(generator.generateSeedPhrase());
                i++;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
