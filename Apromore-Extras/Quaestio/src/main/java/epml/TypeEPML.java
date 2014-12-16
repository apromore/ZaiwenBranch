/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.15 at 11:16:00 AM EST 
//

package epml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for typeEPML complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="typeEPML">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.epml.de}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="graphicsDefault" type="{http://www.epml.de}typeGraphicsDefault" minOccurs="0"/>
 *         &lt;element name="coordinates" type="{http://www.epml.de}typeCoordinates"/>
 *         &lt;element name="definitions" type="{http://www.epml.de}typeDefinitions" minOccurs="0"/>
 *         &lt;element name="attributeTypes" type="{http://www.epml.de}typeAttrTypes" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="directory" type="{http://www.epml.de}typeDirectory" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEPML", propOrder = { "graphicsDefault", "coordinates",
		"definitions", "attributeTypes", "directory" })
public class TypeEPML extends TExtensibleElements {

	protected TypeGraphicsDefault graphicsDefault;
	@XmlElement(required = true)
	protected TypeCoordinates coordinates;
	protected TypeDefinitions definitions;
	protected List<TypeAttrTypes> attributeTypes;
	@XmlElement(required = true)
	protected List<TypeDirectory> directory;

	/**
	 * Gets the value of the graphicsDefault property.
	 * 
	 * @return possible object is {@link TypeGraphicsDefault }
	 * 
	 */
	public TypeGraphicsDefault getGraphicsDefault() {
		return graphicsDefault;
	}

	/**
	 * Sets the value of the graphicsDefault property.
	 * 
	 * @param value
	 *            allowed object is {@link TypeGraphicsDefault }
	 * 
	 */
	public void setGraphicsDefault(TypeGraphicsDefault value) {
		this.graphicsDefault = value;
	}

	/**
	 * Gets the value of the coordinates property.
	 * 
	 * @return possible object is {@link TypeCoordinates }
	 * 
	 */
	public TypeCoordinates getCoordinates() {
		return coordinates;
	}

	/**
	 * Sets the value of the coordinates property.
	 * 
	 * @param value
	 *            allowed object is {@link TypeCoordinates }
	 * 
	 */
	public void setCoordinates(TypeCoordinates value) {
		this.coordinates = value;
	}

	/**
	 * Gets the value of the definitions property.
	 * 
	 * @return possible object is {@link TypeDefinitions }
	 * 
	 */
	public TypeDefinitions getDefinitions() {
		return definitions;
	}

	/**
	 * Sets the value of the definitions property.
	 * 
	 * @param value
	 *            allowed object is {@link TypeDefinitions }
	 * 
	 */
	public void setDefinitions(TypeDefinitions value) {
		this.definitions = value;
	}

	/**
	 * Gets the value of the attributeTypes property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the attributeTypes property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAttributeTypes().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TypeAttrTypes }
	 * 
	 * 
	 */
	public List<TypeAttrTypes> getAttributeTypes() {
		if (attributeTypes == null) {
			attributeTypes = new ArrayList<TypeAttrTypes>();
		}
		return this.attributeTypes;
	}

	/**
	 * Gets the value of the directory property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the directory property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDirectory().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TypeDirectory }
	 * 
	 * 
	 */
	public List<TypeDirectory> getDirectory() {
		if (directory == null) {
			directory = new ArrayList<TypeDirectory>();
		}
		return this.directory;
	}

}
