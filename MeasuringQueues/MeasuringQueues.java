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

/** 
 *  Measures the performance of different ways of doing a priority queue of Items
 *  Uses an Item class that has nothing in it but a priority (so it takes
 *   minimal time to construct a new Item).
 *  The Item constructor doesn't need any arguments
 *  Remember that small priority values are the highest priority: 1 is higher priority than 10.
 *  Three different choices to measure:
 *      Using a built-in PriorityQueue
 *      Using an ArrayList, with
 *         - the head of the queue (the next item to poll) head at 0,
 *         - the tail of the queue (where the offered item is put) at the end
 *         - sorting the list every time you add an item.
 *      Using an ArrayList, with
 *         - the head of the queue at end
 *         - the tail of the queue at 0
 *         - sorting the list (in reverse order) every time you add an item.
 *         
 *  Each method should have an items parameter, which is a collection of Items
 *    which should be initially added to the queue (eg  new PriorityQueue<Item>(items); or
 *    new ArrayList<Item>(items))
 *    It should then repeatedly dequeue an item from the queue, and enqueue a new Item(). 
 *    It should do this 100,000 times.
 *    (the number of times can be changed using the textField)
 *  When testing that your methods work correctly, you should have debugging statements
 *    such as UI.println(queue) in the loop to print out the state of the queue.
 *    You should comment them out before doing the measurement
 */

public class MeasuringQueues{

    private static final int TIMES=100000;  //Number of times to repeatedly dequeue and enqueue item 

    /** main method */
    public static void main(String[] arguments){
        MeasuringQueues mq= new MeasuringQueues();
        mq.setupGUI();
    }

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.addButton("Measure PQ", this::measurePQ);
        UI.addButton("Measure", this::measure);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }

    /**
     * Create a priority queue using a PriorityQueue, 
     * adding 1,000 items to the queue. (this needs to vary!!)
     * Measure the cost of adding and removing an item from the queue.
     */
    public void measurePQ(){
        PriorityQueue<Item> queue = new PriorityQueue<Item>();
        for (int i=0; i<10000; i++){
            queue.offer(new Item());
        }

        long start = System.currentTimeMillis();
        for (int i=0; i<TIMES; i++){
            queue.offer(new Item());
            queue.poll();
        }
        long stop = System.currentTimeMillis();

        UI.printf("%.7fms  ", 1.0*(stop-start)/TIMES);
    }

    
    /**
     * Create a priority queue using a PriorityQueue, 
     * adding n items to the queue.
     * Measure the cost of adding and removing an item from the queue.
     */
    public void measurePQ(int n){
        PriorityQueue<Item> queue = new PriorityQueue<Item>();//1
        for (int i=0; i<n; i++){//3+nlogn
            queue.offer(new Item());
        }

        long start = System.currentTimeMillis();//1
        int times = TIMES*10;//1
        for (int i=0; i<times; i++){//3+nlogn
            queue.offer(new Item());
            queue.poll();
        }
        long stop = System.currentTimeMillis();//.1

        UI.printf("%.6fms  ", 1.0*(stop-start)/times);//1
    }

    /**
     * Create a queue using an ArrayList with the head at the front (position 0).
     * Add n items to the list and then sort the list.
     * Then, enqueue (at end) and dequeue (from 0) TIMES times.
     */
    public void measureALFront(int n){
        ArrayList<Item> queue = new ArrayList<Item>();
        for (int i=0; i<n; i++){
            queue.add(new Item());
        }
        Collections.sort(queue);

        long start = System.currentTimeMillis();
        for (int i=0; i<TIMES; i++){
            queue.add(new Item());
            Collections.sort(queue);
            queue.remove(0);
        }
        long stop = System.currentTimeMillis();

        UI.printf("%.6fms  ", 1.0*(stop-start)/TIMES);
    }

    /**
     * Create a queue using an ArrayList with the head at the end.
     * Add n items to the list and then sort the list.
     * Note: Head of queue is at the end of the list, 
     * so we need to sort in the reverse order of Items (so the smallest value comes at the end)
     * Then, enqueue (at end) and dequeue (from 0) TIMES times.
     */
    public void measureALEnd(int n){
        ArrayList<Item> queue = new ArrayList<Item>();
        for (int i=0; i<n; i++){
            queue.add(new Item());
        }
        Collections.sort(queue, Collections.reverseOrder());

        long start = System.currentTimeMillis();
        for (int i=0; i<TIMES; i++){
            queue.add(0,new Item());
            Collections.sort(queue, Collections.reverseOrder());
            queue.remove(queue.size()-1);
        }
        long stop = System.currentTimeMillis();

        UI.printf("%.6fms  ", 1.0*(stop-start)/TIMES);
    }

    /**
     * For a sequence of values of n, from 1000 to 1024000,
     *  - call each of the measuring methods,
     */
    public void measure(){
        UI.printf("Measuring methods using %d repetitions, on queues of size 1000 up to 1,024,000\n",TIMES);
        UI.println("       n          PQ          ALFront       ALEnd");
        for (int n = 1000; n<=1024000; n=n*2){
            UI.printf("%,10d:  ", n);
            measurePQ(n);
            measureALFront(n);
            measureALEnd(n);
            UI.println();
        }
    }

    

}
