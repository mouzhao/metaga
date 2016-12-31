// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.tools.tar;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class TarOutputStream extends FilterOutputStream
{
    public static final int LONGFILE_ERROR = 0;
    public static final int LONGFILE_TRUNCATE = 1;
    public static final int LONGFILE_GNU = 2;
    protected boolean debug;
    protected int currSize;
    protected int currBytes;
    protected byte[] oneBuf;
    protected byte[] recordBuf;
    protected int assemLen;
    protected byte[] assemBuf;
    protected TarBuffer buffer;
    protected int longFileMode;
    
    public TarOutputStream(final OutputStream outputStream) {
        this(outputStream, 10240, 512);
    }
    
    public TarOutputStream(final OutputStream outputStream, final int n) {
        this(outputStream, n, 512);
    }
    
    public TarOutputStream(final OutputStream outputStream, final int n, final int n2) {
        super(outputStream);
        this.longFileMode = 0;
        this.buffer = new TarBuffer(outputStream, n, n2);
        this.debug = false;
        this.assemLen = 0;
        this.assemBuf = new byte[n2];
        this.recordBuf = new byte[n2];
        this.oneBuf = new byte[1];
    }
    
    public void setLongFileMode(final int longFileMode) {
        this.longFileMode = longFileMode;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public void setBufferDebug(final boolean debug) {
        this.buffer.setDebug(debug);
    }
    
    public void finish() throws IOException {
        this.writeEOFRecord();
        this.writeEOFRecord();
    }
    
    public void close() throws IOException {
        this.finish();
        this.buffer.close();
    }
    
    public int getRecordSize() {
        return this.buffer.getRecordSize();
    }
    
    public void putNextEntry(final TarEntry tarEntry) throws IOException {
        if (tarEntry.getName().length() >= 100) {
            if (this.longFileMode == 2) {
                final TarEntry tarEntry2 = new TarEntry("././@LongLink", (byte)76);
                tarEntry2.setSize(tarEntry.getName().length() + 1);
                this.putNextEntry(tarEntry2);
                this.write(tarEntry.getName().getBytes());
                this.write(0);
                this.closeEntry();
            }
            else if (this.longFileMode != 1) {
                throw new RuntimeException("file name '" + tarEntry.getName() + "' is too long ( > " + 100 + " bytes)");
            }
        }
        tarEntry.writeEntryHeader(this.recordBuf);
        this.buffer.writeRecord(this.recordBuf);
        this.currBytes = 0;
        if (tarEntry.isDirectory()) {
            this.currSize = 0;
        }
        else {
            this.currSize = (int)tarEntry.getSize();
        }
    }
    
    public void closeEntry() throws IOException {
        if (this.assemLen > 0) {
            for (int i = this.assemLen; i < this.assemBuf.length; ++i) {
                this.assemBuf[i] = 0;
            }
            this.buffer.writeRecord(this.assemBuf);
            this.currBytes += this.assemLen;
            this.assemLen = 0;
        }
        if (this.currBytes < this.currSize) {
            throw new IOException("entry closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
        }
    }
    
    public void write(final int n) throws IOException {
        this.oneBuf[0] = (byte)n;
        this.write(this.oneBuf, 0, 1);
    }
    
    public void write(final byte[] array) throws IOException {
        this.write(array, 0, array.length);
    }
    
    public void write(final byte[] array, int n, int i) throws IOException {
        if (this.currBytes + i > this.currSize) {
            throw new IOException("request to write '" + i + "' bytes exceeds size in header of '" + this.currSize + "' bytes");
        }
        if (this.assemLen > 0) {
            if (this.assemLen + i >= this.recordBuf.length) {
                final int n2 = this.recordBuf.length - this.assemLen;
                System.arraycopy(this.assemBuf, 0, this.recordBuf, 0, this.assemLen);
                System.arraycopy(array, n, this.recordBuf, this.assemLen, n2);
                this.buffer.writeRecord(this.recordBuf);
                this.currBytes += this.recordBuf.length;
                n += n2;
                i -= n2;
                this.assemLen = 0;
            }
            else {
                System.arraycopy(array, n, this.assemBuf, this.assemLen, i);
                n += i;
                this.assemLen += i;
                i -= i;
            }
        }
        while (i > 0) {
            if (i < this.recordBuf.length) {
                System.arraycopy(array, n, this.assemBuf, this.assemLen, i);
                this.assemLen += i;
                break;
            }
            this.buffer.writeRecord(array, n);
            final int length = this.recordBuf.length;
            this.currBytes += length;
            i -= length;
            n += length;
        }
    }
    
    private void writeEOFRecord() throws IOException {
        for (int i = 0; i < this.recordBuf.length; ++i) {
            this.recordBuf[i] = 0;
        }
        this.buffer.writeRecord(this.recordBuf);
    }
}
