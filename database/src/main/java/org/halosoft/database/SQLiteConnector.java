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
import java.util.Locale;
import java.util.Scanner;
import java.io.InputStream;
import java.lang.System.Logger.Level;
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

            TalkDBProperties.logger.log(Level.TRACE, String.format("[%s]: Connection to SQLite has been established.", this.getClass().getName()));
        
        } catch (SQLException e) {
            
            TalkDBProperties.logger.log(Level.ERROR, String.format("[%s]: %s", getClass().getName(), e.getMessage()), e);
        }

        return retval;
    }
    @Override
    public QueryResultSet query(String queryString){

        QueryResultSet retval2=null;

        try (Statement stmt = conn.createStatement() ){

            int retvalInt=0;

            for (String token : queryString.toLowerCase(Locale.ENGLISH).split("\n")) {

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

            TalkDBProperties.logger.log(Level.ERROR, String.format("[%s]: %s\n QUERY:\n %s", getClass().getName(), e.getMessage(), queryString), e);
        }

        return retval2;
    }

    @Override
    public QueryResultSet queryFromFile(Path filepath) throws NoSuchFileException{

        if ( !Files.exists(filepath) ) {
            
            throw new NoSuchFileException(filepath.toString()+String.format("(path %s absolute)", filepath.isAbsolute()? "is": "is NOT"));
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

    public QueryResultSet queryFromFile(InputStream fileStream) throws NoSuchFileException{

        if ( fileStream==null ) {
            
            throw new NoSuchFileException("(File stream is null!");
        }

        QueryResultSet retval=null;

        try (Scanner reader = new Scanner(fileStream)) {

            StringBuilder queryToBuild=new StringBuilder();

            while( reader.hasNextLine() ){
                
                String line=reader.nextLine().stripTrailing();

                queryToBuild.append(line).append('\n');

                if( !line.equals("") && line.charAt(line.length()-1)==';' ){

                    this.query(queryToBuild.toString().stripLeading().stripTrailing());
                    
                    queryToBuild=new StringBuilder();

                }
            }            
        
        }


        return retval;
    }
}