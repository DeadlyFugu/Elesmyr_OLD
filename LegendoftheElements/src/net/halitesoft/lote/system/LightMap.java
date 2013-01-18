package net.halitesoft.lote.system;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import net.halitesoft.lote.world.Region;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.newdawn.slick.Color;

public class LightMap {
	private int vbID,cbID,ibID;
	public int resx,resy;
	public int gw,gh;
	public Color ambLight,ambLightT;
	private ArrayList<Light> lights;
	public LightMap(boolean enabled,int resx, int resy) {
		resx+=2;
		resy+=2;
		this.resx=resx;
		this.resy=resy;
		this.gw=Main.INTERNAL_RESX/(resx-2);
		this.gh=Main.INTERNAL_RESY/(resy-2);
		ambLight = new Color(0,0,0,1);
		ambLightT = new Color(0,0,0,1);
		
		lights = new ArrayList<Light>();
		/*lights.add(new Light(600,550,256,0.5f,0.35f,0.2f,0f)); //RED LIGHT
		lights.add(new Light(900,700,192,0.2f,0.5f,0.3f,0f)); //SMALL LIGHT
		lights.add(new Light(700,1200,512,0.3f,0.2f,0.5f,0f)); //BIG LIGHT
		lights.add(new Light(700,500,256,0.3f,0.4f,0.6f,0f)); //BLUE LIGHT
		lights.add(new Light(800,520,256,0.3f,0.4f,0.6f,0f)); //BLUE LIGHT*/
		
		IntBuffer buffer = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(buffer);
		vbID=buffer.get(0);
		cbID=buffer.get(1);
		ibID=buffer.get(2);

		//Vertex position buffer
		IntBuffer vbuffer = BufferUtils.createIntBuffer(resx*resy*2+2);
		for (int y=0;y<resy;y++)
			for (int x=0;x<resx;x++)
				vbuffer.put(new int[] {x,y});
		vbuffer.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbuffer, GL15.GL_STATIC_DRAW);

		//Vertex color buffer
		FloatBuffer cbuffer = BufferUtils.createFloatBuffer(resx*resy*4);
		for (int y=0;y<resy;y++)
			for (int x=0;x<resx;x++)
				cbuffer.put(new float[] {0,0,0,1});
		cbuffer.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cbID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, cbuffer, GL15.GL_STREAM_DRAW);

		//Index buffer
		IntBuffer ibuffer = BufferUtils.createIntBuffer((resx-1)*(resy-1)*4);
		for (int y=0;y<resy-1;y++)
			for (int x=0;x<resx-1;x++)
				ibuffer.put(new int[] {x+(y*resx),(x+1)+(y*resx),(x+1)+((y+1)*resx),x+((y+1)*resx)});
		ibuffer.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ibID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ibuffer, GL15.GL_STATIC_DRAW);
	}
	
	public void addLight(Light light) {
		this.lights.add(light);
	}
	
	public void addLight(ArrayList<Light> lights) {
		this.lights.addAll(lights);
	}
	
	public void removeLight(Light light) {
		this.lights.remove(light);
	}
	
	public void clearLight() {
		this.lights.clear();
	}

	public void update(Region r, Camera cam, float time) {
		float[][][] col = new float[resx][resy][4];
		float xmul = (float) (resx-2)/(Main.INTERNAL_RESX);
		float ymul = (float) (resy-2)/(Main.INTERNAL_RESY);
		
		//a=a+(b-a)*c
		float timeh = time/60f;
		calcAmbLight(timeh);
		
		ambLight.r+=(ambLightT.r-ambLight.r)/30f;
		ambLight.g+=(ambLightT.g-ambLight.g)/30f;
		ambLight.b+=(ambLightT.b-ambLight.b)/30f;
		ambLight.a+=(ambLightT.a-ambLight.a)/30f;
		//ambLight = ambLightT;
		
		for (int y=0;y<resy;y++)
			for (int x=0;x<resx;x++) {
				//setCol(col,x,y,0,0,0,0); //day;
				//setCol(col,x,y,0.4f,0.2f,0.1f,0.5f); //sunset
				//setCol(col,x,y,0.02f,0.02f,0.05f,0.995f); //night
				setCol(col,x,y,ambLight.r,ambLight.g,ambLight.b,ambLight.a);
			}
		//START LIGHT
		for (Light l : ((ArrayList<Light>) lights.clone())) {
			float xf = (float) ((l.x+(Math.floor((cam.getXOff()-1)/(float) gw)*gw))*xmul)+1;
			float yf = (float) ((l.y+(Math.floor((cam.getYOff()-1)/(float) gh)*gh))*ymul)+1;
			int xs = (int) Math.round(xf);
			int ys = (int) Math.round(yf);
			int dists = (int) (l.dist*xmul);
			for (int cx = xs-dists; cx<xs+dists; cx++) {
				for (int cy = ys-dists; cy<ys+dists; cy++) {
					if (cx>=0&&cx<col.length&&cy>=0&&cy<col[0].length) {
						//LINEAR
						//float atn = 1-(float) (Math.hypot(xs-cx,ys-cy)/dists);
						
						//INVERSE QUADRATRIC
						//float atn = (float) Math.max(0,1-Math.hypot(xf-cx,yf-cy)/dists);
						//atn=atn*atn;
						//REAL
						float att_s = 16;
						float atn = (float) (Math.pow(Math.hypot(xf-cx,yf-cy),2)/(dists*dists));
						atn =  1.f/(atn*att_s+1.f); 
						att_s = 1.f/(att_s+1);
						atn = atn-att_s;
						atn/=1.f-att_s;//*/
						
						mixCol(col,cx,cy,l.r,l.g,l.b,l.a,atn*(1-l.a)*ambLight.a);
					}
				}
			}
		}

		//Vertex color buffer
		if (time!=-1) {
		FloatBuffer cbuffer = BufferUtils.createFloatBuffer(resx*resy*4);
		for (int y=0;y<resy;y++)
			for (int x=0;x<resx;x++) {
				cbuffer.put(col[x][y]);
			}
		cbuffer.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cbID);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, cbuffer);
		}
	}

	private void calcAmbLight(float timeh) {
		if (timeh<5) { //Night
			setAmbCol(0.02f,0.02f,0.05f,0.995f);
		} else if (timeh<5.5f) { //Night to Sunrise
			setAmbColInterp(0.02f,0.02f,0.05f,0.995f,0.4f,0.2f,0.1f,0.5f,(timeh%0.5f)*2);
		} else if (timeh<6) { //Sunrise to day
			setAmbColInterp(0.4f,0.2f,0.1f,0.5f,0,0,0,0,(timeh%0.5f)*2);
		} else if (timeh<18) { //Day
			setAmbCol(0,0,0,0); //(0.197f,0.198f,0.1f,0.1f)-Looks nicer (0.37f,0.38f,0.4f,0.9f)-Fog
		} else if (timeh<18.5f) { //Day to Sunset
			setAmbColInterp(0,0,0,0,0.4f,0.2f,0.1f,0.5f,(timeh%0.5f)*2);
		} else if (timeh<19.5) { //Sunset to Night
			setAmbColInterp(0.4f,0.2f,0.1f,0.5f,0.02f,0.02f,0.05f,0.995f,(timeh-0.5f)%1);
		} else if (timeh<24) { //Night
			setAmbCol(0.02f,0.02f,0.05f,0.995f);
		}
	}

	private void setAmbCol(float r, float g, float b, float a) {
		ambLightT.r=r;
		ambLightT.g=g;
		ambLightT.b=b;
		ambLightT.a=a;
	}
	
	private void setAmbColInterp(float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2, float i) {
		ambLightT.r=r1+(r2-r1)*i;
		ambLightT.g=g1+(g2-g1)*i;
		ambLightT.b=b1+(b2-b1)*i;
		ambLightT.a=a1+(a2-a1)*i;
	}
	
	public void skipFade(float time) {
		calcAmbLight(time);
		ambLight=ambLightT;
	}

	private void setCol(float[][][] col, int x, int y, float r, float g, float b, float a) {
		col[x][y][0]= r;
		col[x][y][1]= g;
		col[x][y][2]= b;
		col[x][y][3]= a;
	}
	
	private void mixCol(float[][][] col, int x, int y, float r, float g, float b, float a, float str) {
		str=Math.max(0, Math.min(1,str));
		col[x][y][0]=col[x][y][0]+Math.max(0,(r-col[x][y][0])*str);
		col[x][y][1]=col[x][y][1]+Math.max(0,(g-col[x][y][1])*str);
		col[x][y][2]=col[x][y][2]+Math.max(0,(b-col[x][y][2])*str);
		col[x][y][3]=col[x][y][3]+Math.min(0,(a-col[x][y][3])*str);
	}

	public void render() {
		//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
	    GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbID);
		GL11.glVertexPointer(2, GL11.GL_INT, 0, 0);

		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cbID);
		GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibID);
		GL12.glDrawRangeElements(GL11.GL_QUADS, 0, (resx-1)*(resy-1)*4, (resx-1)*(resy-1)*4, GL11.GL_UNSIGNED_INT, 0);
	}
}