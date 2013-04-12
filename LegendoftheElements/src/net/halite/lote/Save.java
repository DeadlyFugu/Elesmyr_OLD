package net.halite.lote;

import net.halite.hbt.*;
import net.halite.lote.system.Globals;
import net.halite.lote.system.Main;
import net.halite.lote.util.FileHandler;
import net.halite.lote.world.Region;
import net.halite.lote.world.World;
import net.halite.lote.world.entity.Entity;
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
HBTCompound data;

public Save(String name) {
	this.name=name;

	data= new HBTCompound("saveroot");
	try {
		for (HBTTag tag : FileHandler.readHBT("save/"+name,false)) {
			data.addTag(tag);
		}
	} catch (IOException e) {
		e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	}
}

public HBTTag getTag(String name) throws HBTCompound.TagNotFoundException {
	return data.getTag(name);
}

public HBTCompound getCompound(String name) {
	try {return (HBTCompound) getTag(name);}
	catch (ClassCastException e) {Log.warn("tag:"+name, e); return new HBTCompound(name);}
	catch (HBTCompound.TagNotFoundException e) {return new HBTCompound(name);}
}

public byte getByte(String name, byte def) {
	try {return ((HBTByte) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public short getShort(String name, short def) {
	try {return ((HBTShort) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public int getInt(String name, int def) {
	try {return ((HBTInt) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public long getLong(String name, long def) {
	try {return ((HBTLong) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public float getFloat(String name, float def) {
	try {return ((HBTFloat) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public double getDouble(String name, double def) {
	try {return ((HBTDouble) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public String getString(String name, String def) {
	try {return ((HBTString) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public byte[] getByteArray(String name, byte[] def) {
	try {return ((HBTByteArray) getTag(name)).getData();}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return def;}
	catch (HBTCompound.TagNotFoundException e) {return def;}
}

public HBTFlag getFlag(String name, String def) {
	try {return (HBTFlag) getTag(name);}
	catch (ClassCastException e) {Log.warn("tag:"+name,e); return new HBTFlag(name, def);}
	catch (HBTCompound.TagNotFoundException e) {return new HBTFlag(name, def);}
}

public void putTag(String name, HBTTag tag) throws HBTCompound.TagNotFoundException {
	data.setTag(name, tag);
}

public void putByte(String name, byte data) {
	putTag(name, new HBTByte(name.substring(name.lastIndexOf('.')+1), data));
}

public void putShort(String name, short data) {
	putTag(name,new HBTShort(name.substring(name.lastIndexOf('.')+1),data));
}

public void putInt(String name, int data) {
	putTag(name,new HBTInt(name.substring(name.lastIndexOf('.')+1),data));
}

public void putLong(String name, long data) {
	putTag(name,new HBTLong(name.substring(name.lastIndexOf('.')+1),data));
}

public void putFloat(String name, float data) {
	putTag(name,new HBTFloat(name.substring(name.lastIndexOf('.')+1),data));
}

public void putDouble(String name, double data) {
	putTag(name,new HBTDouble(name.substring(name.lastIndexOf('.')+1),data));
}

public void putByteArray(String name, byte[] data) {
	putTag(name,new HBTByteArray(name.substring(name.lastIndexOf('.')+1),data));
}

public void putString(String name, String data) {
	putTag(name,new HBTString(name.substring(name.lastIndexOf('.')+1),data));
}


public void putCompound(String name, HBTCompound data) {
	putTag(name,data);
}

public void write() {
	putLong("meta.savedate", new Date().getTime());
	try {
		HBTOutputStream os = new HBTOutputStream(new FileOutputStream("save/"+name+".hbt"),false);
		for (HBTTag tag : data)
			os.write(tag);
		os.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	saveScreen();
	Globals.set("lastSave",name);
	Globals.save();
}

public void putPlayer(String name, String data, World world) {
	if (data==null)
		return;
	Entity player=world.getRegion(data.split("\\.")[0]).entities.get(Integer.parseInt(data.split("\\.", 2)[1])); //Can sometimes cause null pointer.
	//put("players."+name, data.split("\\.")[0]+","+player.x+","+player.y);
	HBTCompound pTag = new HBTCompound(name);
	pTag.addTag(new HBTString("str",data.split("\\.")[0]+","+player.x+","+player.y)); //TODO: Proper player to hbt
	putTag("players."+name,pTag);
}

public void putPlayer(String name, String data, Region region) {
	Entity player=region.entities.get(Integer.parseInt(data.split("\\.", 2)[1]));
	//put("players."+name, data.split("\\.")[0]+","+player.x+","+player.y);
	HBTCompound pTag = new HBTCompound(name);
	pTag.addTag(new HBTString("str",data.split("\\.")[0]+","+player.x+","+player.y)); //TODO: Proper player to hbt
	putTag("players."+name,pTag);
}

public void clearTag(String world) {
	HBTCompound tag =data;
	while (world.contains(".")) {
		tag=tag.getCompound(world.split("\\.",2)[0]);
		world = world.split("\\.",2)[1];
	}
	HBTTag toDel=null;
	for (HBTTag tag1 : tag) {
		if (tag1.getName().equals(world)) toDel=tag;
	}
	tag.getData().remove(toDel);
	if (toDel==null)
		Log.warn("Finding tag "+world+" failed.");
}

public void saveScreen() {
	GL11.glReadBuffer(GL11.GL_FRONT);
	int width = Display.getDisplayMode().getWidth();
	int height= Display.getDisplayMode().getHeight();
	int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
	ByteBuffer buffer = BufferUtils.createByteBuffer(width*height*bpp);
	GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
	final int scale = height/Main.INTERNAL_RESY;
	BufferedImage image = new BufferedImage(width/scale, height/scale, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < width/scale; x++)
			for(int y = 0; y < height/scale; y++)
			{
				int i = ((x*scale) + (width * (y*scale))) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, (height/scale) - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
	try {
		File file = new File("save/thumb/"+name+".png");
		file.createNewFile();
		ImageIO.write(image, "PNG", file);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
}
