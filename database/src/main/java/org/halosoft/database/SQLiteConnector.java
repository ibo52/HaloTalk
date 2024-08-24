package org.halosoft.database;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/* 
 * Creates a new or loads existing database file for given path 
 * to communicate with Database
*/
public class SQLiteConnector extends Connector {
    
    /* 
     * Creates a new or loads existing database file for given path
     * 
     * @param path: existing sqlite db 'FILE' or desired name to be created, with path specified
     * 
    */
    public SQLiteConnector(String path){
        
        super("sqlite", path);

    }

    @Override
    public Connection connect() {
        Connection retval=null;
        try {
            
            retval = DriverManager.getConnection(url);

            System.err.println(String.format("[%s]: Connection to SQLite has been established.", this.getClass().getName()));
        
        } catch (SQLException e) {
            
            System.err.println(String.format("[%s]: %s", getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }

        return retval;
    }
    @Override
    public QueryResultSet query(String queryString){

        QueryResultSet retval2=null;

        try (Statement stmt = conn.createStatement() ){

            int retvalInt=0;

            for (String token : queryString.toLowerCase().split("\n")) {

                if(
                (  !token.stripLeading().startsWith("--")
                || !token.stripLeading().startsWith("/*")
                || !token.stripTrailing().endsWith("*/"))
                && 
                (  token.contains("delete")
                || token.contains("update")
                || token.contains("insert")
                || token.contains("create")
                )){
                    retvalInt=1;
                    break;
                }
            }

            if( retvalInt==1 ){

                retvalInt=stmt.executeUpdate(queryString);
                retval2=new QueryResultSet(retvalInt);

            }else{

                ResultSet retval=stmt.executeQuery(queryString);
                
                retval2=new QueryResultSet(retval);
            }

        } catch (Exception e) {

            System.err.println(String.format("[%s]: %s", getClass().getName(), e.getMessage()));
            System.err.println(String.format("query: %s", queryString));
            e.printStackTrace();
        }

        return retval2;
    }

    @Override
    public QueryResultSet queryFromFile(Path filepath) throws NoSuchFileException{

        if ( !Files.exists(filepath) ) {
            
            throw new NoSuchFileException(filepath.toString());
        }

        QueryResultSet retval=null;

        try (Scanner reader = new Scanner(filepath.toFile())) {

            StringBuilder queryToBuild=new StringBuilder();

            while( reader.hasNextLine() ){
                
                String line=reader.nextLine().stripTrailing();

                queryToBuild.append(line).append('\n');

                if( !line.equals("") && line.charAt(line.length()-1)==';' ){

                    this.query(queryToBuild.toString().stripLeading().stripTrailing());
                    
                    queryToBuild=new StringBuilder();

                }
            }            
        
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }


        return retval;
    }

    public static void main(String[] args) {

        SQLiteConnector c=new SQLiteConnector("temp.db");

        //c.queryFromFile(Paths.get(SQLiteConnector.class.getResource("/tables.sql").getPath()));
        
        SQLiteConnector.print( c.query("select * from sender") );

        SQLiteConnector.print(c.query("insert into sender(ip) values('asd')"));

        SQLiteConnector.print( c.query("select * from sender") );

        SQLiteConnector.print(c.query("delete from sender where ip='asd'"));

        SQLiteConnector.print( c.query("select * from sender") );

    }
}