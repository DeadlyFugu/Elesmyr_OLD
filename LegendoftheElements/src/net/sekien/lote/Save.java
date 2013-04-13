package net.sekien.lote;

import net.sekien.hbt.HBTFlag;
import net.sekien.lote.system.Globals;
import net.sekien.lote.system.Main;
import net.sekien.lote.util.FileHandler;
import net.sekien.lote.world.Region;
import net.sekien.lote.world.World;
import net.sekien.lote.world.entity.Entity;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class Save {

String name;
net.sekien.hbt.HBTCompound data;

public Save(String name) {
	this.name=name;

	data=new net.sekien.hbt.HBTCompound("saveroot");
	try {
		for (net.sekien.hbt.HBTTag tag : FileHandler.readHBT("save/"+name, false)) {
			data.addTag(tag);
		}
	} catch (IOException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
}

public net.sekien.hbt.HBTTag getTag(String name) throws net.sekien.hbt.HBTCompound.TagNotFoundException {
	return data.getTag(name);
}

public net.sekien.hbt.HBTCompound getCompound(String name) {
	try {return (net.sekien.hbt.HBTCompound) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new net.sekien.hbt.HBTCompound(name);
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return new net.sekien.hbt.HBTCompound(name);}
}

public byte getByte(String name, byte def) {
	try {return ((net.sekien.hbt.HBTByte) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public short getShort(String name, short def) {
	try {return ((net.sekien.hbt.HBTShort) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public int getInt(String name, int def) {
	try {return ((net.sekien.hbt.HBTInt) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public long getLong(String name, long def) {
	try {return ((net.sekien.hbt.HBTLong) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public float getFloat(String name, float def) {
	try {return ((net.sekien.hbt.HBTFloat) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public double getDouble(String name, double def) {
	try {return ((net.sekien.hbt.HBTDouble) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public String getString(String name, String def) {
	try {return ((net.sekien.hbt.HBTString) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public byte[] getByteArray(String name, byte[] def) {
	try {return ((net.sekien.hbt.HBTByteArray) getTag(name)).getData();} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return def;
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return def;}
}

public HBTFlag getFlag(String name, String def) {
	try {return (HBTFlag) getTag(name);} catch (ClassCastException e) {
		Log.warn("tag:"+name, e);
		return new HBTFlag(name, def);
	} catch (net.sekien.hbt.HBTCompound.TagNotFoundException e) {return new HBTFlag(name, def);}
}

public void putTag(String name, net.sekien.hbt.HBTTag tag) throws net.sekien.hbt.HBTCompound.TagNotFoundException {
	data.setTag(name, tag);
}

public void putByte(String name, byte data) {
	putTag(name, new net.sekien.hbt.HBTByte(name.substring(name.lastIndexOf('.')+1), data));
}

public void putShort(String name, short data) {
	putTag(name, new net.sekien.hbt.HBTShort(name.substring(name.lastIndexOf('.')+1), data));
}

public void putInt(String name, int data) {
	putTag(name, new net.sekien.hbt.HBTInt(name.substring(name.lastIndexOf('.')+1), data));
}

public void putLong(String name, long data) {
	putTag(name, new net.sekien.hbt.HBTLong(name.substring(name.lastIndexOf('.')+1), data));
}

public void putFloat(String name, float data) {
	putTag(name, new net.sekien.hbt.HBTFloat(name.substring(name.lastIndexOf('.')+1), data));
}

public void putDouble(String name, double data) {
	putTag(name, new net.sekien.hbt.HBTDouble(name.substring(name.lastIndexOf('.')+1), data));
}

public void putByteArray(String name, byte[] data) {
	putTag(name, new net.sekien.hbt.HBTByteArray(name.substring(name.lastIndexOf('.')+1), data));
}

public void putString(String name, String data) {
	putTag(name, new net.sekien.hbt.HBTString(name.substring(name.lastIndexOf('.')+1), data));
}

public void putCompound(String name, net.sekien.hbt.HBTCompound data) {
	putTag(name, data);
}

public void write() {
	putLong("meta.savedate", new Date().getTime());
	try {
		net.sekien.hbt.HBTOutputStream os=new net.sekien.hbt.HBTOutputStream(new FileOutputStream("save/"+name+".hbt"), false);
		for (net.sekien.hbt.HBTTag tag : data)
			os.write(tag);
		os.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	System.out.println(data);
	saveScreen();
	Globals.set("lastSave", name);
	Globals.save();
}

public void putPlayer(String name, String data, World world) {
	if (data==null)
		return;
	Entity player=world.getRegion(data.split("\\.")[0]).entities.get(Integer.parseInt(data.split("\\.", 2)[1])); //Can sometimes cause null pointer.
	//put("players."+name, data.split("\\.")[0]+","+player.x+","+player.y);
	net.sekien.hbt.HBTCompound pTag=new net.sekien.hbt.HBTCompound(name);
	pTag.addTag(new net.sekien.hbt.HBTString("region", data.split("\\.")[0])); //TODO: Proper player to hbt
	pTag.addTag(new net.sekien.hbt.HBTInt("x", player.x));
	pTag.addTag(new net.sekien.hbt.HBTInt("y", player.y));
	putTag("players."+name, pTag);
}

public void putPlayer(String name, String data, Region region) {
	Entity player=region.entities.get(Integer.parseInt(data.split("\\.", 2)[1]));
	//put("players."+name, data.split("\\.")[0]+","+player.x+","+player.y);
	net.sekien.hbt.HBTCompound pTag=new net.sekien.hbt.HBTCompound(name);
	pTag.addTag(new net.sekien.hbt.HBTString("region", data.split("\\.")[0])); //TODO: Proper player to hbt
	pTag.addTag(new net.sekien.hbt.HBTInt("x", player.x));
	pTag.addTag(new net.sekien.hbt.HBTInt("y", player.y));
	putTag("players."+name, pTag);
}

public void clearTag(String world) {
	net.sekien.hbt.HBTCompound tag=data;
	while (world.contains(".")) {
		tag=tag.getCompound(world.split("\\.", 2)[0]);
		world=world.split("\\.", 2)[1];
	}
	net.sekien.hbt.HBTTag toDel=null;
	for (net.sekien.hbt.HBTTag tag1 : tag) {
		if (tag1.getName().equals(world)) toDel=tag;
	}
	tag.getData().remove(toDel);
	if (toDel==null)
		Log.warn("Finding tag "+world+" failed.");
}

public void saveScreen() {
	GL11.glReadBuffer(GL11.GL_FRONT);
	int width=Display.getDisplayMode().getWidth();
	int height=Display.getDisplayMode().getHeight();
	int bpp=4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
	ByteBuffer buffer=BufferUtils.createByteBuffer(width*height*bpp);
	GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	final int scale=height/Main.INTERNAL_RESY;
	BufferedImage image=new BufferedImage(width/scale, height/scale, BufferedImage.TYPE_INT_RGB);
	for (int x=0; x<width/scale; x++)
		for (int y=0; y<height/scale; y++) {
			int i=((x*scale)+(width*(y*scale)))*bpp;
			int r=buffer.get(i)&0xFF;
			int g=buffer.get(i+1)&0xFF;
			int b=buffer.get(i+2)&0xFF;
			image.setRGB(x, (height/scale)-(y+1), (0xFF<<24)|(r<<16)|(g<<8)|b);
		}
	try {
		File file=new File("save/thumb/"+name+".png");
		file.createNewFile();
		ImageIO.write(image, "PNG", file);
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}
