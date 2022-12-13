# Java Transformer

This is a set of tests for reproducing Java issue when using a default XSLT processor.

## Description
In XPath you can return combined nodes using `/*[self::a or self::b]`. That `OR` operator can be replaced 
with pipe `/*[self::a | self::b]`. However, Java implementation in some cases returns different results 
for these operators. From my test cases, I deduce it is rather an edge case. But this unreliability pushes 
me to prefer 3rd party library (saxon).

## Procedure
1. Clone this repo
2. Run BadTransformer.java
3. Inspect the output

## Actual result
```
<?xml version="1.0" encoding="UTF-8"?><simplelist>
    <member>0-0-0-0-other<inlinemediaobject/>1-0-0-1-element<link/></member>
    <member>0-0-0-0-other<inlinemediaobject/>1-0-0-1-element<!--comment-->0-1-1-1-space<link/></member>
    <member>0-0-0-0-other<inlinemediaobject/>XXX0-0-0-0-other<link/></member>
</simplelist>
```
## Expected result
```
<?xml version="1.0" encoding="UTF-8"?><simplelist>
    <member>0-0-0-0-other<inlinemediaobject/>1-0-1-1-space<link/></member>
    <member>0-0-0-0-other<inlinemediaobject/>1-0-1-1-space<!--comment-->0-1-1-1-space<link/></member>
    <member>0-0-0-0-other<inlinemediaobject/>XXX0-0-0-0-other<link/></member>
</simplelist>
```

## Comments
The same combining syntax is used also in CorrectTransformer and XPathTester, where it works properly.

To see a mystery:
1. On line 41 replace:
   `<xsl:template match="link | inlinemediaobject | comment()" priority="10">`
   with
   `<xsl:template match="link | comment()" priority="10">`
2. Run the transformation again.
3. Inspect the output (now it is correct!)
