/* BlumBlumShub.java: implementation of the Blum Blum Shub PRNG

  Copyright 2016 Graffiti Plum.

*/

package org.hacktivity;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.lang.Double;
import java.lang.Math;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class BlumBlumShub {
    private BigInteger TWO =
            BigInteger.ONE.add(BigInteger.ONE);
    private static int MINKEYLEN = 16;
    private static SecureRandom rng = new SecureRandom();
    public int key_bitlen;
    public boolean improved;
    public BigInteger n, x;

    public void gen_blumint() {
        //if (key_bitlen < this.MINKEYLEN)
        //	return(); // THROW

        int pbits = ((this.key_bitlen) / 2 + 1);
        int qbits = (this.key_bitlen) / 2 + (this.key_bitlen % 2);
        BigInteger p = BigInteger.ZERO;
        BigInteger q = BigInteger.ZERO;

        for (p = p.probablePrime(pbits, rng);
             !p.testBit(1);
             p = p.probablePrime(pbits, rng))
            ;
        for (q = q.probablePrime(qbits, rng);
             !q.testBit(1);
             q = q.probablePrime(qbits, rng))
            ;

        this.n = p.multiply(q);
        this.key_bitlen = this.n.toString(2).length();
    }

    public void gen_x() {
        Double seedBytes = new Double(Math.ceil(this.key_bitlen / 8.0));
        byte seed[] = rng.generateSeed(seedBytes.intValue());
        this.x = new BigInteger(1, seed);

        while (this.x.gcd(this.n).compareTo(BigInteger.ONE) != 0) {
            this.x.add(BigInteger.ONE);
        }

        // x[0] = x^2 (mod n)
        this.x = this.x.modPow(TWO, n);
    }

    public byte[] randBytes(int nbytes) {
        ByteArrayOutputStream alist = new ByteArrayOutputStream();

        if (this.improved == false) {
        /* basic implementation (only keep parity) */
            {
                int i;
                for (i = 0; i < nbytes; i++) {
                    int j;
                    int b = 0;
			    /* we keep the parity (least significant bit) */
                    for (j = 7; j >= 0; j--) {
				    /* x[n+1] = x[n]^2 (mod blumint) */
                        this.x = this.x.modPow(TWO, n);

                        if (this.x.testBit(0))
                            b |= (1 << j);
                    }

                    alist.write(b);
                }
                return (alist.toByteArray());
            }
        } else {
		/* improved implementation (keep log2(log2(n)) bits */
            int loglogblum =
                    new Double(Math.log(1.0 * this.key_bitlen) /
                            Math.log(2.0)).intValue();

            int byt = 0, bit = 0, b = 0, i;

            for (; ; ) {
			/* x[n+1] = x[n]^2 (mod blumint) */
                this.x = this.x.modPow(TWO, n);

                for (i = 0; i < loglogblum; i++) {
                    if (byt == nbytes)
                        return (alist.toByteArray());

				/* get the ith bit of x */
                    if (this.x.testBit(i))
                        b |= (1 << (7 - bit));

                    if (bit == 7) {
                        alist.write(b);
                        byt++;
                        b = 0;
                        bit = 0;
                    } else {
                        bit++;
                    }
                }
            }
        }
    }

    public ArrayList <Integer> randInt(int base, int nmemb) {
        int i;
        ArrayList rndint = new ArrayList();

	/* we waste a few precious bits here unless we're a power of 256 */
        Double nbytes =
                new Double(Math.ceil(nmemb *
                        (Math.log(base) / Math.log(256.0))));

        byte rndbuf[] = randBytes(nbytes.intValue());

        BigInteger bn = new BigInteger(1, rndbuf);

        for (i = 0; i < nmemb + 1; i++) {
            BigInteger tmp = new BigInteger(new Integer(base).toString());
            BigInteger q;

            tmp = tmp.pow(nmemb - i);
            q = bn.divide(tmp);
            if (i > 0)
                rndint.add(new Integer(q.intValue()));

            tmp = q.multiply(tmp);
            bn = bn.subtract(tmp);
        }

        return (rndint);
    }

    public BlumBlumShub(int keybits) {
        byte trash[] = rng.generateSeed(1024);
        this.rng.nextBytes(trash);
        this.key_bitlen = keybits;
        this.improved = true;
        this.gen_blumint();
        this.gen_x();
    }
}