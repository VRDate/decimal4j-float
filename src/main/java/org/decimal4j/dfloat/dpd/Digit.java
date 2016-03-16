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
package org.decimal4j.dfloat.dpd;

import java.io.IOException;

/**
 * Digit deals with single digits and sequences of digits in DPD encoded values.
 */
public final class Digit {

	private Digit() {
		throw new RuntimeException("No Digit for you!");
	}

	/** 10 bits, 1024*3 values */
	private static final byte[] DPD_TO_DIGITS = {'0','0','0','0','0','1','0','0','2','0','0','3','0','0','4','0','0','5','0','0','6','0','0','7','0','0','8','0','0','9','0','8','0','0','8','1','8','0','0','8','0','1','8','8','0','8','8','1','0','1','0','0','1','1','0','1','2','0','1','3','0','1','4','0','1','5','0','1','6','0','1','7','0','1','8','0','1','9','0','9','0','0','9','1','8','1','0','8','1','1','8','9','0','8','9','1','0','2','0','0','2','1','0','2','2','0','2','3','0','2','4','0','2','5','0','2','6','0','2','7','0','2','8','0','2','9','0','8','2','0','8','3','8','2','0','8','2','1','8','0','8','8','0','9','0','3','0','0','3','1','0','3','2','0','3','3','0','3','4','0','3','5','0','3','6','0','3','7','0','3','8','0','3','9','0','9','2','0','9','3','8','3','0','8','3','1','8','1','8','8','1','9','0','4','0','0','4','1','0','4','2','0','4','3','0','4','4','0','4','5','0','4','6','0','4','7','0','4','8','0','4','9','0','8','4','0','8','5','8','4','0','8','4','1','0','8','8','0','8','9','0','5','0','0','5','1','0','5','2','0','5','3','0','5','4','0','5','5','0','5','6','0','5','7','0','5','8','0','5','9','0','9','4','0','9','5','8','5','0','8','5','1','0','9','8','0','9','9','0','6','0','0','6','1','0','6','2','0','6','3','0','6','4','0','6','5','0','6','6','0','6','7','0','6','8','0','6','9','0','8','6','0','8','7','8','6','0','8','6','1','8','8','8','8','8','9','0','7','0','0','7','1','0','7','2','0','7','3','0','7','4','0','7','5','0','7','6','0','7','7','0','7','8','0','7','9','0','9','6','0','9','7','8','7','0','8','7','1','8','9','8','8','9','9','1','0','0','1','0','1','1','0','2','1','0','3','1','0','4','1','0','5','1','0','6','1','0','7','1','0','8','1','0','9','1','8','0','1','8','1','9','0','0','9','0','1','9','8','0','9','8','1','1','1','0','1','1','1','1','1','2','1','1','3','1','1','4','1','1','5','1','1','6','1','1','7','1','1','8','1','1','9','1','9','0','1','9','1','9','1','0','9','1','1','9','9','0','9','9','1','1','2','0','1','2','1','1','2','2','1','2','3','1','2','4','1','2','5','1','2','6','1','2','7','1','2','8','1','2','9','1','8','2','1','8','3','9','2','0','9','2','1','9','0','8','9','0','9','1','3','0','1','3','1','1','3','2','1','3','3','1','3','4','1','3','5','1','3','6','1','3','7','1','3','8','1','3','9','1','9','2','1','9','3','9','3','0','9','3','1','9','1','8','9','1','9','1','4','0','1','4','1','1','4','2','1','4','3','1','4','4','1','4','5','1','4','6','1','4','7','1','4','8','1','4','9','1','8','4','1','8','5','9','4','0','9','4','1','1','8','8','1','8','9','1','5','0','1','5','1','1','5','2','1','5','3','1','5','4','1','5','5','1','5','6','1','5','7','1','5','8','1','5','9','1','9','4','1','9','5','9','5','0','9','5','1','1','9','8','1','9','9','1','6','0','1','6','1','1','6','2','1','6','3','1','6','4','1','6','5','1','6','6','1','6','7','1','6','8','1','6','9','1','8','6','1','8','7','9','6','0','9','6','1','9','8','8','9','8','9','1','7','0','1','7','1','1','7','2','1','7','3','1','7','4','1','7','5','1','7','6','1','7','7','1','7','8','1','7','9','1','9','6','1','9','7','9','7','0','9','7','1','9','9','8','9','9','9','2','0','0','2','0','1','2','0','2','2','0','3','2','0','4','2','0','5','2','0','6','2','0','7','2','0','8','2','0','9','2','8','0','2','8','1','8','0','2','8','0','3','8','8','2','8','8','3','2','1','0','2','1','1','2','1','2','2','1','3','2','1','4','2','1','5','2','1','6','2','1','7','2','1','8','2','1','9','2','9','0','2','9','1','8','1','2','8','1','3','8','9','2','8','9','3','2','2','0','2','2','1','2','2','2','2','2','3','2','2','4','2','2','5','2','2','6','2','2','7','2','2','8','2','2','9','2','8','2','2','8','3','8','2','2','8','2','3','8','2','8','8','2','9','2','3','0','2','3','1','2','3','2','2','3','3','2','3','4','2','3','5','2','3','6','2','3','7','2','3','8','2','3','9','2','9','2','2','9','3','8','3','2','8','3','3','8','3','8','8','3','9','2','4','0','2','4','1','2','4','2','2','4','3','2','4','4','2','4','5','2','4','6','2','4','7','2','4','8','2','4','9','2','8','4','2','8','5','8','4','2','8','4','3','2','8','8','2','8','9','2','5','0','2','5','1','2','5','2','2','5','3','2','5','4','2','5','5','2','5','6','2','5','7','2','5','8','2','5','9','2','9','4','2','9','5','8','5','2','8','5','3','2','9','8','2','9','9','2','6','0','2','6','1','2','6','2','2','6','3','2','6','4','2','6','5','2','6','6','2','6','7','2','6','8','2','6','9','2','8','6','2','8','7','8','6','2','8','6','3','8','8','8','8','8','9','2','7','0','2','7','1','2','7','2','2','7','3','2','7','4','2','7','5','2','7','6','2','7','7','2','7','8','2','7','9','2','9','6','2','9','7','8','7','2','8','7','3','8','9','8','8','9','9','3','0','0','3','0','1','3','0','2','3','0','3','3','0','4','3','0','5','3','0','6','3','0','7','3','0','8','3','0','9','3','8','0','3','8','1','9','0','2','9','0','3','9','8','2','9','8','3','3','1','0','3','1','1','3','1','2','3','1','3','3','1','4','3','1','5','3','1','6','3','1','7','3','1','8','3','1','9','3','9','0','3','9','1','9','1','2','9','1','3','9','9','2','9','9','3','3','2','0','3','2','1','3','2','2','3','2','3','3','2','4','3','2','5','3','2','6','3','2','7','3','2','8','3','2','9','3','8','2','3','8','3','9','2','2','9','2','3','9','2','8','9','2','9','3','3','0','3','3','1','3','3','2','3','3','3','3','3','4','3','3','5','3','3','6','3','3','7','3','3','8','3','3','9','3','9','2','3','9','3','9','3','2','9','3','3','9','3','8','9','3','9','3','4','0','3','4','1','3','4','2','3','4','3','3','4','4','3','4','5','3','4','6','3','4','7','3','4','8','3','4','9','3','8','4','3','8','5','9','4','2','9','4','3','3','8','8','3','8','9','3','5','0','3','5','1','3','5','2','3','5','3','3','5','4','3','5','5','3','5','6','3','5','7','3','5','8','3','5','9','3','9','4','3','9','5','9','5','2','9','5','3','3','9','8','3','9','9','3','6','0','3','6','1','3','6','2','3','6','3','3','6','4','3','6','5','3','6','6','3','6','7','3','6','8','3','6','9','3','8','6','3','8','7','9','6','2','9','6','3','9','8','8','9','8','9','3','7','0','3','7','1','3','7','2','3','7','3','3','7','4','3','7','5','3','7','6','3','7','7','3','7','8','3','7','9','3','9','6','3','9','7','9','7','2','9','7','3','9','9','8','9','9','9','4','0','0','4','0','1','4','0','2','4','0','3','4','0','4','4','0','5','4','0','6','4','0','7','4','0','8','4','0','9','4','8','0','4','8','1','8','0','4','8','0','5','8','8','4','8','8','5','4','1','0','4','1','1','4','1','2','4','1','3','4','1','4','4','1','5','4','1','6','4','1','7','4','1','8','4','1','9','4','9','0','4','9','1','8','1','4','8','1','5','8','9','4','8','9','5','4','2','0','4','2','1','4','2','2','4','2','3','4','2','4','4','2','5','4','2','6','4','2','7','4','2','8','4','2','9','4','8','2','4','8','3','8','2','4','8','2','5','8','4','8','8','4','9','4','3','0','4','3','1','4','3','2','4','3','3','4','3','4','4','3','5','4','3','6','4','3','7','4','3','8','4','3','9','4','9','2','4','9','3','8','3','4','8','3','5','8','5','8','8','5','9','4','4','0','4','4','1','4','4','2','4','4','3','4','4','4','4','4','5','4','4','6','4','4','7','4','4','8','4','4','9','4','8','4','4','8','5','8','4','4','8','4','5','4','8','8','4','8','9','4','5','0','4','5','1','4','5','2','4','5','3','4','5','4','4','5','5','4','5','6','4','5','7','4','5','8','4','5','9','4','9','4','4','9','5','8','5','4','8','5','5','4','9','8','4','9','9','4','6','0','4','6','1','4','6','2','4','6','3','4','6','4','4','6','5','4','6','6','4','6','7','4','6','8','4','6','9','4','8','6','4','8','7','8','6','4','8','6','5','8','8','8','8','8','9','4','7','0','4','7','1','4','7','2','4','7','3','4','7','4','4','7','5','4','7','6','4','7','7','4','7','8','4','7','9','4','9','6','4','9','7','8','7','4','8','7','5','8','9','8','8','9','9','5','0','0','5','0','1','5','0','2','5','0','3','5','0','4','5','0','5','5','0','6','5','0','7','5','0','8','5','0','9','5','8','0','5','8','1','9','0','4','9','0','5','9','8','4','9','8','5','5','1','0','5','1','1','5','1','2','5','1','3','5','1','4','5','1','5','5','1','6','5','1','7','5','1','8','5','1','9','5','9','0','5','9','1','9','1','4','9','1','5','9','9','4','9','9','5','5','2','0','5','2','1','5','2','2','5','2','3','5','2','4','5','2','5','5','2','6','5','2','7','5','2','8','5','2','9','5','8','2','5','8','3','9','2','4','9','2','5','9','4','8','9','4','9','5','3','0','5','3','1','5','3','2','5','3','3','5','3','4','5','3','5','5','3','6','5','3','7','5','3','8','5','3','9','5','9','2','5','9','3','9','3','4','9','3','5','9','5','8','9','5','9','5','4','0','5','4','1','5','4','2','5','4','3','5','4','4','5','4','5','5','4','6','5','4','7','5','4','8','5','4','9','5','8','4','5','8','5','9','4','4','9','4','5','5','8','8','5','8','9','5','5','0','5','5','1','5','5','2','5','5','3','5','5','4','5','5','5','5','5','6','5','5','7','5','5','8','5','5','9','5','9','4','5','9','5','9','5','4','9','5','5','5','9','8','5','9','9','5','6','0','5','6','1','5','6','2','5','6','3','5','6','4','5','6','5','5','6','6','5','6','7','5','6','8','5','6','9','5','8','6','5','8','7','9','6','4','9','6','5','9','8','8','9','8','9','5','7','0','5','7','1','5','7','2','5','7','3','5','7','4','5','7','5','5','7','6','5','7','7','5','7','8','5','7','9','5','9','6','5','9','7','9','7','4','9','7','5','9','9','8','9','9','9','6','0','0','6','0','1','6','0','2','6','0','3','6','0','4','6','0','5','6','0','6','6','0','7','6','0','8','6','0','9','6','8','0','6','8','1','8','0','6','8','0','7','8','8','6','8','8','7','6','1','0','6','1','1','6','1','2','6','1','3','6','1','4','6','1','5','6','1','6','6','1','7','6','1','8','6','1','9','6','9','0','6','9','1','8','1','6','8','1','7','8','9','6','8','9','7','6','2','0','6','2','1','6','2','2','6','2','3','6','2','4','6','2','5','6','2','6','6','2','7','6','2','8','6','2','9','6','8','2','6','8','3','8','2','6','8','2','7','8','6','8','8','6','9','6','3','0','6','3','1','6','3','2','6','3','3','6','3','4','6','3','5','6','3','6','6','3','7','6','3','8','6','3','9','6','9','2','6','9','3','8','3','6','8','3','7','8','7','8','8','7','9','6','4','0','6','4','1','6','4','2','6','4','3','6','4','4','6','4','5','6','4','6','6','4','7','6','4','8','6','4','9','6','8','4','6','8','5','8','4','6','8','4','7','6','8','8','6','8','9','6','5','0','6','5','1','6','5','2','6','5','3','6','5','4','6','5','5','6','5','6','6','5','7','6','5','8','6','5','9','6','9','4','6','9','5','8','5','6','8','5','7','6','9','8','6','9','9','6','6','0','6','6','1','6','6','2','6','6','3','6','6','4','6','6','5','6','6','6','6','6','7','6','6','8','6','6','9','6','8','6','6','8','7','8','6','6','8','6','7','8','8','8','8','8','9','6','7','0','6','7','1','6','7','2','6','7','3','6','7','4','6','7','5','6','7','6','6','7','7','6','7','8','6','7','9','6','9','6','6','9','7','8','7','6','8','7','7','8','9','8','8','9','9','7','0','0','7','0','1','7','0','2','7','0','3','7','0','4','7','0','5','7','0','6','7','0','7','7','0','8','7','0','9','7','8','0','7','8','1','9','0','6','9','0','7','9','8','6','9','8','7','7','1','0','7','1','1','7','1','2','7','1','3','7','1','4','7','1','5','7','1','6','7','1','7','7','1','8','7','1','9','7','9','0','7','9','1','9','1','6','9','1','7','9','9','6','9','9','7','7','2','0','7','2','1','7','2','2','7','2','3','7','2','4','7','2','5','7','2','6','7','2','7','7','2','8','7','2','9','7','8','2','7','8','3','9','2','6','9','2','7','9','6','8','9','6','9','7','3','0','7','3','1','7','3','2','7','3','3','7','3','4','7','3','5','7','3','6','7','3','7','7','3','8','7','3','9','7','9','2','7','9','3','9','3','6','9','3','7','9','7','8','9','7','9','7','4','0','7','4','1','7','4','2','7','4','3','7','4','4','7','4','5','7','4','6','7','4','7','7','4','8','7','4','9','7','8','4','7','8','5','9','4','6','9','4','7','7','8','8','7','8','9','7','5','0','7','5','1','7','5','2','7','5','3','7','5','4','7','5','5','7','5','6','7','5','7','7','5','8','7','5','9','7','9','4','7','9','5','9','5','6','9','5','7','7','9','8','7','9','9','7','6','0','7','6','1','7','6','2','7','6','3','7','6','4','7','6','5','7','6','6','7','6','7','7','6','8','7','6','9','7','8','6','7','8','7','9','6','6','9','6','7','9','8','8','9','8','9','7','7','0','7','7','1','7','7','2','7','7','3','7','7','4','7','7','5','7','7','6','7','7','7','7','7','8','7','7','9','7','9','6','7','9','7','9','7','6','9','7','7','9','9','8','9','9','9'};

	private static final int[] DIGIT_INDEX_SHIFT = {40, 40, 40, 30, 30, 30, 20, 20, 20, 10, 10, 10, 0, 0, 0};
	private static final int[] DIGIT_INDEX_MOD3 = {0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2};
	private static final ThreadLocal<byte[]> CHARS_THREAD_LOCAL = new ThreadLocal<byte[]>() {
		protected byte[] initialValue() {
			return new byte[15];
		};
	};

	/**
	 * Converts 10 DPD bits to an integer value from 0 to 999 and returns the i-th most significant digit of this
	 * 3 digit declet. For a value 837 the method returns '8' (i=0), '3' (i=1) and '7' (i=2), respectively.
	 * 
	 * @param dpd
	 *            DPD value encoding 3 decimal digits
	 * @param i
	 *            the decimal digit index (0-2), 0 for most significant and 2 for least significant digit of the
	 *            declet
	 * @return i-th least significant digit of the decoded decimal declet
	 */
	public static final char decletToCharDigit(final int dpd, int i) {
		return (char)DPD_TO_DIGITS[3*dpd + i];
	}

	/**
	 * Converts 10 DPD bits to an integer value from 0 to 999 and returns the i-th most significant digit of this
	 * 3 digit declet. For a value 837 the method returns 8 (i=0), 3 (i=1) and 7 (i=2), respectively.
	 *
	 * @param dpd
	 *            DPD value encoding 3 decimal digits
	 * @param i
	 *            the decimal digit index (0-2), 0 for most significant and 2 for least significant digit of the
	 *            declet
	 * @return i-th least significant digit of the decoded decimal declet
	 */
	public static final int decletToIntDigit(final int dpd, int i) {
		return decletToCharDigit(dpd, i) - '0';
	}

	/**
	 * Converts 50 DPD bits to an integer value from 0 to 999,999,999,999,999 and returns i-th most significant
	 * digit of this 15 digit value. For instance returns '3' for a value 12837 and i=13.
	 *
	 * @param dpd
	 *            50 DPD value encoding 15 decimal digits
	 * @param i
	 *            the decimal digit index, 0 for most significant and 14 for lest significant digit
	 * @return i-th most significant digit of the decoded decimal value
	 */
	public static final char dpdToCharDigit(final long dpd, final int i) {
		final int declet = (int)((dpd >>> DIGIT_INDEX_SHIFT[i]) & 0x3ff);
		return decletToCharDigit(declet, DIGIT_INDEX_MOD3[i]);
	}

	/**
	 * Converts 50 DPD bits to a long value from 0 to 999,999,999,999,999, including leading zeroes.
	 * 
	 * @param dpd
	 *            50 DPD value encoding 15 decimal digits
	 * @return the long value between 0 and 999,999,999,999,999.
	 */
	public static final String dpdToString(final long dpd) {
		return new String(dpdToCharsThreadLocal(dpd));
	}

	private static final byte[] dpdToCharsThreadLocal(final long dpd) {
		final byte[] chars = CHARS_THREAD_LOCAL.get();
		final int dpd10to30 = (int) (dpd & 0x3fffffff);
		final int dpd50 = (int) ((dpd >>> 40) & 0x3ff);
		final int dpd40 = (int) ((dpd >>> 30) & 0x3ff);
		final int dpd30 = (dpd10to30 >>> 20) & 0x3ff;
		final int dpd20 = (dpd10to30 >>> 10) & 0x3ff;
		final int dpd10 = dpd10to30 & 0x3ff;
		System.arraycopy(DPD_TO_DIGITS, 3*dpd50, chars, 0, 3);
		System.arraycopy(DPD_TO_DIGITS, 3*dpd40, chars, 3, 3);
		System.arraycopy(DPD_TO_DIGITS, 3*dpd30, chars, 6, 3);
		System.arraycopy(DPD_TO_DIGITS, 3*dpd20, chars, 9, 3);
		System.arraycopy(DPD_TO_DIGITS, 3*dpd10, chars, 12, 3);
		return chars;
	}

	public static final StringBuilder dpdToStringBuilder(final long dpd, final StringBuilder stringBuilder) {
		final byte[] chars = dpdToCharsThreadLocal(dpd);
		for (int i = 0; i < chars.length; i++) {
			stringBuilder.append((char)chars[i]);
		}
		return stringBuilder;
	}

	public static final Appendable dpdToAppendable(final long dpd, final Appendable appendable) throws IOException {
		final byte[] chars = dpdToCharsThreadLocal(dpd);
		for (int i = 0; i < chars.length; i++) {
			appendable.append((char)chars[i]);
		}
		return appendable;
	}

	/**
	 * Converts 3 char digits digits to DPD.
	 * 
	 * @param d0
	 *            most significant digit, '0'-'9'
	 * @param d1
	 *            middle significant digit, '0'-'9'
	 * @param d2
	 *            least significant digit, '0'-'9'
	 * @return 10 bit dpd encoding of the same 3 decimal digits
	 */
	public static final int charDigitsToDpd(final char d0, final char d1, final char d2) {
		return intDigitsToDpd(d0 - '0', d1 - '0', d2 - '0');
	}

	/**
	 * Converts 3 integer digits digits to DPD.
	 * 
	 * @param d0
	 *            most significant digit, 0-9
	 * @param d1
	 *            middle significant digit, 0-9
	 * @param d2
	 *            least significant digit, 0-9
	 * @return 10 bit dpd encoding of the same 3 decimal digits
	 */
	public static final int intDigitsToDpd(final int d0, final int d1, final int d2) {
		return Declet.intToDpd(100*d0 + 10*d1 + d2);
	}
}
