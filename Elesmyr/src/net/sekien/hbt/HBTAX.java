/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.hbt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA. User: matt Date: 7/09/13 Time: 12:32 PM To change this template use File | Settings |
 * File Templates.
 */
public class HBTAX {
public static HBTCompound getTags(String s, String rootname) {
	if (!s.endsWith(";") && !s.endsWith(",") && !s.endsWith("}"))
		s = s.concat(";");
	StringBuilder sb = new StringBuilder();
	HBTCompound out = new HBTCompound(rootname);
	boolean instr = false;
	int nestlvl = 0;
	String compoundname = null;
	for (char c : s.toCharArray()) {
		if (c=='"') {
			instr = !instr;
		} else if (instr) {
			sb.append(c);
		} else if (c=='{') {
			if (nestlvl==0) {
				compoundname = sb.toString();
				sb.setLength(0);
			} else sb.append(c);
			nestlvl++;
		} else if (c=='}') {
			nestlvl--;
			if (nestlvl==0) {
				out.addTag(getTags(sb.toString(), compoundname));
				sb.setLength(0);
			} else sb.append(c);
		} else if (nestlvl==0) {
			if (c==',' || c==';') {
				String tag = sb.toString();
				sb.setLength(0);
				if (tag.contains(":")) {
					String[] parts = tag.split(":", 2);
					if (parts[1].endsWith("\"")) {
						if (!parts[1].startsWith("\"")) {
							System.err.println("HBTAX: "+parts[1]+" is not a valid string");
						} else {
							out.addTag(new HBTString(parts[0], parts[1].substring(1, parts[1].length()-1)));
						}
					} else if (parts[1].matches("[0-9]+")) {
						out.addTag(new HBTInt(parts[0], Integer.parseInt(parts[1])));
					} else if (parts[1].matches("[0-9]+\\.[0-9]+")) {
						out.addTag(new HBTFloat(parts[0], Float.parseFloat(parts[1])));
					} else if (parts[1].matches("[0-9]+\\.[0-9]+d")) {
						out.addTag(new HBTDouble(parts[0], Double.parseDouble(parts[1].substring(0, parts[1].length()-1))));
					} else if (parts[1].matches("[0-9]+s")) {
						out.addTag(new HBTShort(parts[0], Short.parseShort(parts[1].substring(0, parts[1].length()-1))));
					} else if (parts[1].matches("[0-9]+b")) {
						out.addTag(new HBTByte(parts[0], Byte.parseByte(parts[1].substring(0, parts[1].length()-1))));
					} else if (parts[1].matches("[0-9]+l")) {
						out.addTag(new HBTLong(parts[0], Long.parseLong(parts[1].substring(0, parts[1].length()-1))));
					} else if (HBTFlag.isValid(parts[1])) {
						out.addTag(new HBTFlag(parts[0], parts[1]));
					} else if (parts[1].startsWith("0x")) {
						out.addTag(new HBTByteArray(parts[0], parseByteArray(parts[1].substring(2))));
					} else {
						out.addTag(new HBTString(parts[0], parts[1]));
					}
				} else {
					System.err.println("HBTAX: Tag "+tag+" does not have a value");
				}
			} else {
				sb.append(c);
			}
		} else {
			sb.append(c);
		}
	}
	return out;
}

private static byte[] parseByteArray(String str) {
	ArrayList<String> parts = new ArrayList<String>();
	Character p = null;
	for (char c : str.toCharArray()) {
		if (p!=null) {
			parts.add(""+p+""+c);
			p = null;
		} else {
			p = c;
		}
	}
	byte[] ret = new byte[parts.size()];
	for (int i = 0; i < parts.size(); i++) {
		ret[i] = (byte) Short.parseShort(parts.get(i), 16);
	}
	return ret;
}

public static void main(String args[]) {
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	while (true) {
		String s = null;
		try {
			s = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			break;
		}
		if (s.equals("exit")) break;
		System.out.println(getTags(s, "root"));
	}
}
}
