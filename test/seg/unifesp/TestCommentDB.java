package seg.unifesp;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

class TestCommentDB {

  @Test
  void test() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Connection con=DriverManager.getConnection(  
          "jdbc:mysql://localhost:3306/comments","root","1234"); 
      Statement statement = con.createStatement();
      //The next line has the issue
      statement.executeUpdate("insert into comments  values (1, \"test123\")");
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }  
     
  }

}
