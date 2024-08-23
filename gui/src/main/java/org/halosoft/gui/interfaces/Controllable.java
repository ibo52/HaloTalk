/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.halosoft.gui.interfaces;

/**
 *
 * @author ibrahim
 */
public interface Controllable {
    
    /**
     * Some controllers requires to use its Parent's
     * controllers. This methods set Parent's controllers to use later
     * @param controller general Object to cast to desired specific controller 
     */
    public void setParentController(Object controller);
    
    /**
     * Some controllers requires to use or pass its Parent's to other
     * controllers. This methods provides a general way to
     * return Parent's controllers
     * @return general Object to cast to desired specific controller 
     */
    public Object getParentController();
    
    /**
     * General method to remove Controller's
     * root node from its Parent, which means its de-referenced
     * and not in Scene anymore
     */
    public void remove();
    
}
