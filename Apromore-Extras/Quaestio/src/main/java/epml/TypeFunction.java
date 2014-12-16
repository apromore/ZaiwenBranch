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
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for typeFunction complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="typeFunction">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.epml.de}tEpcElement">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="toProcess" type="{http://www.epml.de}typeToProcess" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="configurableFunction" type="{http://www.epml.de}typeCFunction" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="attribute" type="{http://www.epml.de}typeAttribute" minOccurs="0"/>
 *         &lt;/choice>
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
@XmlType(name = "typeFunction", propOrder = { "toProcess",
		"configurableFunction", "attribute" })
public class TypeFunction extends TEpcElement {

	protected TypeToProcess toProcess;
	protected TypeCFunction configurableFunction;
	protected List<TypeAttribute> attribute;

	/**
	 * Gets the value of the toProcess property.
	 * 
	 * @return possible object is {@link TypeToProcess }
	 * 
	 */
	public TypeToProcess getToProcess() {
		return toProcess;
	}

	/**
	 * Sets the value of the toProcess property.
	 * 
	 * @param value
	 *            allowed object is {@link TypeToProcess }
	 * 
	 */
	public void setToProcess(TypeToProcess value) {
		this.toProcess = value;
	}

	/**
	 * Gets the value of the configurableFunction property.
	 * 
	 * @return possible object is {@link TypeCFunction }
	 * 
	 */
	public TypeCFunction getConfigurableFunction() {
		return configurableFunction;
	}

	/**
	 * Sets the value of the configurableFunction property.
	 * 
	 * @param value
	 *            allowed object is {@link TypeCFunction }
	 * 
	 */
	public void setConfigurableFunction(TypeCFunction value) {
		this.configurableFunction = value;
	}

	/**
	 * Gets the value of the attribute property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the attribute property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAttribute().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TypeAttribute }
	 * 
	 * 
	 */
	public List<TypeAttribute> getAttribute() {
		if (attribute == null) {
			attribute = new ArrayList<TypeAttribute>();
		}
		return this.attribute;
	}

}