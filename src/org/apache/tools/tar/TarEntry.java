// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.tools.tar;

import java.util.Locale;
import java.util.Date;
import java.io.File;

public class TarEntry implements TarConstants
{
    private StringBuffer name;
    private int mode;
    private int userId;
    private int groupId;
    private long size;
    private long modTime;
    private int checkSum;
    private byte linkFlag;
    private StringBuffer linkName;
    private StringBuffer magic;
    private StringBuffer userName;
    private StringBuffer groupName;
    private int devMajor;
    private int devMinor;
    private File file;
    public static final int MAX_NAMELEN = 31;
    public static final int DEFAULT_DIR_MODE = 16877;
    public static final int DEFAULT_FILE_MODE = 33188;
    public static final int MILLIS_PER_SECOND = 1000;
    
    private TarEntry() {
        this.magic = new StringBuffer("ustar");
        this.name = new StringBuffer();
        this.linkName = new StringBuffer();
        String s = System.getProperty("user.name", "");
        if (s.length() > 31) {
            s = s.substring(0, 31);
        }
        this.userId = 0;
        this.groupId = 0;
        this.userName = new StringBuffer(s);
        this.groupName = new StringBuffer("");
        this.file = null;
    }
    
    public TarEntry(final String s) {
        this();
        final boolean endsWith = s.endsWith("/");
        this.devMajor = 0;
        this.devMinor = 0;
        this.name = new StringBuffer(s);
        this.mode = (endsWith ? 16877 : 33188);
        this.linkFlag = (byte)(endsWith ? 53 : 48);
        this.userId = 0;
        this.groupId = 0;
        this.size = 0L;
        this.checkSum = 0;
        this.modTime = new Date().getTime() / 1000L;
        this.linkName = new StringBuffer("");
        this.userName = new StringBuffer("");
        this.groupName = new StringBuffer("");
        this.devMajor = 0;
        this.devMinor = 0;
    }
    
    public TarEntry(final String s, final byte linkFlag) {
        this(s);
        this.linkFlag = linkFlag;
    }
    
    public TarEntry(final String s, final String s2) {
        this(s);
        this.linkFlag = 50;
        this.linkName = new StringBuffer(s2);
        this.mode = 41471;
    }
    
    public TarEntry(final File file) {
        this();
        this.file = file;
        String s = file.getPath();
        final String lowerCase = System.getProperty("os.name").toLowerCase(Locale.US);
        if (lowerCase != null) {
            if (lowerCase.startsWith("windows")) {
                if (s.length() > 2) {
                    final char char1 = s.charAt(0);
                    if (s.charAt(1) == ':' && ((char1 >= 'a' && char1 <= 'z') || (char1 >= 'A' && char1 <= 'Z'))) {
                        s = s.substring(2);
                    }
                }
            }
            else if (lowerCase.indexOf("netware") > -1) {
                final int index = s.indexOf(58);
                if (index != -1) {
                    s = s.substring(index + 1);
                }
            }
        }
        String s2;
        for (s2 = s.replace(File.separatorChar, '/'); s2.startsWith("/"); s2 = s2.substring(1)) {}
        this.linkName = new StringBuffer("");
        this.name = new StringBuffer(s2);
        if (file.isDirectory()) {
            this.mode = 16877;
            this.linkFlag = 53;
            if (this.name.charAt(this.name.length() - 1) != '/') {
                this.name.append("/");
            }
            this.size = 0L;
        }
        else {
            this.mode = 33188;
            this.linkFlag = 48;
            this.size = file.length();
        }
        this.modTime = file.lastModified() / 1000L;
        this.checkSum = 0;
        this.devMajor = 0;
        this.devMinor = 0;
    }
    
    public TarEntry(final byte[] array) {
        this();
        this.parseTarHeader(array);
    }
    
    public boolean equals(final TarEntry tarEntry) {
        return this.getName().equals(tarEntry.getName());
    }
    
    public boolean equals(final Object o) {
        return o != null && this.getClass() == o.getClass() && this.equals((TarEntry)o);
    }
    
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    public boolean isDescendent(final TarEntry tarEntry) {
        return tarEntry.getName().startsWith(this.getName());
    }
    
    public String getName() {
        return this.name.toString();
    }
    
    public void setName(final String s) {
        this.name = new StringBuffer(s);
    }
    
    public void setMode(final int mode) {
        this.mode = mode;
    }
    
    public String getLinkName() {
        return this.linkName.toString();
    }
    
    public int getUserId() {
        return this.userId;
    }
    
    public void setUserId(final int userId) {
        this.userId = userId;
    }
    
    public int getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(final int groupId) {
        this.groupId = groupId;
    }
    
    public String getUserName() {
        return this.userName.toString();
    }
    
    public void setUserName(final String s) {
        this.userName = new StringBuffer(s);
    }
    
    public String getGroupName() {
        return this.groupName.toString();
    }
    
    public void setGroupName(final String s) {
        this.groupName = new StringBuffer(s);
    }
    
    public void setIds(final int userId, final int groupId) {
        this.setUserId(userId);
        this.setGroupId(groupId);
    }
    
    public void setNames(final String userName, final String groupName) {
        this.setUserName(userName);
        this.setGroupName(groupName);
    }
    
    public void setModTime(final long n) {
        this.modTime = n / 1000L;
    }
    
    public void setModTime(final Date date) {
        this.modTime = date.getTime() / 1000L;
    }
    
    public Date getModTime() {
        return new Date(this.modTime * 1000L);
    }
    
    public File getFile() {
        return this.file;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public long getSize() {
        return this.size;
    }
    
    public void setSize(final long size) {
        this.size = size;
    }
    
    public boolean isSymlink() {
        return this.linkFlag == 50;
    }
    
    public boolean isGNULongNameEntry() {
        return this.linkFlag == 76 && this.name.toString().equals("././@LongLink");
    }
    
    public boolean isDirectory() {
        if (this.file != null) {
            return this.file.isDirectory();
        }
        return this.linkFlag == 53 || this.getName().endsWith("/");
    }
    
    public TarEntry[] getDirectoryEntries() {
        if (this.file == null || !this.file.isDirectory()) {
            return new TarEntry[0];
        }
        final String[] list = this.file.list();
        final TarEntry[] array = new TarEntry[list.length];
        for (int i = 0; i < list.length; ++i) {
            array[i] = new TarEntry(new File(this.file, list[i]));
        }
        return array;
    }
    
    public void writeEntryHeader(final byte[] array) {
        final int longOctalBytes;
        int n = longOctalBytes = TarUtils.getLongOctalBytes(this.modTime, array, TarUtils.getLongOctalBytes(this.size, array, TarUtils.getOctalBytes(this.groupId, array, TarUtils.getOctalBytes(this.userId, array, TarUtils.getOctalBytes(this.mode, array, TarUtils.getNameBytes(this.name, array, 0, 100), 8), 8), 8), 12), 12);
        for (int i = 0; i < 8; ++i) {
            array[n++] = 32;
        }
        array[n++] = this.linkFlag;
        for (int j = TarUtils.getOctalBytes(this.devMinor, array, TarUtils.getOctalBytes(this.devMajor, array, TarUtils.getNameBytes(this.groupName, array, TarUtils.getNameBytes(this.userName, array, TarUtils.getNameBytes(this.magic, array, TarUtils.getNameBytes(this.linkName, array, n, 100), 8), 32), 32), 8), 8); j < array.length; array[j++] = 0) {}
        TarUtils.getCheckSumOctalBytes(TarUtils.computeCheckSum(array), array, longOctalBytes, 8);
    }
    
    public void parseTarHeader(final byte[] array) {
        int n = 0;
        this.name = TarUtils.parseName(array, n, 100);
        n += 100;
        this.mode = (int)TarUtils.parseOctal(array, n, 8);
        n += 8;
        this.userId = (int)TarUtils.parseOctal(array, n, 8);
        n += 8;
        this.groupId = (int)TarUtils.parseOctal(array, n, 8);
        n += 8;
        this.size = TarUtils.parseOctal(array, n, 12);
        n += 12;
        this.modTime = TarUtils.parseOctal(array, n, 12);
        n += 12;
        this.checkSum = (int)TarUtils.parseOctal(array, n, 8);
        n += 8;
        this.linkFlag = array[n++];
        this.linkName = TarUtils.parseName(array, n, 100);
        n += 100;
        this.magic = TarUtils.parseName(array, n, 8);
        n += 8;
        this.userName = TarUtils.parseName(array, n, 32);
        n += 32;
        this.groupName = TarUtils.parseName(array, n, 32);
        n += 32;
        this.devMajor = (int)TarUtils.parseOctal(array, n, 8);
        n += 8;
        this.devMinor = (int)TarUtils.parseOctal(array, n, 8);
    }
}
