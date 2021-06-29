// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a XMUT103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2021T1, Assignment 3
  * Name:songmengxin
 * Username:1912409230_xmut
 * ID:1912409230
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/**
 * Simulation of a Hospital ER
 * 
 * The hospital has a collection of Departments, including the ER department, each of which has
 *  and a treatment room.
 * 
 * When patients arrive at the hospital, they are immediately assessed by the
 *  triage team who determine the priority of the patient and (unrealistically) a sequence of treatments 
 *  that the patient will need.
 *
 * The simulation should move patients through the departments for each of the required treatments,
 * finally discharging patients when they have completed their final treatment.
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCompl{

    // Copy the code from HospitalERCore and then modify/extend to handle multiple departments

    /*# YOUR CODE HERE */
    
    private static final int MAX_PATIENTS = 5;   // max number of patients currently being treated
    

    private Map<String,Department> department= new HashMap<String,Department>();
    // fields for the statistics
    /*# YOUR CODE HERE */
    private int patientNum = 0;
    private double Times = 0;
    private int patientNumpri = 0;
    private double Timespri = 0;
    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick
    private boolean pri = false;
    // fields controlling the probabilities.
    private int arrivalInterval = 5;   // new patient every 5 ticks, on average
    private double probPri1 = 0.1; // 10% priority 1 patients
    private double probPri2 = 0.2; // 20% priority 2 patients
    private Random random = new Random();  // The random number generator.

    /**
     * Construct a new HospitalERCore object, setting up the GUI, and resetting
     */
    
    public static void main(String[] arguments){
        HospitalERCompl er = new HospitalERCompl();
        er.setupGUI();
        er.reset(false);   // initialise with an ordinary queue.
    }        

    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI(){
        UI.addButton("Reset (Queue)", () -> {this.reset(false); });
        UI.addButton("Reset (Pri Queue)", () -> {this.reset(true);});
        UI.addButton("Start", ()->{if (!running){ run(); }});   //don't start if already running!
        UI.addButton("Pause & Report", ()->{running=false;});
        UI.addSlider("Speed", 1, 400, (401-delay),
            (double val)-> {delay = (int)(401-val);});
        UI.addSlider("Av arrival interval", 1, 50, arrivalInterval,
            (double val)-> {arrivalInterval = (int)val;});
        UI.addSlider("Prob of Pri 1", 1, 100, probPri1*100,
            (double val)-> {probPri1 = val/100;});
        UI.addSlider("Prob of Pri 2", 1, 100, probPri2*100,
            (double val)-> {probPri2 = Math.min(val/100,1-probPri1);});
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,600);
        UI.setDivider(0.5);
    }

    /**
     * Reset the simulation:
     *  stop any running simulation,
     *  reset the waiting and treatment rooms
     *  reset the statistics.
     */
    public void reset(boolean usePriorityQueue){
        running=false;
        UI.sleep(2*delay);  // to make sure that any running simulation has stopped
        time = 0;           // set the "tick" to zero.

        // reset the waiting room, the treatment room, and the statistics.
        /*# YOUR CODE HERE */
        running=false;
        department= new HashMap<String,Department>();
        department.put("MRI",new Department("MRI",MAX_PATIENTS,usePriorityQueue));
        department.put("Surgery",new Department("Surgery",MAX_PATIENTS,usePriorityQueue));
        department.put("X-ray",new Department("X-ray",MAX_PATIENTS,usePriorityQueue));
        department.put("Ultrasound",new Department("Ultrasound",MAX_PATIENTS,usePriorityQueue));
        department.put("ER",new Department("ER",MAX_PATIENTS,usePriorityQueue));
        //reset.
        
        
        this.patientNum = 0;
        this.Times = 0;
        UI.clearGraphics();
        UI.clearText();
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        if (running) { return; } // don't start simulation if already running one!
        running = true;
        Object[][] names = new Object[][]{{"ER",5},{"X-ray",2}};
        for(int i=0;i<names.length;i++){
            Department d = new Department((String)names[i][0],(Integer)names[i][1],false);
        department.put((String)names[i][0],d);
        }
        while (running){         // each time step, check whether the simulation should pause.

            // Hint: if you are stepping through a set, you can't remove
            //   items from the set inside the loop!
            //   If you need to remove items, you can add the items to a
            //   temporary list, and after the loop is done, remove all 
            //   the items on the temporary list from the set.

            /*# YOUR CODE HERE */
            time = time + delay;
           Queue<Patient> curwaitingRoom = new PriorityQueue<Patient>();
           /*if(pri){
            Queue<Patient> curwaitingRoom = new PriorityQueue<Patient>();
            }else{
            Queue<Patient> curwaitingRoom = new ArrayDeque<Patient>();
            }*/
           Set<Patient> curtreatmentRoom = new HashSet<Patient>();
           Department curDepartment;
            for(String key:this.department.keySet()){
            curDepartment = this.department.get(key);
               curwaitingRoom = curDepartment.getWaitingRoom();
               curtreatmentRoom = curDepartment.getTreatmentRoom();
               Set<Patient> temp = new HashSet<Patient>();
                for(Patient pa: curtreatmentRoom){
                temp.add(pa);
            }
                for(Patient pa: temp){
                   if(pa.noMoreTreatments()){
                       curDepartment.curePatient(pa);
                    }
                   else {
                       if(pa.completedCurrentTreatment()){
                    curtreatmentRoom.remove(pa);
                    pa.incrementTreatmentNumber();
                    patientNum++;
                    if(pa.getPriority()==1){
                        patientNumpri++;
                        Timespri+=pa.getWaitingTime();
                    }
                    if(!pa.noMoreTreatments()){this.department.get(pa.getCurrentTreatment()).addPatient(pa);}
                    int time = pa.getWaitingTime();
                    Times+=time;
                }else{
                    pa.advanceTreatmentByTick();
                }
            }
             for(Patient p: curwaitingRoom){
                p.waitForATick();
            }
            if(curtreatmentRoom.size()< curDepartment.getMaxPatients()){
                curDepartment.removeFromwaitingRoom();
            }
            }
        }
            
           
            
            // Gets any new patient that has arrived and adds them to the waiting room
            if (time==1 || Math.random()<1.0/arrivalInterval){
                Patient newPatient = new Patient(time, randomPriority());
                UI.println(time+ ": Arrived: "+newPatient);
                this.department.get(newPatient.getCurrentTreatment()).addPatient(newPatient);
            }
            redraw();
            UI.sleep(delay);
        }
        // paused, so report current statistics
        
        reportStatistics();
    }

    // Additional methods used by run() (You can define more of your own)

    /**
     * Report summary statistics about all the patients that have been discharged.
     * (Doesn't include information about the patients currently waiting or being treated)
     * The run method should have been recording various statistics during the simulation.
     */
    public void reportStatistics(){
        /*# YOUR CODE HERE */
        UI.println("The number of patients treated is "+ this.patientNum);
        double averagetime = patientNum/Times;
        UI.println("The average waiting time of the patients is "+ averagetime);
        UI.println("The total number of priority 1 patients treated is " + this.patientNumpri);
        double Priaveragetime = patientNumpri/Timespri;
        UI.println("The average waiting time of the priority 1 patients is "+ Priaveragetime);

    }


    // HELPER METHODS FOR THE SIMULATION AND VISUALISATION
    /**
     * Redraws all the departments
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);

        // Draw the treatment room and the waiting room:
            double y = 80;
            UI.setFontSize(14);
        
            double x = 10;
       
           
           for(String key:this.department.keySet()){
               
               this.department.get(key).redraw(y);
               
               y+=80;
               /*curwaitingRoom = curDepartment.getWaitingRoom();
               curtreatmentRoom = curDepartment.getTreatmentRoom();
               String name = curDepartment.getName();
               UI.drawString(name, 0, y-35-30*j);
               UI.drawRect(x-curDepartment.getMaxPatients(), y-30-30*j, MAX_PATIENTS*10, 30);
               UI.drawLine(0,y+2,400, y+2);
               for(Patient p : curtreatmentRoom){
                   p.redraw(x, y-30*j);
                   x += 10;
                }
                x = 200;
                List <Patient> waiting = new ArrayList<Patient>();
                Queue<Patient> temp = new ArrayDeque<Patient>();
                for(int i =0; i < curwaitingRoom.size(); i++){
                    Patient p = curwaitingRoom.poll();
                    waiting.add(p);
                    temp.offer(p);
                }
                for(Patient p : temp){
                    curwaitingRoom.offer(p);
                }
                for(Patient p : waiting){
                    p.redraw(x, y-30*j);
                    x += 10;
                }
                j++;*/
               
            }
        
        
        
    }

    /** 
     * Returns a random priority 1 - 3
     * Probability of a priority 1 patient should be probPri1
     * Probability of a priority 2 patient should be probPri2
     * Probability of a priority 3 patient should be (1-probPri1-probPri2)
     */
    private int randomPriority(){
        double rnd = random.nextDouble();
        if (rnd < probPri1) {return 1;}
        if (rnd < (probPri1 + probPri2) ) {return 2;}
        return 3;
    }
}

