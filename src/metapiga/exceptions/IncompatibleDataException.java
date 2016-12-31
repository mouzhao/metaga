// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.exceptions;

import metapiga.modelization.data.DataType;

public class IncompatibleDataException extends Exception
{
    String errorString;
    
    public IncompatibleDataException(final DataType expectedType, final DataType actualType) {
        super("Expected data type mismatch. Expected type is " + expectedType.verbose() + ", while actual type data type is " + actualType.verbose());
        this.errorString = "";
    }
    
    public IncompatibleDataException(final DataType[] expectedDataTypes, final DataType actualDataType) {
        this.errorString = "";
        String expectedDataTypesString = "";
        for (final DataType expectedDataType : expectedDataTypes) {
            expectedDataTypesString = String.valueOf(expectedDataTypesString) + expectedDataType.verbose() + ", ";
        }
        this.errorString = String.valueOf(this.errorString) + "Expected data type mismatch. Expected types are " + expectedDataTypesString + ", while actual type data type is " + actualDataType.verbose();
    }
    
    @Override
    public void printStackTrace() {
        System.err.println(this.errorString);
        super.printStackTrace();
    }
}
