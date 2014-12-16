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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.03 at 05:04:23 PM CET 
//

package org.yawlfoundation.yawlschema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ResourcingPrivilegeType.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="ResourcingPrivilegeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="canSuspend"/>
 *     &lt;enumeration value="canReallocateStateless"/>
 *     &lt;enumeration value="canReallocateStateful"/>
 *     &lt;enumeration value="canDeallocate"/>
 *     &lt;enumeration value="canDelegate"/>
 *     &lt;enumeration value="canSkip"/>
 *     &lt;enumeration value="canPile"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ResourcingPrivilegeType")
@XmlEnum
public enum ResourcingPrivilegeType {

	@XmlEnumValue("canSuspend")
	CAN_SUSPEND("canSuspend"), @XmlEnumValue("canReallocateStateless")
	CAN_REALLOCATE_STATELESS("canReallocateStateless"), @XmlEnumValue("canReallocateStateful")
	CAN_REALLOCATE_STATEFUL("canReallocateStateful"), @XmlEnumValue("canDeallocate")
	CAN_DEALLOCATE("canDeallocate"), @XmlEnumValue("canDelegate")
	CAN_DELEGATE("canDelegate"), @XmlEnumValue("canSkip")
	CAN_SKIP("canSkip"), @XmlEnumValue("canPile")
	CAN_PILE("canPile");
	private final String value;

	ResourcingPrivilegeType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ResourcingPrivilegeType fromValue(String v) {
		for (ResourcingPrivilegeType c : ResourcingPrivilegeType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
