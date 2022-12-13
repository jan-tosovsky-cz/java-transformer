package my.java;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPathTester {

    public static void main(String[] args) throws XPathExpressionException {

        String xml01 = "<root><a/><b/><c/><!--comment--></root>";
        String xml02 = """
                <simplelist>
                    <member><inlinemediaobject/><link/></member>
                    <member><inlinemediaobject/><!--comment--><link/></member>
                    <member><inlinemediaobject/>XXX<link/></member>
                </simplelist>
                """;
        String xPath01 = "root/node()[self::a [$operator] self::comment()]";
        String xPath02 = "simplelist/member[1]/link/preceding-sibling::node()[1][self::* [$operator] self::comment()]";
        String xPath03 = "simplelist/member[2]/link/preceding-sibling::node()[1][self::* [$operator] self::comment()]";

        compareOperators(1, xml01, xPath01);
        compareOperators(2, xml02, xPath02);
        compareOperators(3, xml02, xPath03);
    }

    private static void compareOperators(int index, String xml, String xPath) throws XPathExpressionException {
        int pipeCount = getNodeList(xml, getXPath(xPath, "|")).getLength();
        int orCount = getNodeList(xml, getXPath(xPath, "or")).getLength();
        System.out.println(index + " " + ((pipeCount == orCount) ? "Correct" : "Incorrect"));
    }

    private static String getXPath(String pattern, String operator) {
        return pattern.replace("[$operator]", operator);
    }

    private static NodeList getNodeList(String xml, String xPath) throws XPathExpressionException {
        InputSource inputSource = new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        return (NodeList) XPathFactory.newInstance().newXPath().compile(xPath).evaluate(inputSource, XPathConstants.NODESET);
    }

}
