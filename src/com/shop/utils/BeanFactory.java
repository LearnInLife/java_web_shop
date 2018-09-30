package com.shop.utils;

import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class BeanFactory {

	public static Object getBean(String id) {
		try {
			SAXReader reader = new SAXReader();
			String path = BeanFactory.class.getClassLoader().getResource("bean.xml").getPath();
			Document document = reader.read(path);
			Element el = (Element) document.selectSingleNode("//bean[@id='"+id+"']");
			System.out.println(el);
			
			String className = el.attributeValue("class");
			Class<?> clazz = Class.forName(className);
			Object object = clazz.newInstance();
			return object;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
