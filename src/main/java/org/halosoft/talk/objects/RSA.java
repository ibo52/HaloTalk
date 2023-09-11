/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.util.Arrays;

/**
 *
 * @author ibrahim
 * rivest shamir adelson algorithm
 * to create public-private key pairs

 */
public class RSA {
    private int[] PUBLIC_KEY;
    private int[] PRIVATE_KEY;
    
    private boolean[] primes;
    
    public RSA(){
        this.PRIVATE_KEY=new int[2];
        this.PUBLIC_KEY=new int[2];
        
        primes=findPrimes((int) Math.pow(10, 6));
        int[] selected=this.select2Primes(primes);
        
        this.generateKeys(selected[0], selected[1]);
        
        System.out.println("selcted keys for RSA:");
        System.out.println("public:"+this.PUBLIC_KEY[0]+" " +this.PUBLIC_KEY[1]);
        System.out.println("private:"+this.PRIVATE_KEY[0]+" " +this.PRIVATE_KEY[1]);
    }
    
    public int[] getPublicKey(){
        return this.PUBLIC_KEY;
    }
    
    public int[] getPrivateKey(){
        return this.PRIVATE_KEY;
    }
    
    private int[] select2Primes(boolean[] list){
        int[] selected=new int[2];
        
        int random=(int) (Math.random()*list.length);
        
        while(list[random]!=true){
            random++;
        }
        
        selected[0]=random;
        //-------------------
        random=(int) (Math.random()*list.length);
        
        while(list[random]!=true){
            random++;
        }
        selected[1]=random;
        
        return selected;
    }
    
    private void generateKeys(int prime1, int prime2){
        int p=prime1;
        int q=prime2;
        
        int n=p*q;
        int z=(p-1)*(q-1);
        
        /*select an e that 1<e<phi(n) relatively prime with z
        e not a factor of n phi(n)=z*/
        int e=-1;
        
        for (int i = z-1; i > 1; i--) {
            
            if (RSA.isRelativelyPrime(i, z)) {
                e=i;
                break;
            }
        }
        /*choose d such that (e*d)-1 mod z == 0
        * or d=1+(k*e*z) | d=e-1 % z
        */
        int d=1;
        
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
        
        return primes;
        
        
    }
    public static boolean isRelativelyPrime(int prime1, int prime2){
        /*find if these two number relatively prime.
        if gcd(num1,num2)==1->relatively prime*/
        return RSA.gcd(prime1, prime2)==1;
    }
    public static int gcd(int a, int b){
        /*find greatest common divisor between two numbers*/
        while(b!=0){
            
            int temp=a;
            a=b;
            b=temp%b;
        }
        return a;
    }
    
    public static void main(String[] args) {
        System.out.println("rsa key generator test");
        long t1=System.nanoTime();
        RSA rsa=new RSA();
        
        System.out.println("elapsed:"+(System.nanoTime()-t1)/Math.pow(10, 9));
    }
}
