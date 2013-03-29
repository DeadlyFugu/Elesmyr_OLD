package net.halite.hbt;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: matt
 * Date: 29/03/13
 * Time: 8:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class HBTOutputStream implements Closeable {
    public static final int MAGIC_HEADER = 0x48425430;
    private DataOutputStream os;

    public HBTOutputStream(OutputStream os) throws IOException {
        this(os, true);
    }

    public HBTOutputStream(OutputStream os, boolean gzip) throws IOException {
        if (gzip)
            this.os=new DataOutputStream(new GZIPOutputStream(os));
        else
            this.os=new DataOutputStream(os);
    }

    public void write(HBTCompound root) throws IOException {
        os.writeInt(MAGIC_HEADER);
        writeCompound(root);
    }

    private void writeTag(HBTTag tag) throws IOException {
        Class type = tag.getClass();
        if (type==HBTByte.class) {
            os.writeByte(1);
            os.writeUTF(tag.getName());
            os.writeByte(((HBTByte) tag).getData());
        } else if (type==HBTShort.class) {
            os.writeByte(2);
            os.writeUTF(tag.getName());
            os.writeShort(((HBTShort) tag).getData());
        } else if (type==HBTInt.class) {
            os.writeByte(3);
            os.writeUTF(tag.getName());
            os.writeInt(((HBTInt) tag).getData());
        } else if (type==HBTLong.class) {
            os.writeByte(4);
            os.writeUTF(tag.getName());
            os.writeLong(((HBTLong) tag).getData());
        } else if (type==HBTFloat.class) {
            os.writeByte(5);
            os.writeUTF(tag.getName());
            os.writeFloat(((HBTFloat) tag).getData());
        } else if (type==HBTDouble.class) {
            os.writeByte(6);
            os.writeUTF(tag.getName());
            os.writeDouble(((HBTDouble) tag).getData());
        } else if (type==HBTByteArray.class) {
            os.writeByte(7);
            os.writeUTF(tag.getName());
            os.writeInt(((HBTByteArray) tag).getData().length);
            os.write(((HBTByteArray) tag).getData());
        } else if (type==HBTString.class) {
            os.writeByte(8);
            os.writeUTF(tag.getName());
            os.writeUTF(((HBTString) tag).getData());
        } else if (type==HBTCompound.class) {
            writeCompound((HBTCompound) tag);
        } else {
            throw new IOException("Unrecognised type "+type);
        }
    }

    private void writeCompound(HBTCompound compound) throws IOException {
        os.writeByte(10);
        os.writeUTF(compound.getName());
        for (HBTTag tag : compound) {
            writeTag(tag);
        }
        os.writeByte(0);
    }

    @Override
    public void close() throws IOException {
        os.close();
    }
}
