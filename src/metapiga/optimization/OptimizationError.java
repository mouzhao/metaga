// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.optimization;

public class OptimizationError extends Exception
{
    public OptimizationError() {
    }
    
    public OptimizationError(final String message) {
        super(message);
    }
    
    public OptimizationError(final Throwable cause) {
        super(cause);
    }
    
    public OptimizationError(final String message, final Throwable cause) {
        super(message, cause);
    }
}
