import java.sql.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        //Gọi procedure trong ứng dụng Java (JDBC)
        String url="jdbc:mysql://localhost:3306/database_training";
        String user = "root";
        String password = "Nham3112@";
        try(Connection conn= DriverManager.getConnection(url,user,password)){
            // Gọi stored procedure
            CallableStatement statement=conn.prepareCall("{CALL GetStudentByClass(?)}");
            statement.setInt(1,1);
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next()){
                System.out.println(
                        resultSet.getInt("student_id") + " - " +
                        resultSet.getString("name") + " - " +
                        resultSet.getInt("age") + " - " +
                        resultSet.getString("class_name")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}