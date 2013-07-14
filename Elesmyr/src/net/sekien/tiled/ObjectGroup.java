package net.sekien.tiled;

import org.newdawn.slick.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * A group of objects on the map (objects layer)
 *
 * @author liamzebedee
 */
public class ObjectGroup {
/** The index of this group */
public int index;
/** The name of this group - read from the XML */
public String name;
/** The Objects of this group */
public ArrayList<MapObject> objects;
/** The width of this layer */
public int width;
/** The height of this layer */
public int height;
/** The mapping between object names and offsets */
private HashMap<String, Integer> nameToObjectMap = new HashMap<String, Integer>();
/** the properties of this group */
public Properties props;
/** The TiledMap of which this ObjectGroup belongs to */
TiledMap map;
/** The opacity of this layer (range 0 to 1) */
public float opacity = 1;
/** The visibility of this layer */
public boolean visible = true;
/** The color of this layer. NOTE: Slick does not render objects on default */
public Color color;

/**
 * Create a new group based on the XML definition
 *
 * @param element
 * 		The XML element describing the layer
 * @param map
 * 		The map to which the ObjectGroup belongs
 * @throws SlickException
 * 		Indicates a failure to parse the XML group
 */
public ObjectGroup(Element element, TiledMap map) throws SlickException {
	this.map = map;
	TiledMapPlus tmap = null;
	if (map instanceof TiledMapPlus) {
		tmap = (TiledMapPlus) map;
	}
	name = element.getAttribute("name");
	String widthS = element.getAttribute("width");
	if (widthS!=null) {
		width = Integer.parseInt(widthS);
	}
	String heightS = element.getAttribute("height");
	if (widthS!=null) {
		height = Integer.parseInt(heightS);
	}

	objects = new ArrayList<MapObject>();
	String opacityS = element.getAttribute("opacity");
	if (!opacityS.equals("")) {
		opacity = Float.parseFloat(opacityS);
	}
	if (element.getAttribute("visible").equals("0")) {
		visible = false;
	}

	String colorS = element.getAttribute("color");
	if (colorS!=null && colorS.length()!=0) {
		color = Color.decode(colorS);
	}

	// now read the layer properties
	Element propsElement = (Element) element.getElementsByTagName(
			                                                             "properties").item(0);
	if (propsElement!=null) {
		NodeList properties = propsElement.getElementsByTagName("property");
		if (properties!=null) {
			props = new Properties();
			for (int p = 0; p < properties.getLength(); p++) {
				Element propElement = (Element) properties.item(p);
				String name = propElement.getAttribute("name");
				String value = propElement.getAttribute("value");
				props.setProperty(name, value);
			}
		}
	}

	NodeList objectNodes = element.getElementsByTagName("object");
	for (int i = 0; i < objectNodes.getLength(); i++) {
		Element objElement = (Element) objectNodes.item(i);
		MapObject object = null;
		if (tmap!=null) {
			object = new MapObject(objElement, tmap);
		} else {
			object = new MapObject(objElement);
		}
		object.index = i;
		objects.add(object);
	}
}

/**
 * Gets an object by its name
 *
 * @param objectName
 * 		The name of the object
 */
public MapObject getObject(String objectName) {
	MapObject g = this.objects.get(this.nameToObjectMap.get(objectName));
	return g;
}

/**
 * Gets all objects of a specific type on a layer
 *
 * @param type
 * 		The name of the type
 */
public ArrayList<MapObject> getObjectsOfType(String type) {
	ArrayList<MapObject> foundObjects = new ArrayList<MapObject>();
	for (MapObject object : this.objects) {
		if (object.type.equals(type)) {
			foundObjects.add(object);
		}
	}
	return foundObjects;
}

/**
 * Removes an object
 *
 * @param objectName
 * 		The name of the object
 */
public void removeObject(String objectName) {
	int objectOffset = this.nameToObjectMap.get(objectName);
	MapObject object = this.objects.remove(objectOffset);
}

/**
 * Sets the mapping from object names to their offsets
 *
 * @param map
 * 		The name of the map
 */
public void setObjectNameMapping(HashMap<String, Integer> map) {
	this.nameToObjectMap = map;
}

/**
 * Adds an object to the object group
 *
 * @param object
 * 		The object to be added
 */
public void addObject(MapObject object) {
	this.objects.add(object);
	this.nameToObjectMap.put(object.name, this.objects.size());
}

/** Gets all the objects from this group */
public ArrayList<MapObject> getObjects() {
	return this.objects;
}
}
