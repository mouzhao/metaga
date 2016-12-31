// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.tools.tar;

import java.util.Arrays;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class TarBuffer
{
    public static final int DEFAULT_RCDSIZE = 512;
    public static final int DEFAULT_BLKSIZE = 10240;
    private InputStream inStream;
    private OutputStream outStream;
    private byte[] blockBuffer;
    private int currBlkIdx;
    private int currRecIdx;
    private int blockSize;
    private int recordSize;
    private int recsPerBlock;
    private boolean debug;
    
    public TarBuffer(final InputStream inputStream) {
        this(inputStream, 10240);
    }
    
    public TarBuffer(final InputStream inputStream, final int n) {
        this(inputStream, n, 512);
    }
    
    public TarBuffer(final InputStream inStream, final int n, final int n2) {
        this.inStream = inStream;
        this.outStream = null;
        this.initialize(n, n2);
    }
    
    public TarBuffer(final OutputStream outputStream) {
        this(outputStream, 10240);
    }
    
    public TarBuffer(final OutputStream outputStream, final int n) {
        this(outputStream, n, 512);
    }
    
    public TarBuffer(final OutputStream outStream, final int n, final int n2) {
        this.inStream = null;
        this.outStream = outStream;
        this.initialize(n, n2);
    }
    
    private void initialize(final int blockSize, final int recordSize) {
        this.debug = false;
        this.blockSize = blockSize;
        this.recordSize = recordSize;
        this.recsPerBlock = this.blockSize / this.recordSize;
        this.blockBuffer = new byte[this.blockSize];
        if (this.inStream != null) {
            this.currBlkIdx = -1;
            this.currRecIdx = this.recsPerBlock;
        }
        else {
            this.currBlkIdx = 0;
            this.currRecIdx = 0;
        }
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int getRecordSize() {
        return this.recordSize;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public boolean isEOFRecord(final byte[] array) {
        for (int i = 0; i < this.getRecordSize(); ++i) {
            if (array[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public void skipRecord() throws IOException {
        if (this.debug) {
            System.err.println("SkipRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            throw new IOException("reading (via skip) from an output buffer");
        }
        if (this.currRecIdx >= this.recsPerBlock && !this.readBlock()) {
            return;
        }
        ++this.currRecIdx;
    }
    
    public byte[] readRecord() throws IOException {
        if (this.debug) {
            System.err.println("ReadRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            throw new IOException("reading from an output buffer");
        }
        if (this.currRecIdx >= this.recsPerBlock && !this.readBlock()) {
            return null;
        }
        final byte[] array = new byte[this.recordSize];
        System.arraycopy(this.blockBuffer, this.currRecIdx * this.recordSize, array, 0, this.recordSize);
        ++this.currRecIdx;
        return array;
    }
    
    private boolean readBlock() throws IOException {
        if (this.debug) {
            System.err.println("ReadBlock: blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            throw new IOException("reading from an output buffer");
        }
        this.currRecIdx = 0;
        int n = 0;
        int i = this.blockSize;
        while (i > 0) {
            final long n2 = this.inStream.read(this.blockBuffer, n, i);
            if (n2 == -1L) {
                Arrays.fill(this.blockBuffer, n, n + i, (byte)0);
                break;
            }
            n += (int)n2;
            i -= (int)n2;
            if (n2 == this.blockSize || !this.debug) {
                continue;
            }
            System.err.println("ReadBlock: INCOMPLETE READ " + n2 + " of " + this.blockSize + " bytes read.");
        }
        ++this.currBlkIdx;
        return true;
    }
    
    public int getCurrentBlockNum() {
        return this.currBlkIdx;
    }
    
    public int getCurrentRecordNum() {
        return this.currRecIdx - 1;
    }
    
    public void writeRecord(final byte[] array) throws IOException {
        if (this.debug) {
            System.err.println("WriteRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.outStream == null) {
            throw new IOException("writing to an input buffer");
        }
        if (array.length != this.recordSize) {
            throw new IOException("record to write has length '" + array.length + "' which is not the record size of '" + this.recordSize + "'");
        }
        if (this.currRecIdx >= this.recsPerBlock) {
            this.writeBlock();
        }
        System.arraycopy(array, 0, this.blockBuffer, this.currRecIdx * this.recordSize, this.recordSize);
        ++this.currRecIdx;
    }
    
    public void writeRecord(final byte[] array, final int n) throws IOException {
        if (this.debug) {
            System.err.println("WriteRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.outStream == null) {
            throw new IOException("writing to an input buffer");
        }
        if (n + this.recordSize > array.length) {
            throw new IOException("record has length '" + array.length + "' with offset '" + n + "' which is less than the record size of '" + this.recordSize + "'");
        }
        if (this.currRecIdx >= this.recsPerBlock) {
            this.writeBlock();
        }
        System.arraycopy(array, n, this.blockBuffer, this.currRecIdx * this.recordSize, this.recordSize);
        ++this.currRecIdx;
    }
    
    private void writeBlock() throws IOException {
        if (this.debug) {
            System.err.println("WriteBlock: blkIdx = " + this.currBlkIdx);
        }
        if (this.outStream == null) {
            throw new IOException("writing to an input buffer");
        }
        this.outStream.write(this.blockBuffer, 0, this.blockSize);
        this.outStream.flush();
        this.currRecIdx = 0;
        ++this.currBlkIdx;
    }
    
    private void flushBlock() throws IOException {
        if (this.debug) {
            System.err.println("TarBuffer.flushBlock() called.");
        }
        if (this.outStream == null) {
            throw new IOException("writing to an input buffer");
        }
        if (this.currRecIdx > 0) {
            this.writeBlock();
        }
    }
    
    public void close() throws IOException {
        if (this.debug) {
            System.err.println("TarBuffer.closeBuffer().");
        }
        if (this.outStream != null) {
            this.flushBlock();
            if (this.outStream != System.out && this.outStream != System.err) {
                this.outStream.close();
                this.outStream = null;
            }
        }
        else if (this.inStream != null && this.inStream != System.in) {
            this.inStream.close();
            this.inStream = null;
        }
    }
}
