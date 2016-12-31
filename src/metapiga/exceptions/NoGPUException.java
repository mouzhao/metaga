// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.exceptions;

public class NoGPUException extends Exception
{
    public NoGPUException() {
        super("You've picked the graphical card calculation option, but MetaPIGA didn't detect one." + System.getProperty("line.separator") + "Calculations will be performed on the CPU.");
    }
    
    public NoGPUException(final Throwable cause) {
        super("You've picked the graphical card calculation option, but MetaPIGA didn't detect one." + System.getProperty("line.separator") + "Calculations will be performed on the CPU.", cause);
    }
}
