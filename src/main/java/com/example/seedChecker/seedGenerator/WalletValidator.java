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
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.base.Network;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;

@AllArgsConstructor
@Service
public class WalletValidator {
    private static final String INFURA_URL = "https://mainnet.infura.io/v3/YOUR_INFURA_PROJECT_ID";
    private static final String BITCOIN_BALANCE_API_URL = "https://api.blockchain.info/haskoin-store/btc/address/";

    public void validateSeedPhrase(List<String> seedPhrase) {
        try {
            // Validate Bitcoin wallet
            NetworkParameters params = MainNetParams.get();
            Context.propagate(new Context(params));
            DeterministicSeed deterministicSeed = new DeterministicSeed(seedPhrase, null, "", 0);
            Wallet wallet = Wallet.fromSeed(params, deterministicSeed, ScriptType.P2PKH);

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
                BigInteger balance = hasBitcoinBalance(address.toString());
                if (balance.equals(BigInteger.ZERO)) {
                    System.out.println("No funds in Bitcoin wallet: " + address);
                } else {
                    String result = "Bitcoin wallet " + address + " validated with balance: " + balance + "\nSeed phrase: " + seedPhrase + "\n";
                    System.out.println(result);
                    appendResultToFile(result);
                }
            }

//            // Validate Ethereum wallet
//            String mnemonic = String.join(" ", seedPhrase);
//            Credentials credentials = WalletUtils.loadBip39Credentials(null, mnemonic);
//            if (credentials.getAddress() == null) {
//                System.out.println("Ethereum address generation failed");
//                return false;
//            }
//
//            // Check Ethereum balance
//            Web3j web3j = Web3j.build(new HttpService(INFURA_URL));
//            EthGetBalance ethGetBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
//            BigInteger balance = ethGetBalance.getBalance();
//            if (balance.equals(BigInteger.ZERO)) {
//                System.out.println("No funds in Ethereum wallet");
//                return false;
//            }

//            return true;
        } catch (Exception e) {
            System.out.println("Validation error: " + e.getMessage());
//            return false;
        }
    }

    private void appendResultToFile(String result) {
        try {
            Files.write(Paths.get("../result.txt"), result.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private BigInteger hasBitcoinBalance(String address) {
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
}
