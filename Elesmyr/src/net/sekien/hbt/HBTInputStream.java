package net.sekien.hbt;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA. User: matt Date: 29/03/13 Time: 7:52 AM To change this template use File | Settings |
 * File Templates.
 */
public class HBTInputStream implements Closeable {
private DataInputStream is;

public HBTInputStream(InputStream is) throws IOException {
	this(is, true);
}

public HBTInputStream(InputStream is, boolean gzip) throws IOException {
	if (gzip)
		this.is = new DataInputStream(new GZIPInputStream(is));
	else
		this.is = new DataInputStream(is);
}

public net.sekien.hbt.HBTTag read() throws IOException {
        /*int header = is.readInt();
        if (header!=HBTOutputStream.MAGIC_HEADER) {
            throw new IOException("Bad magic header "+Integer.toHexString(header)+", "+Integer.toHexString(HBTOutputStream.MAGIC_HEADER)+" expected");
        }*/
	return readTag();
}

private net.sekien.hbt.HBTTag readTag() throws IOException {
	byte type;
	try {
		type = is.readByte();
	} catch (EOFException e) {
		return null;
	}
	if (type==0) return null;
	String name = is.readUTF();
	switch (type) {
		case 1:
			return new HBTByte(name, is.readByte());
		case 2:
			return new net.sekien.hbt.HBTShort(name, is.readShort());
		case 3:
			return new HBTInt(name, is.readInt());
		case 4:
			return new HBTLong(name, is.readLong());
		case 5:
			return new HBTFloat(name, is.readFloat());
		case 6:
			return new HBTDouble(name, is.readDouble());
		case 7:
			return readByteArray(name);
		case 8:
			return new net.sekien.hbt.HBTString(name, is.readUTF());
		case 10:
			return readCompound(name);
		case 11:
			return new HBTComment(name);
		case 12:
			return new HBTFlag(name, is.readByte());
		default:
			throw new IOException("Unrecognised type "+Integer.toHexString(type));
	}
}

private net.sekien.hbt.HBTTag readByteArray(String name) throws IOException {
	byte[] data = new byte[is.readInt()];
	is.readFully(data);
	return new net.sekien.hbt.HBTByteArray(name, data);
}

private HBTCompound readCompound(String name) throws IOException {
	HBTCompound compound = new HBTCompound(name);
	net.sekien.hbt.HBTTag tag;
	while ((tag = readTag())!=null) {
		compound.addTag(tag);
	}
	return compound;
}

@Override
public void close() throws IOException {
	is.close();
}
}
