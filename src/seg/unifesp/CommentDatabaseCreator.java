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

public class CommentDatabaseCreator {
  
  private static Connection con;
  private static Statement statement;

  
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
            classification = brClass.readLine();
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

  private static void connectToDataBase() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      con=DriverManager.getConnection(  
          "jdbc:mysql://localhost:3306/comments","root","1234"); 
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
