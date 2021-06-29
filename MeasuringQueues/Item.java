// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a XMUT103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2021T1, Assignment 4
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

public class Item implements Comparable<Item>{

    private static final Random rand = new Random();  // the random number generator

    private int priority;  

    /**
     * Construct a new Item object
     * with random priorities
     */
    public Item(){
        priority = rand.nextInt()>>>12; //random (non-negative) priority, 0 .. 2^20
    }

    /** 
     * Compare this Item with another Item to determine who should 
     *  be dequeued first.
     * An item should be earlier in the ordering if they should be dequeued first.
     */
    public int compareTo(Item other){
        if (this.priority < other.priority) { return -1; }
        if (this.priority > other.priority) { return 1; }
        return 0;
    }

    /** toString */
    public String toString(){
        return "P:"+priority;
    }

}
