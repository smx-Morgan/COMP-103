// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a XMUT103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2021T1, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;
/** Pencil   */
public class Pencil{
    private double lastX;
    private double lastY;
    private Stack <List <double[]>> undo = new Stack<>();
    private Stack <List <double[]>> redo = new Stack<>();
    private double wide = 1;
    List <double[]> list = new ArrayList <>();
    private boolean flag = false ;
    private boolean color = true ;
    
    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addButton("Quit", UI::quit);
        UI.addButton("undo",this::undo);
        UI.addButton("redo",this::redo);
        UI.addButton("change color",this::colorChange);
        UI.addSlider("wide",1,10,5,this::setWide);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    /**
     * Respond to mouse events
     */
    
    public void doMouse(String action, double x, double y) {
        
        if (action.equals("pressed")){
            lastX = x;
            lastY = y;
            list = new ArrayList<>();
            list.add(new double []{ x, y});
        }
        else if (action.equals("dragged")){
            UI.drawLine(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
            list.add(new double []{ x, y});
        }
        else if (action.equals("released")){
            UI.setLineWidth(wide);
            UI.drawLine(lastX, lastY, x, y);
            list.add(new double []{ x, y});
            undo.add(list);
        } if(flag){
                redo = new Stack<>();
                flag = false;
            }
    }
    public void undo(){
        if(!undo.isEmpty()){
            List <double[]> tmp = undo.pop();
            
            for(int i = 1; i < tmp.size(); i++){
                UI.setLineWidth(wide);
                UI.eraseLine(tmp.get(i-1)[0],tmp.get(i-1)[1],tmp.get(i)[0],tmp.get(i)[1]);
            }
            redo.push(tmp);
        }
    }
    public void redo(){
        if(!redo.isEmpty()){
            List<double[]> tmp = redo.pop();
            for(int i=1;i<tmp.size();i++){
                UI.setLineWidth(wide);
                UI.drawLine(tmp.get(i-1)[0],tmp.get(i-1)[1],tmp.get(i)[0],tmp.get(i)[1]);
            }
            flag = true;
        }
    }
    public void colorChange(){
        if(color){
           UI.setColor(Color.red); 
        }
        if(!color){
            UI.setColor(Color.black);
        }
    }
    public void setWide(double wide){
        this.wide = wide;
    }
    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }

}
