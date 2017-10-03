/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.phaser.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.jboss.qa.phaser.PhaseDefinition;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class XmlUtils {

	private static Document doc = null;

	public static void generateReport(Throwable ex, PhaseDefinition phaseDefinition) {
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();

			final Element rootElement = createTestsuiteElement(phaseDefinition, ex);
			doc.appendChild(rootElement);

			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(doc);
			final File reportsDir = new File(phaseDefinition.getReportsHandling().getReportsDir());
			final File reportFile = new File(reportsDir, "TEST-"
					+ phaseDefinition.getJob().getClass().getSimpleName() + "-" + System.nanoTime() + ".xml");
			reportFile.getParentFile().mkdirs();
			reportFile.createNewFile();
			final StreamResult result = new StreamResult(reportFile);
			transformer.transform(source, result);

			log.info("generated report at: " + reportFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Element createTestsuiteElement(PhaseDefinition phaseDefinition, Throwable ex) {
		final Element elem = doc.createElement("testsuite");
		final Element propsElem = doc.createElement("properties");
		final Properties props = System.getProperties();
		if (props != null) {
			final Enumeration e = props.propertyNames();
			while (e.hasMoreElements()) {
				final String name = (String) e.nextElement();
				final Element propElement = doc.createElement("property");
				propElement.setAttribute("name", name);
				propElement.setAttribute("value", props.getProperty(name));
				propsElem.appendChild(propElement);
			}
		}
		elem.appendChild(propsElem);
		elem.setAttribute("name", phaseDefinition.getJob().getClass().toString());
		elem.setAttribute("tests", "1");
		elem.setAttribute("skipped", "0");
		elem.setAttribute("failures", "0");
		if (ex == null) {
			elem.setAttribute("errors", "0");
		} else {
			elem.setAttribute("errors", "1");
		}
		elem.appendChild(createTestCaseElement(phaseDefinition, ex));
		return elem;
	}

	private static Element createTestCaseElement(PhaseDefinition phaseDefinition, Throwable ex) {
		final Element elem = doc.createElement("testcase");
		elem.setAttribute("name", phaseDefinition.getMethod().getName());
		elem.setAttribute("classname", phaseDefinition.getJob().getClass().getCanonicalName());
		// TODO(jkasztur): implement time
		elem.setAttribute("time", "0");
		if (ex != null) {
			final Element failElem = doc.createElement("failure");
			failElem.setAttribute("type", ex.getCause().getClass().getName());
			failElem.setTextContent(ExceptionUtils.getStackTrace(ex.getCause()));
			elem.appendChild(failElem);
		}
		return elem;
	}

	private XmlUtils() {
	}
}
