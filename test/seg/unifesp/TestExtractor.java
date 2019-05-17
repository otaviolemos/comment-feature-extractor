package seg.unifesp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class TestExtractor {

  @Test
  void testUniqueWords() {
    String[] sv = {"test", "test", "test", "one", "two", "two", "three"};
    assertEquals(4, Extractor.getNumberOfUniqueWords(sv));
  }
  
  @Test
  void testRegexDeprecated() {
    String s = "\n   * @deprecated \n * \n \n";
    assertEquals(1, Extractor.countMatches(s, "(?is)@deprecated"));
  }
  
  @Test
  void testRegexAuthor() {
    String s = "\n" + 
        " * @author BoazH\n" + 
        " *\n" + 
        " \n" + 
        "";
    assertEquals(1, Extractor.countMatches(s, "(?is)@author|@owner|contributor"));
  }
  
  @Test
  void testRegexLicense() {
    String s = "public static final int QUERY = 4;\n" + 
        "\n" + 
        "";
    assertEquals(0, Extractor.countMatches(s, "(?is)(license|copyright|reserved|terms|distribut|legal|warrant|law)"));
  }
  
  @Test
  void testRegexLicense2() {
    String s = "* This code is copyright (c) Adam Buckley 2004\n" + 
        "*\n" + 
        "* This program is free software; you can redistribute it and/or modify it\n" + 
        "* under the terms of the GNU General Public License as published by the Free\n" + 
        "* Software Foundation; either version 2 of the License, or (at your option)\n" + 
        "* any later version.  A HTML version of the GNU General Public License can be\n" + 
        "* seen at http://www.gnu.org/licenses/gpl.html\n" + 
        "*\n" + 
        "* This program is distributed in the hope that it will be useful, but WITHOUT\n" + 
        "* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or\n" + 
        "* FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for\n" + 
        "* more details.\n" + 
        "*\n" + 
        "*\n" + 
        "* Comments for member variables are taken from RFC2030 by David Mills,\n" + 
        "* University of Delaware.\n" + 
        "*\n" + 
        "* Number format conversion code in NtpMessage(byte[] array) and toByteArray()\n" + 
        "* inspired by http://www.pps.jussieu.fr/~jch/enseignement/reseaux/\n" + 
        "* NTPMessage.java which is copyright (c) 2003 by Juliusz Chroboczek\n" + 
        "*\n" + 
        "* @author Adam Buckley";
    assertEquals(13, Extractor.countMatches(s, "(?is)(license|copyright|reserved|terms|distribut|legal|warrant|law)"));
  }
  
  @Test 
  void testRegexCode() {
    String s = "public static final int QUERY = 4;\n" + 
        "for (i=0; i<10;i++) {\n" + 
        "";
    assertEquals(8, Extractor.countMatches(s, "(?is)[a-zA-Z]+\\.[a-zA-Z]+\\(.*\\)|if\\s\\(|while\\s\\(|for\\s\\(|;|=|==|void|int|double|String|boolean|public|private|protected|char"));
  }
  
  @Test
  void testRegexParam() {
    String s = "Returns the first ELEMENT with name str, while ignoring whitespace nodes\n" + 
        "*\n" + 
        "* @param elm\n" + 
        "* @param str\n" + 
        "* @return the String value of a child node of Element elm that has a name str\n" + 
        "* or null if no match\n" + 
        "\n";
    assertEquals(3, Extractor.countMatches(s, "(?is)(@param|@usage|@throws|@since|@noextend|@noimplement|@value|@return|for example)"));
  }
  
  @Test
  void testRegexDirective() {
    String s = "$NON-NLS1$";
    assertEquals(1, Extractor.countMatches(s, "\\$.*\\$"));
  }
  
  @Test
  void testRegexFormatter() {
    String s = "$asdasd$";
    assertEquals(1, Extractor.countMatches(s, "([^*\\s])(\\1\\1)|^\\s*\\\\/\\\\/\\\\/\\\\s*\\\\S*|\\$\\S*\\s*\\S*\\$"));
  }
  
  @Test
  void testRegexFormatter2() {
    String s = "\\\\\\";
    assertEquals(1, Extractor.countMatches(s, "([^*\\s])(\\1\\1)|^\\s*\\/\\/\\/\\s*\\S*|\\$\\S*\\s*\\S*\\$"));
  }

  @Test 
  void testRegexCode2() {
    String s = "this.setBlockAndMetadata(this.worldObj, aint1[0], aint1[1], aint1[2], par6, 0);";
    assertEquals(2, Extractor.countMatches(s, "(?is)[a-zA-Z]+\\.[a-zA-Z]+\\(.*\\)|if\\s\\(|while\\s\\(|for\\s\\(|;|=|==|void|int|double|String|boolean|public|private|protected|char"));
  }
  
  @Test
  void testRegexCode3() {
    String s = "-------------------------------------------------------------------------------------";
    assertEquals(0, Extractor.countMatches(s, "(?is)[a-zA-Z]+\\.[a-zA-Z]+\\(.*\\)|if\\s\\(|while\\s\\(|for\\s\\(|;|=|==|void|int|double|String|boolean|public|private|protected|char"));
  }
  
  @Test
  void testAutoGenerate() {
    String s = " TODO Auto-generated catch block";
    assertEquals(1, Extractor.countMatches(s, "(?is)Auto-generate|non-Javadoc"));
  }
}
