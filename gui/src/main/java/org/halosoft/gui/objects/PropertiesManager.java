/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
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
    
    public static void savePropertiesToFile(Properties props
            ,String PackageRelativePathToFile,String... comments ) {
        
        if (props==null | props.isEmpty()) {
            return;
        }
        
        if (PackageRelativePathToFile==null) {
            PackageRelativePathToFile="settings/unknown.properties";
            
        }else if( !PackageRelativePathToFile.contains(".properties") ){
            PackageRelativePathToFile+=".properties";
        }
        
        try {
                File fosPath=new File( Paths.get(App.class.
                        getResource(PackageRelativePathToFile
                        ).toURI()).toString());
                
                BufferedWriter fos=Files.newBufferedWriter(fosPath.toPath(), 
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND);
                
                StringBuilder comment=new StringBuilder("\nAuto edited properties");
                for(String c:comments){
                    comment.append("#"+c);
                }
                props.store(fos, comment.toString());
                
                fos.flush();
            } catch (URISyntaxException ex1) {
                App.logger.log(Level.WARNING, "URI parameter"
                    + " for Path is wrong",ex1);
                
            } catch (IOException ex1) {
                App.logger.log(Level.WARNING, "Error when creating "
                    + "BufferedWriter with Files",ex1);
            }   
    }
}
