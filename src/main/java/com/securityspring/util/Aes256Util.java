package com.securityspring.util;

import lombok.experimental.UtilityClass;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@UtilityClass
public class Aes256Util {

    private static final int KEY_LENGTH = 256;

    private static final int ITERATION_COUNT = 65536;

    static final Logger LOGGER = LoggerFactory.getLogger("Aes256Util");

    public static final String AES = "AES";

    public static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static final String ENCRYPT_FAILED = "Encrypt failed";

    public static final String DECRYPT_FAILED = "Decrypt failed";

    public static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";


    public static String encrypt(final String strToEncrypt,
                                 final String secretKey,
                                 final String salt)
            throws BadRequestException {
        LOGGER.info("Encrypting password");
        try {
            final SecureRandom secureRandom = new SecureRandom();
            final byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            final IvParameterSpec ivspec = new IvParameterSpec(iv);

            final SecretKeySpec secretKeySpec = getSecretKeySpec(secretKey, salt);

            final Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

            final byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            final byte[] encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (final Exception e) {
            throw new BadRequestException(ENCRYPT_FAILED);
        }

    }


    public static String decrypt(final String strToDecrypt,
                                 final String secretKey,
                                 final String salt) throws BadRequestException
              {
        LOGGER.info("Decrypting password");
        try {
            final byte[] encryptedData = Base64.getDecoder().decode(strToDecrypt);
            final SecureRandom secureRandom = new SecureRandom();
            final byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);

            final SecretKeySpec secretKeySpec = getSecretKeySpec(secretKey, salt);
            final IvParameterSpec ivspec = new IvParameterSpec(iv);

            final Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

            final byte[] cipherText = new byte[encryptedData.length - 16];
            System.arraycopy(encryptedData, 16, cipherText, 0, cipherText.length);

            final byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new BadRequestException(DECRYPT_FAILED);
        }

    }

    private static SecretKeySpec getSecretKeySpec(final String secretKey,
                                                  final String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        final KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        final SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), AES);
    }

}
