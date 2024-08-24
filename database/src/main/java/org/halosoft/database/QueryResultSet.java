package org.halosoft.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;

public class QueryResultSet{

    private int NUM_COLUMNS=-1;
    private int NUM_RECORDS=-1;

    private final LinkedList<String> columnTypes=new LinkedList<>();

    private final LinkedList<String> metadata=new LinkedList<>();

    private LinkedList<LinkedList<String>> list=new LinkedList<>();

    public QueryResultSet(ResultSet retval){
        
        if( retval!=null ){

            try {
                
                ResultSetMetaData rsmd = retval.getMetaData();

                NUM_COLUMNS = rsmd.getColumnCount();

                for (int i = 1; i <= NUM_COLUMNS; i++) {

                    metadata.add(rsmd.getColumnName(i));

                    columnTypes.add(rsmd.getColumnTypeName(i));
                }

                while (retval.next()) {

                    this.list.add(new LinkedList<String>());

                    for (int i = 1; i <= NUM_COLUMNS; i++) {
                        
                        this.list.getLast().add( retval.getString(i) );
                        
                    }                
                }
                NUM_RECORDS=this.list.size();

            } catch (SQLException e) {
                e.printStackTrace();
                //System.err.println(String.format("[%s]: %s", getClass().getName(), e.getMessage()));
            }
        }

    }

    public QueryResultSet(int retval){
 
                NUM_COLUMNS = 1;
                NUM_RECORDS=1;

                this.list.add(new LinkedList<String>());

                metadata.add("database update retval");
                columnTypes.add("int");

                this.list.getLast().add(Integer.toString(retval));


    }

    public int getColumnCount(){
        return NUM_COLUMNS;
    }

    public int getRecordCount(){
        return NUM_RECORDS;
    }

    public boolean hasNext(){
        return list.size()>0;
    }

    /*
     * Returns the next record from RQueryesultSet
     * Note: Record will be removed from QueryResultSet 
     */
    public LinkedList<String> getNextRecord(){
        
        return list.removeFirst();
    }

    public String getColumnTypeName(int index){

        if(index<0 || index> NUM_COLUMNS){
            throw new IndexOutOfBoundsException("No such index");
        }
        return columnTypes.get(index);
    }

    public String getColumnName(int index){

        if(index<0 || index> NUM_COLUMNS){
            throw new IndexOutOfBoundsException("No such index");
        }
        return metadata.get(index);
    }

}
