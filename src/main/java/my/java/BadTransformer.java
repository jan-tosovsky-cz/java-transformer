package my.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class BadTransformer {

    public static void main(String[] args) throws IOException {

        String xmlContent = """
                <simplelist>
                    <member><inlinemediaobject/><link/></member>
                    <member><inlinemediaobject/><!--comment--><link/></member>
                    <member><inlinemediaobject/>XXX<link/></member>
                </simplelist>
                """;
        String xslContent = """

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="link | inlinemediaobject | comment()" priority="10">
        <xsl:value-of select="count(preceding-sibling::node()[1][self::*])"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="count(preceding-sibling::node()[1][self::comment()])"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="count(preceding-sibling::node()[1][self::* or self::comment()])"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="count(preceding-sibling::node()[1][self::*|self::comment()])"/>
        <xsl:text>-</xsl:text>
        <xsl:choose>
            <xsl:when test="preceding-sibling::node()[1][self::* or self::comment()]">space</xsl:when>
            <xsl:when test="preceding-sibling::node()[1][self::*]">element</xsl:when>
            <xsl:when test="preceding-sibling::node()[1][self::comment]">comment</xsl:when>
            <xsl:otherwise>other</xsl:otherwise>
        </xsl:choose>

        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>

    </xsl:template>

</xsl:stylesheet>
        """;

        InputStream xmlStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
        InputStream xslStream = new ByteArrayInputStream(xslContent.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslStream));
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            transformer.transform(new SAXSource(reader, new InputSource(xmlStream)), new StreamResult(outputStream));

            System.out.println(outputStream.toString("UTF-8"));

        } catch (ParserConfigurationException | SAXException | TransformerException e) {
            throw new IOException(e);
        }
    }
}