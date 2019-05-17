package seg.unifesp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {
  
  private static Writer csvWriter = null;
  private static String[] featureWords = null;
  
  public static void main(String[] args) {
    // open csv file and add header
    String fileType = "neng";
    try {
      csvWriter = openFile("comments_" + fileType + ".csv", false);
    } catch (IOException e) {
      System.out.println("Error trying to open csv file.");
      e.printStackTrace();
    }
    addHeaderToCsv();
    
    // open word features file
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("feature-words.file"), "UTF-8"));
      String line = br.readLine();
      featureWords = line.split(",");
      System.out.println("Number of feature words: " + featureWords.length);
    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    
    int countComment = 0;
    
    BufferedReader br;
    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream("comments-" + fileType + ".file"), "UTF-8"));
      String line = null;
      String comment = "";
      while ((line = br.readLine()) != null) {
          line = line.trim();
          if(line.length() > 0 && !line.contains("<<<COMMENT SEPARATOR>>>")) {
            comment += line + "\n";
          } else if (line.contains("<<<COMMENT SEPARATOR>>>")) {
            System.out.println("\nComment:\n" + comment);
            countComment++;
            extractAndStoreFeatures(comment);
            comment = "";
          }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
 
    // for each comment:
      // extract features and write to a csv file
    // close csv file
    
    closeOutputFile(csvWriter);
  }
  
  private static void extractAndStoreFeatures(String comment) {
     // Number of characters,Number of rows,Number of words,Unique words,Summary,Expand,Rational,Deprecation,Usage,Exception,TODO,Incomplete,Commented code,Directive,Formatter,License,Ownership,Pointer,Automatic generated,
     addFieldValueToCsvWithComma(String.valueOf(comment.length()));
     addFieldValueToCsvWithComma(String.valueOf(comment.split(" ").length));
     addFieldValueToCsvWithComma(String.valueOf(comment.split(" ").length));
     addFieldValueToCsvWithComma(String.valueOf(getNumberOfUniqueWords(comment.split(" "))));
     addFieldValueToCsvWithComma(String.valueOf(0));
     addFieldValueToCsvWithComma(String.valueOf(0));
     addFieldValueToCsvWithComma(String.valueOf(0));
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)@deprecated"))); 
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)@param|@usage|@throws|@since|@noextend|@noimplement|@value|@return|for example"))); // ok
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)exception|@throws|expected")));
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)todo|fix|fixme|ignore|bug|note|#[0-9]*")));
     addFieldValueToCsvWithComma(String.valueOf(isEmpty(comment) ? "1" : "0"));
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)[a-zA-Z]+\\.[a-zA-Z]+\\(.*\\)|if\\s\\(|while\\s\\(|for\\s\\(|;|=|==|void|int|double|String|boolean|public|private|protected|char"))); 
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "\\$.*\\$")));
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "([^*\\s])(\\1\\1)|^\\s*\\/\\/\\/\\s*\\S*|\\$\\S*\\s*\\S*\\$")));
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)license|copyright|reserved|terms|distribut|legal|warrant|law")));
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)@author|@owner|contributor")));
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)@link|see|@inheritDoc|@literal|@code")));
     addFieldValueToCsvWithComma(String.valueOf(countMatches(comment, "(?is)Auto-generate|non-Javadoc")));
     addAllWordChecks(comment);
     addLineBreakToCsv();
  }

  private static void addAllWordChecks(String comment) {
    for(int i = 0; i < featureWords.length-1; i++) {
      addFieldValueToCsvWithComma(comment.contains(featureWords[i]) ? "1" : "0");
    }
    addFieldValueToCsv(comment.contains(featureWords[featureWords.length-1]) ? "1" : "0");
  }

  private static boolean isEmpty(String comment) {
    for(char c : comment.toCharArray()) {
      if(c != '/' || c != ' ' || c != '*')
        return false;
    }
    return true;
  }

  public static int countMatches(String comment, String pattern) {
    Pattern p = Pattern.compile(pattern);
    int count = 0;
    Matcher matcher = p.matcher(comment); 
    
    while (matcher.find())
        count++;
    return count;
  }

  private static void addLineBreakToCsv() {
    try {
      csvWriter.append("\n");
    } catch (IOException e) {
      e.printStackTrace();
    }  }

  public static int getNumberOfUniqueWords(String[] split) {
    ArrayList<String> uniqueWords = new ArrayList<String>();
    for(String s : split) {
      if(!uniqueWords.contains(s))
        uniqueWords.add(s);
    }
    return uniqueWords.size();
  }

  private static void addFieldValueToCsvWithComma(String value) {
    try {
      csvWriter.append(value + ",");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static void addFieldValueToCsv(String value) {
    try {
      csvWriter.append(value);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void addHeaderToCsv() {
    try {
      csvWriter.append("Number of characters,Number of rows,Number of words,Unique words,Summary,Expand,Rational,Deprecation,Usage,Exception,TODO,Incomplete,Commented code,Directive,Formatter,License,Ownership,Pointer,Automatic generated,");
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("feature-words.file"), "UTF-8"));
      String line = null;
      while ((line = br.readLine()) != null && line.trim().length() > 0) {
          line = line.trim();
          csvWriter.append(line);
      }
      csvWriter.append("\n");
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Writer openFile(String filename, boolean append) throws IOException {
    try {
      Writer pWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, append), "UTF-8"),
          1024 * 100);
      return pWriter;

    } catch (IOException e) {
      System.err.println(e.getMessage());
      throw e;
    }
  }

  public static void closeOutputFile(Writer pWriter) {
    if (null != pWriter) {
      try {
        pWriter.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        pWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
