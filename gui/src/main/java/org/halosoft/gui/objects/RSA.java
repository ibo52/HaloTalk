/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import java.util.Arrays;

/**
 *
 * @author ibrahim
 * rivest shamir adelman algorithm
 * to create public-private key pairs

 */
public class RSA {
    private long[] PUBLIC_KEY;
    private long[] PRIVATE_KEY;
    
    private boolean[] primes;
    
    public RSA(){
        this( (int)Math.pow(10, 4) );
    }
    
    public RSA(int range){
        
        this.PRIVATE_KEY=new long[2];
        this.PUBLIC_KEY=new long[2];
        
        primes=findPrimes( range);
        
        long[] selected=this.select2Primes(primes);
        
        this.generateKeys(selected[0], selected[1]);
    }
    
    public long[] getPublicKey(){
        return this.PUBLIC_KEY;
    }
    
    public long[] getPrivateKey(){
        return this.PRIVATE_KEY;
    }
    
    private long[] select2Primes(boolean[] list){
        long[] selected=new long[2];
        
        int random=(int) (Math.random()*list.length);
        
        while(list[random]!=true){
            
            random=(random+1)%list.length;
        }
        
        selected[0]=random;
        //-------------------
        random=(int) (Math.random()*list.length);
        
        while(list[random]!=true){
            
            random=(random+1)%list.length;
            
            if (random == selected[0]) {
                random=(random+1)%list.length;
            }
        }
        selected[1]=random;
        
        return selected;
    }
    
    private void generateKeys(long prime1, long prime2){
        long p=prime1;
        long q=prime2;
        
        long n=p*q;
        long z=(p-1)*(q-1);
        
        /*select an e that 1<e<phi(n) relatively prime with z
        e not a factor of n phi(n)=z*/
        long e=-1;
        
        for (long i = z-1; i > 1; i--) {

            if (RSA.isRelativelyPrime(i, z)) {
                e=i;
                break;
            }
        }
        /*choose d such that (e*d)-1 mod z == 0
        * or d=(1+(k*z))/e | d=e-1 % z
        */
        long d=1;
        while( (e*d)%z !=1 ){
                d++;
        }
        
        /*n,e is public key to encrypt
        * n,d is private key to decrypt
        */
        this.PRIVATE_KEY[0]=n;this.PRIVATE_KEY[1]=d;
        this.PUBLIC_KEY[0]=n;  this.PUBLIC_KEY[1]=e;
        
    }
    
    public static boolean[] findPrimes(int range){
        
        if (range<2) {
            return new boolean[]{false};
        
        }else{
            boolean[] primes=new boolean[range];
            Arrays.fill(primes, true);

            double rootOfRange=Math.sqrt(range);

            for (int i = 2; i < rootOfRange; i++) {

                if (primes[i]==true) {

                    int squareOfI=i*i;

                    while(squareOfI<range){
                        primes[squareOfI]=false;
                        squareOfI+=i;
                    }
                }

            }

            primes[0]=false;
            primes[1]=false;
            return primes;   
        }
    }
    
    public static boolean isRelativelyPrime(long prime1, long prime2){
        /*find if these two number relatively prime.
        if gcd(num1,num2)==1->relatively prime*/
        return RSA.gcd(prime1, prime2)==1;
    }
    public static long gcd(long a, long b){
        /*find greatest common divisor between two numbers*/
        while(b!=0){
            
            long temp=a;
            a=b;
            b=temp%b;
        }
        return a;
    }
}
