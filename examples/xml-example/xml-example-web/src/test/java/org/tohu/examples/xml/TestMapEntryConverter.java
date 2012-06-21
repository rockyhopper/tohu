package org.tohu.examples.xml;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TestMapEntryConverter {

	@Test
	public void testRead() {
		String xml = "<data>\n  <age>43</age>\n  <name>Fred</name>\n</data>";
		XStream xs = new XStream();
		xs.alias("data", java.util.TreeMap.class);
		xs.registerConverter(new MapEntryConverter());
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("name", "Fred");
		map.put("age", "43");
		assertEquals(map, xs.fromXML(xml));

	}

	@Test
	public void testWrite() {
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("name", "Fred");
		map.put("age", 43);
		XStream xs = new XStream();
		xs.alias("data", java.util.TreeMap.class);
		xs.registerConverter(new MapEntryConverter());
		assertEquals("<data>\n  <age>43</age>\n  <name>Fred</name>\n</data>", xs.toXML(map));
	}

}