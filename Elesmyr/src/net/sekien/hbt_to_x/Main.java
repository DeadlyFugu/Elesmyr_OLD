package net.sekien.hbt_to_x;

import net.sekien.elesmyr.util.FileHandler;
import net.sekien.hbt.HBTInputStream;
import net.sekien.hbt.HBTOutputStream;
import net.sekien.hbt.HBTTag;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
public static void main(String[] arg) {
	JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	if (fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
		try {
			HBTInputStream in = new HBTInputStream(new FileInputStream(fileChooser.getSelectedFile()), false);
			List<HBTTag> tags = new ArrayList<HBTTag>();
			try {
				while (true) {
					HBTTag tag = in.read();
					if (tag!=null)
						tags.add(tag);
					else
						throw new IOException();
				}
			} catch (IOException ignored) {}
			in.close();

			File tmp = File.createTempFile(fileChooser.getSelectedFile().getName().replaceAll("\\s", ""), ".hbtx");
			BufferedWriter tmpout = new BufferedWriter(new FileWriter(tmp));
			for (HBTTag tag : tags)
				tmpout.write(tag.toString()+"\n");
			tmpout.flush();
			tmpout.close();

			Process p = Runtime.getRuntime().exec("gedit -s "+tmp);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			BufferedReader tmpin = new BufferedReader(new FileReader(tmp));
			StringBuilder buf = new StringBuilder();
			String line;
			while ((line = tmpin.readLine())!=null) {
				buf.append(line+"\n");
			}
			tmpin.close();

			tags = FileHandler.parseTextHBT(buf.toString());
			HBTOutputStream out = new HBTOutputStream(new FileOutputStream(fileChooser.getSelectedFile()), false);
			for (HBTTag tag : tags) {
				out.write(tag);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
}
