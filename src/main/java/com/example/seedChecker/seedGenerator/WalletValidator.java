package com.example.seedChecker.seedGenerator;

import lombok.AllArgsConstructor;
import org.bitcoinj.base.Address;
import org.bitcoinj.base.ScriptType;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@AllArgsConstructor
@Service
public class WalletValidator {
    private static final String INFURA_URL = "https://mainnet.infura.io/v3/YOUR_INFURA_PROJECT_ID";
    private static final String BITCOIN_BALANCE_API_URL = "https://api.blockchain.info/haskoin-store/btc/address/";
    private final Context context;
    private final NetworkParameters params;

    public WalletValidator() {
        params = MainNetParams.get();
        this.context = new Context(params);
    }

    public void validateSeedPhrase(List<String> seedPhrase) {
        try {
            Context.propagate(context);
            DeterministicSeed deterministicSeed = new DeterministicSeed(seedPhrase, null, "", 0);
            DeterministicKey key = HDKeyDerivation.createMasterPrivateKey(deterministicSeed.getSeedBytes());
            DeterministicKey derivedKey = HDKeyDerivation.deriveChildKey(
                    HDKeyDerivation.deriveChildKey(
                            HDKeyDerivation.deriveChildKey(
                                    HDKeyDerivation.deriveChildKey(
                                            HDKeyDerivation.deriveChildKey(
                                                    key, new ChildNumber(44, true)),
                                            new ChildNumber(0, true)),
                                    new ChildNumber(0, true)),
                            new ChildNumber(0, false)),
                    new ChildNumber(0, false));

            Address address = derivedKey.toAddress(ScriptType.P2PKH, params.network());
            if (address != null) {
                BigInteger balance = hasBitcoinBalance(address.toString());
                if (balance.equals(BigInteger.ZERO)) {
                    System.out.println("No funds in Bitcoin wallet: " + address);
                } else {
                    String result = "Bitcoin wallet " + address + " validated with balance: " + balance + "\nSeed phrase: " + seedPhrase + "\n";
                    System.out.println(result);
                    appendResultToFile(result);
                }
            }
        } catch (Exception e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private void appendResultToFile(String result) {
        String desktopPath = System.getProperty("user.home") + "/Desktop/result.txt";
        try {
            Files.write(Paths.get(desktopPath), result.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public BigInteger hasBitcoinBalance(String address) {
        try {
            URL url = new URL(BITCOIN_BALANCE_API_URL + address + "/balance");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }

            Scanner scanner = new Scanner(url.openStream());
            String inline = "";
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }
            scanner.close();

            JSONObject jsonResponse = new JSONObject(inline);
            BigInteger balance = jsonResponse.getBigInteger("confirmed");
            return balance;
        } catch (Exception e) {
            System.out.println("Error checking Bitcoin balance: " + e.getMessage());
            return BigInteger.ZERO;
        }
    }

    public void validateSeedPhrase2(List<String> seedPhrase, String sourceFilePath, long i) throws IOException {
        Context.propagate(context);
        DeterministicSeed deterministicSeed = new DeterministicSeed(seedPhrase, null, "", 0);

        DeterministicKey key = HDKeyDerivation.createMasterPrivateKey(deterministicSeed.getSeedBytes());
        // modify this for checking multichain wallets
        DeterministicKey derivedKey = HDKeyDerivation.deriveChildKey(
                HDKeyDerivation.deriveChildKey(
                        HDKeyDerivation.deriveChildKey(
                                HDKeyDerivation.deriveChildKey(
                                        HDKeyDerivation.deriveChildKey(
                                                key, new ChildNumber(44, true)),
                                        new ChildNumber(0, true)),
                                new ChildNumber(0, true)),
                        new ChildNumber(0, false)),
                new ChildNumber(0, false));

        Address address = derivedKey.toAddress(ScriptType.P2PKH, params.network());
        if (address != null) {
            int a=0;
            Path path = Paths.get(sourceFilePath);
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split("\t");
                    if (values[0].equals(address.toString())) {
                        String result = "Bitcoin wallet " + values[0] + " validated Seed phrase: " + seedPhrase + "\n";
                        System.out.println(result);
                        appendResultToFile(result);
                        return;
                    } else {
                        System.out.println("Address: " + values[0] + "  Seed #"+ i + " " + seedPhrase + " does not match");
                    }
                    System.out.println("Checked: " + a);
                    a++;
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }


        } else {
            System.out.println("No funds in Bitcoin wallet: " + address);
        }
    }
}
