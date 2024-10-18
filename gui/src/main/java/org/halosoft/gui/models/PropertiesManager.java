/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Level;

import org.halosoft.gui.App;

/**
 *
 * @author ibrahim
 */
public class PropertiesManager {

    public static final Path DEFAULT_SAVEPATH=Paths.get(System.getProperty("user.home"), ".halotalk", "properties" );
    
    public static Properties readFromFile(String packageRelativePath) {
        
        Properties props=new Properties();
        
        try {
            props.load(App.class
                    .getResourceAsStream(packageRelativePath));
            return props;
            
        } catch (IOException ex) {
            App.logger.log(Level.FINEST, "Error while loading properties"
                    + " file for given path:"+packageRelativePath
                    +"\nWill be return empty",ex);
            return props;
        }
    }

    /**
     * Save properties to default path
     * @param props properties
     * @param savePath desired subpath of file to be saved
     * @param comments comments to describe properties etc.
     */
    public static void saveToFile(Properties props
            ,String savePath, String... comments ) {
        
        if (props==null | props.isEmpty()) {
            return;
        }
        
        if( !savePath.contains(".properties") ){
            savePath+=".properties";
        }
        
        try {
                Path fosPath=PropertiesManager.DEFAULT_SAVEPATH.resolve(savePath);

                //create path if not exists
                Files.createDirectories( fosPath.getParent() );

                BufferedWriter fos=Files.newBufferedWriter(fosPath, 
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                
                StringBuilder comment=new StringBuilder("\n#Auto edited properties");
                
                for(String c:comments){
                    comment.append("#").append(c).append("\n");
                }

                props.store(fos, comment.toString());
                
                fos.flush();
            } catch (IOException ex1) {
                App.logger.log(Level.WARNING, "Error when creating "
                    + "BufferedWriter with Files",ex1);
            }   
    }

    /**
     * Save properties to given path
     * @param props properties
     * @param savePath desired destination of file to be saved
     * @param comments comments to describe properties etc.
     */
    public static void saveToFile(Properties props
    ,Path savePath, String... comments ) {

        if (props==null | props.isEmpty()) {
            return;
        }

        if( !savePath.toString().contains(".properties") ){
            savePath=savePath.getParent().resolve( savePath.getFileName()+".properties");
        }

        try {
                
                BufferedWriter fos=Files.newBufferedWriter(savePath, 
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND);
                
                StringBuilder comment=new StringBuilder("\n#Auto edited properties");

                for(String c:comments){
                    comment.append("#").append(c).append("\n");
                }
                props.store(fos, comment.toString());
                
                fos.flush();

            }catch (IOException ex1) {
                App.logger.log(Level.WARNING, "Error when creating "
                    + "BufferedWriter with Files",ex1);
            }   
        }

}
