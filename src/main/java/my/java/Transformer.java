package my.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Transformer {

    public static void main(String[] args) throws IOException {

        String xmlContent = "<root><a/><b/><c/><!--comment--></root>";
        String xslContent = """

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/">
        <xsl:value-of select="count(root/node()[self::a])"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="count(root/node()[self::comment()])"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="count(root/node()[self::a or self::comment()])"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="count(root/node()[self::a|self::comment()])"/>
    </xsl:template>
</xsl:stylesheet>
        """;

        InputStream xmlStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
        InputStream xslStream = new ByteArrayInputStream(xslContent.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            javax.xml.transform.Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslStream));
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            transformer.transform(new SAXSource(reader, new InputSource(xmlStream)), new StreamResult(outputStream));

            System.out.println(outputStream.toString("UTF-8"));

        } catch (ParserConfigurationException | SAXException | TransformerException e) {
            throw new IOException(e);
        }
    }
}