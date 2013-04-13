package net.sekien.lote.util;

public enum ResourceType {
	IMAGE("png", "jpg", "bmp", "tga"),
	FONT("ttf", "png", "bmp", "tga"),
	HBT("hbtx", "hbt", "hbtc"),
	PLAIN("", "txt"),
	LIGHTMAP("lm"),
	MAP("tmx");
private String[] exts;

private ResourceType(String... exts) {
	this.exts=exts;
}

public String[] getExtensions() {
	return exts;
}
}