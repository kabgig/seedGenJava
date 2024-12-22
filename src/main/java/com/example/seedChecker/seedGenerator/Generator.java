package com.example.seedChecker.seedGenerator;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicSeed;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class Generator {
    public List<String> generateSeedPhrase() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
        secureRandom.nextBytes(entropy);

        List<String> mnemonic = MnemonicCode.INSTANCE.toMnemonic(entropy);
        return mnemonic;
    }
}
