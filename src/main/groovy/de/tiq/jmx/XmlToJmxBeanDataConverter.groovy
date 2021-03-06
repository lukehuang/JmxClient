/*
 * Copyright 2013
 * Dan Häberlein
 * TIQ Solutions GmbH
 * Weißenfelser Str. 84
 * 04229 Leipzig, Germany
 *
 * info@tiq-solutions.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.tiq.jmx

import javax.management.ObjectName

class XmlToJmxBeanDataConverter {

	String pathToFile;

	XmlToJmxBeanDataConverter(String pathToFile) {
		this.pathToFile = pathToFile
	}

	List<JmxMBeanData> convertTo(){
		def result = []
		def xmlContent = new File(pathToFile).getText('UTF-8')
		def jmxMBeanDataList = new XmlParser().parseText(xmlContent)
		jmxMBeanDataList.each { node ->
			def associatedMethodName = node.associatedObjectName.text()
			def attributes = getAttributesOfMBean(node)
			//the get method of a node returns a nodelist, so we need to get the first value of it with [0]
			result << new JmxMBeanData(associatedObjectName:new ObjectName(associatedMethodName), attributes:attributes)
		}
		return result
	}

	private List getAttributesOfMBean(node) {
		def attributes = []
		def applyableMethods = node.attributes[0].children().each { attributeNode ->
			def name = attributeNode.name.text()
			def keys = attributeNode.compositeKeys.key.collect{  it.text() }
			attributes << new Attribute(name: name, compositeKeys: keys)
		}
		return attributes
	}
}
