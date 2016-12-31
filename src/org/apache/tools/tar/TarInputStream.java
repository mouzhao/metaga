// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.tools.tar;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class TarInputStream extends FilterInputStream
{
    protected boolean debug;
    protected boolean hasHitEOF;
    protected int entrySize;
    protected int entryOffset;
    protected byte[] readBuf;
    protected TarBuffer buffer;
    protected TarEntry currEntry;
    private boolean v7Format;
    protected byte[] oneBuf;
    
    public TarInputStream(final InputStream inputStream) {
        this(inputStream, 10240, 512);
    }
    
    public TarInputStream(final InputStream inputStream, final int n) {
        this(inputStream, n, 512);
    }
    
    public TarInputStream(final InputStream inputStream, final int n, final int n2) {
        super(inputStream);
        this.buffer = new TarBuffer(inputStream, n, n2);
        this.readBuf = null;
        this.oneBuf = new byte[1];
        this.debug = false;
        this.hasHitEOF = false;
        this.v7Format = false;
    }
    
    public void setDebug(final boolean b) {
        this.debug = b;
        this.buffer.setDebug(b);
    }
    
    public void close() throws IOException {
        this.buffer.close();
    }
    
    public int getRecordSize() {
        return this.buffer.getRecordSize();
    }
    
    public int available() throws IOException {
        return this.entrySize - this.entryOffset;
    }
    
    public long skip(final long n) throws IOException {
        final byte[] array = new byte[8192];
        long n2;
        int read;
        for (n2 = n; n2 > 0L; n2 -= read) {
            read = this.read(array, 0, (int)((n2 > array.length) ? array.length : n2));
            if (read == -1) {
                break;
            }
        }
        return n - n2;
    }
    
    public boolean markSupported() {
        return false;
    }
    
    public void mark(final int n) {
    }
    
    public void reset() {
    }
    
    public TarEntry getNextEntry() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        if (this.currEntry != null) {
            final int n = this.entrySize - this.entryOffset;
            if (this.debug) {
                System.err.println("TarInputStream: SKIP currENTRY '" + this.currEntry.getName() + "' SZ " + this.entrySize + " OFF " + this.entryOffset + "  skipping " + n + " bytes");
            }
            if (n > 0) {
                this.skip(n);
            }
            this.readBuf = null;
        }
        final byte[] record = this.buffer.readRecord();
        if (record == null) {
            if (this.debug) {
                System.err.println("READ NULL RECORD");
            }
            this.hasHitEOF = true;
        }
        else if (this.buffer.isEOFRecord(record)) {
            if (this.debug) {
                System.err.println("READ EOF RECORD");
            }
            this.hasHitEOF = true;
        }
        if (this.hasHitEOF) {
            this.currEntry = null;
        }
        else {
            this.currEntry = new TarEntry(record);
            if (record[257] != 117 || record[258] != 115 || record[259] != 116 || record[260] != 97 || record[261] != 114) {
                this.v7Format = true;
            }
            if (this.debug) {
                System.err.println("TarInputStream: SET CURRENTRY '" + this.currEntry.getName() + "' size = " + this.currEntry.getSize());
            }
            this.entryOffset = 0;
            this.entrySize = (int)this.currEntry.getSize();
        }
        if (this.currEntry != null && this.currEntry.isGNULongNameEntry()) {
            final StringBuffer sb = new StringBuffer();
            final byte[] array = new byte[256];
            int read;
            while ((read = this.read(array)) >= 0) {
                sb.append(new String(array, 0, read));
            }
            this.getNextEntry();
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\0') {
                sb.deleteCharAt(sb.length() - 1);
            }
            this.currEntry.setName(sb.toString());
        }
        return this.currEntry;
    }
    
    public int read() throws IOException {
        return (this.read(this.oneBuf, 0, 1) == -1) ? -1 : (this.oneBuf[0] & 0xFF);
    }
    
    public int read(final byte[] array, int n, int i) throws IOException {
        int n2 = 0;
        if (this.entryOffset >= this.entrySize) {
            return -1;
        }
        if (i + this.entryOffset > this.entrySize) {
            i = this.entrySize - this.entryOffset;
        }
        if (this.readBuf != null) {
            final int n3 = (i > this.readBuf.length) ? this.readBuf.length : i;
            System.arraycopy(this.readBuf, 0, array, n, n3);
            if (n3 >= this.readBuf.length) {
                this.readBuf = null;
            }
            else {
                final int n4 = this.readBuf.length - n3;
                final byte[] readBuf = new byte[n4];
                System.arraycopy(this.readBuf, n3, readBuf, 0, n4);
                this.readBuf = readBuf;
            }
            n2 += n3;
            i -= n3;
            n += n3;
        }
        while (i > 0) {
            final byte[] record = this.buffer.readRecord();
            if (record == null) {
                throw new IOException("unexpected EOF with " + i + " bytes unread");
            }
            int n5 = i;
            final int length = record.length;
            if (length > n5) {
                System.arraycopy(record, 0, array, n, n5);
                System.arraycopy(record, n5, this.readBuf = new byte[length - n5], 0, length - n5);
            }
            else {
                n5 = length;
                System.arraycopy(record, 0, array, n, length);
            }
            n2 += n5;
            i -= n5;
            n += n5;
        }
        this.entryOffset += n2;
        return n2;
    }
    
    public void copyEntryContents(final OutputStream outputStream) throws IOException {
        final byte[] array = new byte[32768];
        while (true) {
            final int read = this.read(array, 0, array.length);
            if (read == -1) {
                break;
            }
            outputStream.write(array, 0, read);
        }
    }
}
