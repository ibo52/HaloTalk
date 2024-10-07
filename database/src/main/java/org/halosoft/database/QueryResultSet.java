package org.halosoft.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;

public class QueryResultSet{

    private int NUM_COLUMNS=-1;
    private int NUM_RECORDS=-1;

    private final LinkedList<String> columnTypes=new LinkedList<>();

    private final LinkedList<String> columnNames=new LinkedList<>();

    private final LinkedList<LinkedList<String>> rows=new LinkedList<>();

    public QueryResultSet(ResultSet retval){
        
        if( retval!=null ){

            try {
                
                ResultSetMetaData rsmd = retval.getMetaData();

                NUM_COLUMNS = rsmd.getColumnCount();

                for (int i = 1; i <= NUM_COLUMNS; i++) {

                    columnNames.add(rsmd.getColumnName(i));

                    columnTypes.add(rsmd.getColumnTypeName(i));
                }

                while (retval.next()) {

                    this.rows.add(new LinkedList<String>());

                    for (int i = 1; i <= NUM_COLUMNS; i++) {
                        
                        this.rows.getLast().add( retval.getString(i) );
                        
                    }                
                }
                NUM_RECORDS=this.rows.size();

            } catch (SQLException e) {
                e.printStackTrace();
                //System.err.println(String.format("[%s]: %s", getClass().getName(), e.getMessage()));
            }
        }

    }

    public QueryResultSet(int retval){
 
                NUM_COLUMNS = 1;
                NUM_RECORDS = 1;

                this.rows.add(new LinkedList<String>());

                columnNames.add("database return value");
                columnTypes.add("int");

                this.rows.getLast().add(Integer.toString(retval));


    }

    public int getColumnCount(){
        return NUM_COLUMNS;
    }

    public int getRecordCount(){
        return NUM_RECORDS;
    }

    public boolean hasNext(){
        return rows.size()>0;
    }

    /*
     * Returns the next record from RQueryesultSet
     * Note: Record will be removed from QueryResultSet 
     */
    public LinkedList<String> getNextRecord(){
        
        return rows.removeFirst();
    }

    public LinkedList<String> getLastRecord(){
        
        return rows.removeLast();
    }

    public String getNextRecord(String columnName){

        int idx=-1;
        for (int i = 0; i < columnNames.size(); i++) {
            
            if(this.columnNames.get(i).equals(columnName)){
                idx=i;
                break;
            }
        }

        return idx<0? "":rows.removeFirst().get(idx);
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
        return columnNames.get(index);
    }

    public int indexOf(String columnName){

        return columnNames.indexOf(columnName);
    }

}
