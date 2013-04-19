package net.sekien.hbt;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 8:04 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTOutputStream implements Closeable {
public static final int MAGIC_HEADER = 0x48425430;
private DataOutputStream os;

public HBTOutputStream(OutputStream os) throws IOException {
	this(os, true);
}

public HBTOutputStream(OutputStream os, boolean gzip) throws IOException {
	if (gzip)
		this.os = new DataOutputStream(new GZIPOutputStream(os));
	else
		this.os = new DataOutputStream(os);
}

public void write(net.sekien.hbt.HBTTag root) throws IOException {
	//os.writeInt(MAGIC_HEADER);
	writeTag(root);
}

private void writeTag(net.sekien.hbt.HBTTag tag) throws IOException {
	Class type = tag.getClass();
	if (type==HBTByte.class) {
		os.writeByte(1);
		os.writeUTF(tag.getName());
		os.writeByte(((HBTByte) tag).getData());
	} else if (type==net.sekien.hbt.HBTShort.class) {
		os.writeByte(2);
		os.writeUTF(tag.getName());
		os.writeShort(((net.sekien.hbt.HBTShort) tag).getData());
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
	} else if (type==net.sekien.hbt.HBTByteArray.class) {
		os.writeByte(7);
		os.writeUTF(tag.getName());
		os.writeInt(((net.sekien.hbt.HBTByteArray) tag).getData().length);
		os.write(((net.sekien.hbt.HBTByteArray) tag).getData());
	} else if (type==net.sekien.hbt.HBTString.class) {
		os.writeByte(8);
		os.writeUTF(tag.getName());
		os.writeUTF(((net.sekien.hbt.HBTString) tag).getData());
	} else if (type==HBTCompound.class) {
		writeCompound((HBTCompound) tag);
	} else if (type==HBTComment.class) {
		os.writeByte(11);
		os.writeUTF(tag.getName());
	} else if (type==HBTFlag.class) {
		os.writeByte(12);
		os.writeUTF(tag.getName());
		os.writeByte(((HBTFlag) tag).getData());
	} else {
		throw new IOException("Unrecognised type "+type);
	}
}

private void writeCompound(HBTCompound compound) throws IOException {
	os.writeByte(10);
	os.writeUTF(compound.getName());
	for (net.sekien.hbt.HBTTag tag : compound) {
		writeTag(tag);
	}
	os.writeByte(0);
}

@Override
public void close() throws IOException {
	os.close();
}
}
