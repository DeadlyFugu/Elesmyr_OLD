package net.halite.hbt;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: matt
 * Date: 29/03/13
 * Time: 7:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class HBTInputStream implements Closeable {
    private DataInputStream is;

    public HBTInputStream(InputStream is) throws IOException {
        this(is, true);
    }

    public HBTInputStream(InputStream is, boolean gzip) throws IOException {
        if (gzip)
            this.is=new DataInputStream(new GZIPInputStream(is));
        else
            this.is=new DataInputStream(is);
    }

    public HBTCompound read() throws IOException {
        HBTCompound root;
        int header = is.readInt();
        if (header!=HBTOutputStream.MAGIC_HEADER) {
            throw new IOException("Bad magic header "+Integer.toHexString(header)+", "+Integer.toHexString(HBTOutputStream.MAGIC_HEADER)+" expected");
        }
        try {
            root=(HBTCompound) readTag();
        } catch (EOFException e) {
            throw new IOException("Premature EOF");
        }
        return root;
    }

    private HBTTag readTag() throws IOException {
        byte type = is.readByte();
        if (type==0) return null;
        String name = is.readUTF();
        switch (type) {
            case 1: return new HBTByte(name,is.readByte());
            case 2: return new HBTShort(name,is.readShort());
            case 3: return new HBTInt(name,is.readInt());
            case 4: return new HBTLong(name,is.readLong());
            case 5: return new HBTFloat(name,is.readFloat());
            case 6: return new HBTDouble(name,is.readDouble());
            case 7: return readByteArray(name);
            case 8: return new HBTString(name,is.readUTF());
            case 10: return readCompound(name);
            default: throw new IOException("Unrecognised type "+Integer.toHexString(type));
        }
    }

    private HBTTag readByteArray(String name) throws IOException {
        byte[] data = new byte[is.readInt()];
        is.readFully(data);
        return new HBTByteArray(name,data);
    }

    private HBTCompound readCompound(String name) throws IOException {
        HBTCompound compound = new HBTCompound(name);
        HBTTag tag;
        while ((tag=readTag())!=null) {
            compound.addTag(tag);
        }
        return compound;
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
