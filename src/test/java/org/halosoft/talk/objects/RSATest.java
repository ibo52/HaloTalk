/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.halosoft.talk.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ibrahim
 */
public class RSATest {
    
    public RSATest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getPublicKey method, of class RSA.
     */
    @Test
    public void testGetPublicKey() {
        System.out.println("getPublicKey");
        RSA instance = new RSA();

        long[] result = instance.getPublicKey();
        
        assertTrue(result !=null);
        assertTrue(result[0] !=-1);
        assertTrue(result[1] !=-1);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getPrivateKey method, of class RSA.
     */
    @Test
    public void testGetPrivateKey() {
        System.out.println("getPrivateKey");
        RSA instance = new RSA();

        long[] result = instance.getPrivateKey();
       
        assertTrue(result !=null);
        assertTrue(result[0] !=-1);
        assertTrue(result[1] !=-1);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of findPrimes method, of class RSA.
     */
    @Test
    public void testFindPrimes() {
        System.out.println("findPrimes");
        int range = 0;
        boolean[] result = RSA.findPrimes(range);
        
        assertTrue(result !=null);
        //-------------
        range = -1;
        result = RSA.findPrimes(range);
        
        assertTrue(result !=null);

        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of isRelativelyPrime method, of class RSA.
     */
    @Test
    public void testIsRelativelyPrime() {
        System.out.println("isRelativelyPrime");
        long prime1 = 7L;
        long prime2 = 49L;

        boolean result = RSA.isRelativelyPrime(prime1, prime2);
        assertFalse(result);
        //--------------------
        prime1 = 51L;
        prime2 = 11L;

        result = RSA.isRelativelyPrime(prime1, prime2);
        assertTrue(result);
        // TODO review the generated test code and remove the default call to fail.

    }

    /**
     * Test of gcd method, of class RSA.
     */
    @Test
    public void testGcd() {
        System.out.println("gcd");
        long a = 0L;
        long b = 0L;
        
        long expResult = 0L;
        long result = RSA.gcd(a, b);
        
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        a=11L;
        b=55L;
        result=RSA.gcd(a, b);
        assertEquals(11, result);
        //------------
        a=11L;
        b=56L;
        result=RSA.gcd(a, b);
        assertEquals(1, result);
    }

    /**
     * Test of main method, of class RSA.
     */
    @Test
    public void testGeneralCryptography() {

        System.out.println("\tGeneral Test for cryptography"
                +"\n\tRSA:\t\tkey generator"
                + "\n\tEnigma:\t\tencryption/decryption"
                +"\n\tDataManipulator:\thexToString");
        
        int range=(int) Math.pow(10, 4);
        System.out.printf("prime numbers will be selected"
                + " from range[0, %d] \n", range);
        
        long t1=System.nanoTime();
        
        RSA rsa=new RSA();
        
        System.out.println("elapsed seconds for RSA key generation :"
                +(System.nanoTime()-t1)/Math.pow(10, 9));
        //-------------------------
        
        String message="some message to ciphered";

        String ciphered=Enigma.encrypt(message, rsa.getPublicKey());

        String de=Enigma.decrypt(ciphered, rsa.getPrivateKey());

        String decoded=DataManipulator.HexToString(de);
        assertEquals(message, decoded);
    }
    
}
