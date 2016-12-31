// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.tools.tar;

public class TarUtils
{
    public static long parseOctal(final byte[] array, final int n, final int n2) {
        long n3 = 0L;
        int n4 = 1;
        for (int n5 = n + n2, i = n; i < n5; ++i) {
            if (array[i] == 0) {
                break;
            }
            if (array[i] == 32 || array[i] == 48) {
                if (n4 != 0) {
                    continue;
                }
                if (array[i] == 32) {
                    break;
                }
            }
            n4 = 0;
            n3 = (n3 << 3) + (array[i] - 48);
        }
        return n3;
    }
    
    public static StringBuffer parseName(final byte[] array, final int n, final int n2) {
        final StringBuffer sb = new StringBuffer(n2);
        for (int n3 = n + n2, n4 = n; n4 < n3 && array[n4] != 0; ++n4) {
            sb.append((char)array[n4]);
        }
        return sb;
    }
    
    public static int getNameBytes(final StringBuffer sb, final byte[] array, final int n, final int n2) {
        int i;
        for (i = 0; i < n2 && i < sb.length(); ++i) {
            array[n + i] = (byte)sb.charAt(i);
        }
        while (i < n2) {
            array[n + i] = 0;
            ++i;
        }
        return n + n2;
    }
    
    public static int getOctalBytes(final long n, final byte[] array, final int n2, final int n3) {
        int i = n3 - 1;
        array[n2 + i] = 0;
        --i;
        array[n2 + i] = 32;
        --i;
        if (n == 0L) {
            array[n2 + i] = 48;
            --i;
        }
        else {
            for (long n4 = n; i >= 0 && n4 > 0L; n4 >>= 3, --i) {
                array[n2 + i] = (byte)(48 + (byte)(n4 & 0x7L));
            }
        }
        while (i >= 0) {
            array[n2 + i] = 32;
            --i;
        }
        return n2 + n3;
    }
    
    public static int getLongOctalBytes(final long n, final byte[] array, final int n2, final int n3) {
        final byte[] array2 = new byte[n3 + 1];
        getOctalBytes(n, array2, 0, n3 + 1);
        System.arraycopy(array2, 0, array, n2, n3);
        return n2 + n3;
    }
    
    public static int getCheckSumOctalBytes(final long n, final byte[] array, final int n2, final int n3) {
        getOctalBytes(n, array, n2, n3);
        array[n2 + n3 - 1] = 32;
        array[n2 + n3 - 2] = 0;
        return n2 + n3;
    }
    
    public static long computeCheckSum(final byte[] array) {
        long n = 0L;
        for (int i = 0; i < array.length; ++i) {
            n += (0xFF & array[i]);
        }
        return n;
    }
}
