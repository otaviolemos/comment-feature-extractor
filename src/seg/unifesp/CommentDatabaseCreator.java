package seg.unifesp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentDatabaseCreator {
  
  private static Connection con;
  private static Statement statement;
  private static boolean getFromMlClassifier = false;

  
  public static void main(String[] args) {
    String fileType = "neng";
    connectToDataBase();
    
    BufferedReader brComment, brClass;
    int commentId = 1;
    try {
      brComment = new BufferedReader(new InputStreamReader(new FileInputStream("comments-" + fileType + ".file"), "UTF-8"));
      brClass = new BufferedReader(new InputStreamReader(new FileInputStream(fileType + "_class.csv"), "UTF-8"));
      String line = null;
      String comment = "", classification = "";
      
      while ((line = brComment.readLine()) != null) {
          line = line.trim();
          if(line.length() > 0 && !line.contains("<<<COMMENT SEPARATOR>>>")) {
            comment += line + "\n";
          } else if (line.contains("<<<COMMENT SEPARATOR>>>")) {
            if(getFromMlClassifier)
              classification = brClass.readLine();
            else
              classification = getSimpleClassification(comment);
            System.out.println("\nComment:\n" + comment);
            System.out.println("\nClassification: " + classification);
            addCommentToDatabase(comment, commentId++, fileType.equals("eng") ? 1 : 0, classification);
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
  }

  private static String getSimpleClassification(String comment) {
    if(isEmpty(comment)) {
      return "Empty";
    }
    
    int features[] = new int[11];
    features[0] = countMatches(comment, "(?is)@deprecated"); 
    features[1] = countMatches(comment, "(?is)@param|@usage|@throws|@since|@noextend|@noimplement|@value|@return|for example");
    features[2] = countMatches(comment, "(?is)exception|@throws|expected");
    features[3] = countMatches(comment, "(?is)todo|fix|fixme|ignore|bug|note|#[0-9]*");
    features[4] = countMatches(comment, "(?is)[a-zA-Z]+\\.[a-zA-Z]+\\(.*\\)|if\\s\\(|while\\s\\(|for\\s\\(|;|=|==|void|int|double|String|boolean|public|private|protected|char");
    features[5] = countMatches(comment, "([^*\\s])(\\1\\1)|^\\s*\\/\\/\\/\\s*\\S*|\\$\\S*\\s*\\S*\\$");
    features[6] = countMatches(comment, "(?is)license|copyright|reserved|terms|distribut|legal|warrant|law");
    features[7] = countMatches(comment, "(?is)@author|@owner|contributor");
    features[8] = countMatches(comment, "(?is)@link|see|@inheritDoc|@literal|@code");
    features[9] = countMatches(comment, "(?is)Auto-generate|non-Javadoc");
    features[10] = countMatches(comment, "(?is)what|how|why");
    
    int maxInd = 0;
    for(int i = 1; i <= 10; i++) {
      if(features[i] >= features[maxInd])
        maxInd = i;
    }
    
    switch(maxInd) {
    case 0:
      return "Deprecated";
    case 1:
      return "Usage";
    case 2:
      return "Exception";
    case 3:
      return "Under development";
    case 4:
      return "Commented code";
    case 5:
      return "Style and IDE";
    case 6:
      return "License";
    case 7:
      return "Ownership";
    case 8:
      return "Pointer";
    case 9:
      return "Auto-generated";
    case 10:
      return "Explanation";
    }
    
    return "Explanation";

  }
  
  public static int countMatches(String comment, String pattern) {
    Pattern p = Pattern.compile(pattern);
    int count = 0;
    Matcher matcher = p.matcher(comment); 
    
    while (matcher.find())
        count++;
    return count;
  }

  
  private static boolean isEmpty(String comment) {
    for(char c : comment.toCharArray()) {
      if(c != '/' || c != ' ' || c != '*')
        return false;
    }
    return true;
  }

  private static void connectToDataBase() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      if(getFromMlClassifier)
        con=DriverManager.getConnection(  
          "jdbc:mysql://localhost:3306/comments","root","1234"); 
      else
        con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/comments_simple","root","1234"); 
      statement = con.createStatement();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      System.err.println("Error while trying to connect to database.");
      e.printStackTrace();
    }  
  }

  private static void addCommentToDatabase(String comment, int commentId, int type, String classification) {
    try {
      statement.executeUpdate("insert into comments values ("+ commentId + ", \"" + comment.replace("\"",  "") + "\", " + 
                               type + ", \"" + classification + "\")");
    } catch (SQLException e) {
      System.err.println("Error while trying to insert comment to database.");
      e.printStackTrace();
    }
  }
}
