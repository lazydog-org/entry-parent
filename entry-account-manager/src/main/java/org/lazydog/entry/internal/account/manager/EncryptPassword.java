package org.lazydog.entry.internal.account.manager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.apache.commons.codec.binary.Base64;


/**
 * Encrypt password.
 *
 * @author  Ron Rickard
 */
public class EncryptPassword {

    private static final String DIGEST_ALGORITHM = "SHA-1";
    private static final String HASH_LABEL = "{SSHA}";

    /**
     * Concatenate two byte arrays.
     *
     * @param  left   the left byte array.
     * @param  right  the right byte array.
     *
     * @return  the concatenated byte array.
     */
    private static byte[] concatenate(byte[] left, byte[] right) {

        // Declare.
        byte[] whole;

        // Copy the left and right byte arrays into the whole byte array.
        whole = new byte[left.length + right.length];
        System.arraycopy(left, 0, whole, 0, left.length);
        System.arraycopy(right, 0, whole, left.length, right.length);

        return whole;
    }

    /**
     * Encrypt the password.
     *
     * @param  password  the password.
     *
     * @return  the password encrypted.
     */
    public static String encrypt(String password) {

       // Declare.
       String encrypt;

       // Initialize.
       encrypt = null;

       try {

           // Declare.
           byte[] passwordHash;
           byte[] salt;
           MessageDigest sha;

           // Generate a random salt.
           salt = randomSalt();

           // Get a SSHA message digest.
           sha = MessageDigest.getInstance(DIGEST_ALGORITHM);
           sha.reset();

           // Update the digest with the password and salt.
           sha.update(password.getBytes());
           sha.update(salt);

           // Complete the hash computation.
           passwordHash = sha.digest();

           // Convert the password hash and salt to base 64.
           encrypt = HASH_LABEL + new String(Base64.encodeBase64(concatenate(passwordHash, salt)));
       }
       catch(NoSuchAlgorithmException e) {
           // Never caught.
       }

       return encrypt;
    }

    /**
     * Generate a random salt.
     *
     * @return  a random salt.
     */
    private static byte[] randomSalt() {

        // Declare.
        byte[] salt;
        Random random;

        // Generate random bytes for salt.
        random = new Random();
        salt = Integer.toHexString(random.nextInt()).getBytes();

        return salt;
    }
}
