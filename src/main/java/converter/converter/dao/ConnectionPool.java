package converter.converter.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



public class ConnectionPool {


    private static  final Properties properties= new Properties();

    static {
        loadProperties();
    }

    private  static void loadProperties(){
        try(InputStream input= ConnectionPool.class.getClassLoader()
                .getResourceAsStream("database.properties")){
            if(input==null){
                System.out.println("Sorry, unable to find database.properties");
                return;
            }
            properties.load(input);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  Connection getConnection(){
        Connection connection= null;

        try {
            Class.forName(properties.getProperty("db.driver"));
            connection= DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
            System.out.println("Connection ok");
        } catch (ClassNotFoundException  | SQLException e) {
            e.printStackTrace();
            System.out.println("Connection error");
        }
        return connection;

    }


}
