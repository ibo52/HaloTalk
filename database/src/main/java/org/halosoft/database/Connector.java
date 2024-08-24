package org.halosoft.database;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Connector {

    protected final Connection conn;
    protected final String url;

    public Connector(String databaseDriverName, String path){

        this.url=String.format("jdbc:%s:%s",databaseDriverName,path);

        conn=connect();
    }

    public Connection getConnection(){
        
        return conn;
    }

    abstract Connection connect();

    abstract QueryResultSet query(String queryString);

    abstract QueryResultSet queryFromFile(Path filepath) throws NoSuchFileException;

    public static void print(ResultSet retval){
        
        if( retval!=null ){

            try {
                
                ResultSetMetaData rsmd = retval.getMetaData();
            
                int columnsNumber = rsmd.getColumnCount();

                while (retval.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1) System.out.print(",  ");
                        String columnValue = retval.getString(i);
                        System.out.print(columnValue + " " + rsmd.getColumnName(i));
                    }
                    System.out.println("");
                
                }

            } catch (SQLException e) {

                e.printStackTrace();
                
            }
        }
    }

    public static void print(QueryResultSet retval){
        
        if( retval!=null ){

            try {
                
                StringBuilder b=new StringBuilder();
                int maxFMT=22;

                for(int i=0;i< retval.getColumnCount(); i++){

                    String data= retval.getColumnName(i).length()>maxFMT? retval.getColumnName(i).substring(0, maxFMT-3)+"..": retval.getColumnName(i);
                    b.append(String.format( ("%-"+maxFMT+"s"), data ));
                }
                
                System.out.println(b.append("\n")+Stream.generate(() -> "-").limit(maxFMT*retval.getColumnCount()).collect(Collectors.joining()));

                while (retval.hasNext()) {

                    LinkedList<String> rows=retval.getNextRecord();

                    for (int i = 0; i < retval.getColumnCount(); i++) {

                        String columnValue = rows.get(i).length()>maxFMT? rows.get(i).substring(0, maxFMT-3)+"..": rows.get(i);
                        
                        System.out.print(String.format(("%-"+maxFMT+"s"), columnValue));
                    }
                    System.out.println("");
                
                }

            } catch (Exception e) {

                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    
}
