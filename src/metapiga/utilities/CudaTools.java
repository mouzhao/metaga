// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.utilities;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

public class CudaTools
{
    public static String preparePtxFile(final String cuFileName) throws IOException {
        int endIndex = cuFileName.lastIndexOf(46);
        if (endIndex == -1) {
            endIndex = cuFileName.length() - 1;
        }
        final String ptxFileName = String.valueOf(cuFileName.substring(0, endIndex + 1)) + "ptx";
        final File ptxFile = new File(ptxFileName);
        if (ptxFile.exists()) {
            return ptxFileName;
        }
        final File cuFile = new File(cuFileName);
        if (!cuFile.exists()) {
            throw new IOException("Input file not found: " + cuFileName);
        }
        final String modelString = "-m" + System.getProperty("sun.arch.data.model");
        final String command = "nvcc " + modelString + " -ptx " + cuFile.getPath() + " -o " + ptxFileName;
        System.out.println("Executing\n" + command);
        final Process process = Runtime.getRuntime().exec(command);
        final String errorMessage = new String(toByteArray(process.getErrorStream()));
        final String outputMessage = new String(toByteArray(process.getInputStream()));
        int exitValue = 0;
        try {
            exitValue = process.waitFor();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for nvcc output", e);
        }
        if (exitValue != 0) {
            System.out.println("nvcc process exitValue " + exitValue);
            System.out.println("errorMessage:\n" + errorMessage);
            System.out.println("outputMessage:\n" + outputMessage);
            throw new IOException("Could not create .ptx file: " + errorMessage);
        }
        System.out.println("Finished creating PTX file");
        return ptxFileName;
    }
    
    private static byte[] toByteArray(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[8192];
        while (true) {
            final int read = inputStream.read(buffer);
            if (read == -1) {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }
}
