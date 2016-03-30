/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.dfloat.encode;

import org.decimal4j.dfloat.dpd.Dpd;

/**
 * Encoding implementing the IEEE 754-2008 Decimal 64 Interchange format.
 */
public class Decimal64 {

	private Decimal64() {
		throw new RuntimeException("No Decimal64 for you!");
	}

	public static final int MAX_PRECISION = 16; /* maximum precision (digits) */
	public static final int MAX_EXPONENT = 384; /* maximum adjusted exponent */
	public static final int MIN_EXPONENT = -383; /* minimum adjusted exponent */
	public static final int EXPONENT_BIAS = 398; /* bias for the exponent */
	public static final int MAX_EXPONENT_NOMINAL = MAX_EXPONENT - (MAX_PRECISION - 1);
	public static final int MIN_EXPONENT_NOMINAL = MIN_EXPONENT - (MAX_PRECISION - 1);
	public static final int MAX_STRING_LENGTH = 23; /* maximum string length */
	private static final int DECECONL = 8; /* exp. continuation length */

	public static final long NAN = 0x7c00000000000000L; /* 0 11111 00 ... NaN */
	public static final long SNAN = 0x7e00000000000000L; /* 0 11111 10 ... sNaN */
	public static final long INF = 0x7800000000000000L; /* 0 11110 00 ... Infinity */
	
	public static final long SIGN_BIT_MASK = 0x8000000000000000L; /* 1 00000 00 ... sign bit */

	public static final long ZERO	= 0x2238000000000000L;

	public static final long MIN_NORMAL = 0x043c000000000000L;//10^MIN_EXPONENT
	public static final long MAX_NORMAL = 0x77ff8d7ea4c67fffL;//10^MAX_EXPONENT * (10-10^(1-MAX_PRECISION))

	public static final long COEFF_CONT_MASK = 0x0003ffffffffffffL;
	private static final long EXP_CONT_MASK =  0x03fc000000000000L;

	// @formatter:off
	/* ------------------------------------------------------------------ */
	/* Combination field lookup tables (uInts to save measurable work)    */
	/*								      */
	/*   DECCOMBEXP  - 2 most-significant-bits of exponent (00, 01, or    */
	/*		   10), shifted left for format, or DECFLOAT_Inf/NaN  */
	/*   DECCOMBWEXP - The same, for the next-wider format (unless QUAD)  */
	/*   DECCOMBMSD  - 4-bit most-significant-digit 		      */
	/*		   [0 if the index is a special (Infinity or NaN)]    */
	/*   DECCOMBFROM - 5-bit combination field from EXP top bits and MSD  */
	/*		   (placed in uInt so no shift is needed)	      */
	/*								      */
	/* DECCOMBEXP, DECCOMBWEXP, and DECCOMBMSD are indexed by the sign    */
	/*   and 5-bit combination field (0-63, the second half of the table  */
	/*   identical to the first half)				      */
	/* DECCOMBFROM is indexed by expTopTwoBits*16 + msd		      */
	/*								      */
	/* DECCOMBMSD and DECCOMBFROM are not format-dependent and so are     */
	/* only included once, when QUAD is being built 		      */
	/* ------------------------------------------------------------------ */
	private static final int[] DECCOMBEXP={
	  0, 0, 0, 0, 0, 0, 0, 0,
	  1<<DECECONL, 1<<DECECONL, 1<<DECECONL, 1<<DECECONL,
	  1<<DECECONL, 1<<DECECONL, 1<<DECECONL, 1<<DECECONL,
	  2<<DECECONL, 2<<DECECONL, 2<<DECECONL, 2<<DECECONL,
	  2<<DECECONL, 2<<DECECONL, 2<<DECECONL, 2<<DECECONL,
	  0,	       0,	    1<<DECECONL, 1<<DECECONL,
	  2<<DECECONL, 2<<DECECONL, (int)(INF>>>32), (int)(NAN>>>32),
	  0, 0, 0, 0, 0, 0, 0, 0,
	  1<<DECECONL, 1<<DECECONL, 1<<DECECONL, 1<<DECECONL,
	  1<<DECECONL, 1<<DECECONL, 1<<DECECONL, 1<<DECECONL,
	  2<<DECECONL, 2<<DECECONL, 2<<DECECONL, 2<<DECECONL,
	  2<<DECECONL, 2<<DECECONL, 2<<DECECONL, 2<<DECECONL,
	  0,	       0,	    1<<DECECONL, 1<<DECECONL,
	  2<<DECECONL, 2<<DECECONL, (int)(INF>>>32), (int)(NAN>>>32)};
	
	
	private static final int[] DECCOMBMSD = {
			  0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
			  0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 9, 8, 9, 0, 0,
			  0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
			  0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 9, 8, 9, 0, 0};

	/** DECCOMBFROM is indexed by expTopTwoBits*16 + msd */
	private static final long[] DECCOMBFROM = {
			  0x0000000000000000L, 0x0400000000000000L, 0x0800000000000000L, 0x0C00000000000000L, 0x1000000000000000L, 0x1400000000000000L,
			  0x1800000000000000L, 0x1C00000000000000L, 0x6000000000000000L, 0x6400000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			  0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x2000000000000000L, 0x2400000000000000L,
			  0x2800000000000000L, 0x2C00000000000000L, 0x3000000000000000L, 0x3400000000000000L, 0x3800000000000000L, 0x3C00000000000000L,
			  0x6800000000000000L, 0x6C00000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			  0x0000000000000000L, 0x0000000000000000L, 0x4000000000000000L, 0x4400000000000000L, 0x4800000000000000L, 0x4C00000000000000L,
			  0x5000000000000000L, 0x5400000000000000L, 0x5800000000000000L, 0x5C00000000000000L, 0x7000000000000000L, 0x7400000000000000L,
			  0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L};

	/**
	 * Tests for any zero.
	 * @param dFloat the decimal 64 floating point value
	 * @return true if zero
	 */
	public static final boolean isZero(final long dFloat) {
		return (dFloat & 0x1c03ffffffffffffL) == 0 & (dFloat & 0x6000000000000000L) != 0x6000000000000000L;
	}

	public static final boolean isNaN(final long dFloat) {
		return (dFloat & NAN) == NAN;
	}

	public static final boolean isQuietNaN(final long dFloat) {
		return (dFloat & SNAN) == NAN;
	}

	public static final boolean isSignalingNaN(final long dFloat) {
		return (dFloat & SNAN) == SNAN;
	}

	public static final boolean isInfinite(final long dFloat) {
		return (dFloat & NAN) == INF;
	}

	public static final boolean isPositiveInfinity(final long dFloat) {
		return (dFloat & (NAN | SIGN_BIT_MASK)) == INF;
	}

	public static final boolean isNegativeInfinity(final long dFloat) {
		return (dFloat & (NAN | SIGN_BIT_MASK)) == (INF | SIGN_BIT_MASK);
	}

	public static final boolean isFinite(final long dFloat) {
		return (dFloat & NAN) < INF;
	}

	public static final boolean isNormal(final long dFloat) {
		return isFinite(dFloat) & !isSubnormal(dFloat);
	}

	public static final boolean isSubnormal(final long dFloat) {
		return Decimal64.getExponent(dFloat) == MIN_EXPONENT & Decimal64.getCombinationMSD(dFloat) == 0;
	}

	public static final boolean isCanonical(final long dFloat) {
		return Dpd.isCanonical(dFloat);
	}
	public static final long canonicalize(final long dFloat) {
		return (dFloat & ~COEFF_CONT_MASK) | Dpd.canonicalize(dFloat);
	}

	/**
	 * test for coefficient continuation being zero
	 * @param dFloat the decimal float value in DPD encoding
	 * @return true if zero
	 */
	public static final boolean isCoeffContZero(final long dFloat) {
		return (dFloat & COEFF_CONT_MASK) == 0;
	}
	
	public static final int getExponentContinuation(final long dFloat) {
		return (int)((dFloat & EXP_CONT_MASK) >>> 50);
	}
	
	public static final int getCombinationMSD(final long dFloat) {
		return DECCOMBMSD[(int)(dFloat >>> (26 + 32))];
	}

	public static final int getExponentBiased(final long dFloat) {
		return DECCOMBEXP[(int)(dFloat >>> (26 + 32))] + getExponentContinuation(dFloat);
	}

	public static final int getExponent(final long dFloat) {
		return getExponentBiased(dFloat) - EXPONENT_BIAS;
	}

	public static final long zero(final long sign, final int exp) {
		final int expBiased = exp + EXPONENT_BIAS;
		return (sign & SIGN_BIT_MASK) | DECCOMBFROM[(expBiased >> DECECONL)<<4] | ((((long)expBiased) << 50) & EXP_CONT_MASK);
	}
	public static final long encode(final long sign, final int exp, final int msd, final long dpd) {
		final int expBiased = exp + EXPONENT_BIAS;
		return (sign & SIGN_BIT_MASK) | DECCOMBFROM[((expBiased >> DECECONL)<<4) + msd] | ((((long)expBiased) << 50) & EXP_CONT_MASK) | (dpd & COEFF_CONT_MASK);
	}

	/* Macros and masks for the exponent continuation field and MSD   */
    /* Get the exponent continuation from a decFloat *df as an Int    */
//    #define GETECON(df) ((Int)((DFWORD((df), 0)&0x03ffffff)>>(32-6-DECECONL)))
//    /* Ditto, from the next-wider format			      */
//    #define GETWECON(df) ((Int)((DFWWORD((df), 0)&0x03ffffff)>>(32-6-DECWECONL)))
//    /* Get the biased exponent similarly			      */
//    #define GETEXP(df)	((Int)(DECCOMBEXP[DFWORD((df), 0)>>26]+GETECON(df)))
//    /* Get the unbiased exponent similarly			      */
//    #define GETEXPUN(df) ((Int)GETEXP(df)-DECBIAS)
//    /* Get the MSD similarly (as uInt)				      */
//    #define GETMSD(df)	 (DECCOMBMSD[DFWORD((df), 0)>>26])
//
//    /* Compile-time computes of the exponent continuation field masks */
//    /* full exponent continuation field:			      */
//    #define ECONMASK ((0x03ffffff>>(32-6-DECECONL))<<(32-6-DECECONL))
//    /* same, not including its first digit (the qNaN/sNaN selector):  */
//    #define ECONNANMASK ((0x01ffffff>>(32-6-DECECONL))<<(32-6-DECECONL))


	/* Returns the most significant digit of the mantissa*/
	private static final int getMantissaBitsMSD(final long dpd) {
		return (int)(dpd >>> (26+32));
	}

	public static void main(String... args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(i + ":\t" + Long.toHexString(encode(1, 0, 0, i)));
		}
		for (long i = 1, e=0; i < 10000000000000000L; i*=10,e++) {
			System.out.println("0x" + Long.toHexString(encode(1, (int)e, 0, 1)) + "L");
		}
		System.out.println("MIN_NORMAL=\t" + Long.toHexString(encode(1, MIN_EXPONENT, 1, 0)));
		System.out.println("MAX_NORMAL=\t" + Long.toHexString(encode(1, MAX_EXPONENT_NOMINAL, 9, 999999999999999L)));
	}
}
