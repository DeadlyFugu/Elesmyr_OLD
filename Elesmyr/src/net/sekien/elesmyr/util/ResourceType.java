/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.sekien.elesmyr.util;

public enum ResourceType {
	IMAGE("png", "jpg", "bmp", "tga"),
	FONT("ttf", "png", "bmp", "tga"),
	HBT("hbtx", "hbt", "hbtc"),
	PLAIN("", "txt"),
	LIGHTMAP("lm"),
	MAP("tmx"),
	SFX("wav"),
	MUSIC("ogg");
private String[] exts;

private ResourceType(String... exts) {
	this.exts = exts;
}

public String[] getExtensions() {
	return exts;
}
}