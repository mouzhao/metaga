// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.utilities;

import java.io.Writer;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;

public class TimeStamps
{
    private long startTime;
    private BufferedWriter fileOut;
    private boolean alreadyReset;
    
    public TimeStamps(final String filename) {
        this.alreadyReset = false;
        this.startTime = 0L;
        this.initStampFile(filename);
    }
    
    public void resetTime() {
        if (!this.alreadyReset) {
            this.startTime = System.nanoTime();
            this.alreadyReset = true;
        }
    }
    
    private void initStampFile(final String filename) {
        try {
            this.fileOut = new BufferedWriter(new FileWriter(new File(filename), true));
        }
        catch (Exception e) {
            System.out.println("TimeStamps cannot open stamp file");
        }
    }
    
    public void timeStamp() {
        final long endTime = System.nanoTime();
        this.alreadyReset = false;
        double timePassed = endTime - this.startTime;
        timePassed /= 1.0E9;
        try {
            this.fileOut.write(new StringBuilder().append(timePassed).toString());
            this.fileOut.newLine();
            this.fileOut.flush();
        }
        catch (Exception e) {
            System.out.println("TimeStamps cannot write to stamp file");
        }
    }
}
