package jp.skypencil.findbugs.slf4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class XmlParser {
    void expect(Class<?> clazz, Map<String, Integer> expected) {
        final String filePath = "pkg." + clazz.getSimpleName();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("target/findbugsXml.xml");
            NodeList bugs = doc.getElementsByTagName("BugInstance");
            Map<String, Integer> actual = new HashMap<String, Integer>();

            for (int i = 0; i < bugs.getLength(); ++i) {
                Node bug = bugs.item(i);
                NamedNodeMap attr = bug.getAttributes();
                String className = findClass(bug).getNamedItem("classname").getNodeValue();
                if (className.equals(filePath)) {
                    String bugType = attr.getNamedItem("type").getNodeValue();
                    Integer counter = actual.get(bugType);
                    if (counter == null) {
                        actual.put(bugType, 1);
                    } else {
                        actual.put(bugType, counter + 1);
                    }
                }
            }
            assertThat(actual, is(equalTo(expected)));
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        } catch (SAXException e) {
            throw new AssertionError(e);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private NamedNodeMap findClass(Node bug) {
        NodeList children = bug.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeName().equals("Class")) {
                return child.getAttributes();
            }
        }
        throw new AssertionError();
    }

    /**
     * @deprecated use {@code #expect(Class, Map)} instead.
     */
    @Deprecated
    void expectBugs(Class<?> clazz, int expectedBugs) {
        final String className = clazz.getSimpleName();
        final String filePath = "pkg/" + className + ".java";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("target/findbugsXml.xml");
            NodeList fileStats = doc.getElementsByTagName("FileStats");

            for (int i = 0; i < fileStats.getLength(); ++i) {
                Node node = fileStats.item(i);
                NamedNodeMap attr = node.getAttributes();
                if (attr.getNamedItem("path").getNodeValue().equals(filePath)) {
                    final String bugCount = attr.getNamedItem("bugCount")
                            .getNodeValue();
                    final String message;
                    if (expectedBugs == 0) {
                        message = filePath + " should have no bug";
                    } else {
                        message = filePath + " should have " + expectedBugs
                                + " bugs";
                    }

                    assertThat(message, bugCount,
                            is(Integer.toString(expectedBugs)));
                    return;
                }
            }

            fail("Result of " + filePath + " not found");
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        } catch (SAXException e) {
            throw new AssertionError(e);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
