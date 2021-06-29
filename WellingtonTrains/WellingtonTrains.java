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
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;

/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *x
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains{
    //Fields to store the collections of Stations and Lines
    /*# YOUR CODE HERE */
    private Map <String, Station> stationMap; 
    private Map <String, TrainLine> trainlineMap ;
    private Set<String> ordername = new TreeSet<String>();
    private List<String> trainlinenam = new ArrayList<String>();
    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;
    private int startTime = 0;         // time for enquiring about
    
    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    /**
     * Load data files
     */
    public void loadData(){
        loadStationData();
        UI.println("Loaded Stations");
        loadTrainLineData();
        UI.println("Loaded Train Lines");
        // The following is only needed for the Completion and Challenge
        loadTrainServicesData();
        UI.println("Loaded Train Services");
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("All Stations",        this::listAllStations);
        UI.addButton("Stations by name",    this::listStationsByName);
        UI.addButton("All Lines",           this::listAllTrainLines);
        UI.addTextField("Station",          (String name) -> {this.stationName=name;});
        UI.addTextField("Train Line",       (String name) -> {this.lineName=name;});
        UI.addTextField("Destination",      (String name) -> {this.destinationName=name;});
        UI.addTextField("Time (24hr)",      (String time) ->
            {try{this.startTime=Integer.parseInt(time);}catch(Exception e){UI.println("Enter four digits");}});
        UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
        UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
        UI.addButton("Next Services",       () -> {findNextServices(this.stationName, this.startTime);});
        UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);

        UI.drawImage("data/geographic-map.png", 0, 0);
        UI.drawString("Click to list closest stations", 2, 12);

    }
    public ArrayList<String> readAllLines(String filename){
        try{
            ArrayList<String> ans = new ArrayList<String>();
            Scanner sc = new Scanner(new File(filename));
            while (sc.hasNext()){
                ans.add(sc.nextLine());
            }
            sc.close();
            return ans;
        }
        catch(IOException e){UI.println("Fail: " + e); return null;}
    }

    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
                int i = 0;
                Map<Double,Station> stations = new TreeMap<Double,Station>();
                UI.clearText();
                UI.drawImage("data/geographic-map.png", 0, 0);
                for(String key:stationMap.keySet()){
                    double distance = Math.sqrt((x-stationMap.get(key).getXCoord())*(x-stationMap.get(key).getXCoord())+(y-stationMap.get(key).getYCoord())*(y-stationMap.get(key).getYCoord()));
                    stations.put(distance,stationMap.get(key));
                }
                for(Double key:stations.keySet()){
                    i++;
                    if(i<11){
                        UI.printf("The station is "+stations.get(key).getName()+"   The distance is %.1f\n",key);
                    }
                }
            }
        
    }

    // Methods for loading data and answering queries

    /*# YOUR CODE HERE */
    public void loadStationData(){
            try{stationMap = new HashMap();
                List<String> numberList = readAllLines ("data/stations.data");
                if(numberList == null){
                    return;
                }
                for(String eachNum : numberList){
                    Scanner sc = new Scanner (eachNum);
                    String name = sc.next();
                    int zone = sc.nextInt();
                    double x = sc.nextDouble();
                    double y = sc.nextDouble();
                    Station oneStation = new Station (name,zone,x,y);
                    stationMap.put(oneStation.getName(),oneStation);
                    ordername.add(name);
                }
            }catch(Exception e){
                UI.println("Exception");
            }
    }
    public void loadTrainLineData(){
            try{trainlineMap = new HashMap();
                List<String> trainLineList = readAllLines("data/train-lines.data");
                if(trainLineList == null){
                    return;
                }
                for(String eachLine : trainLineList){
                    //Scanner sc = new Scanner(eachLine);
                    String name = eachLine;
                    TrainLine thisLine = new TrainLine(eachLine);
                    UI.println(eachLine);
                    trainlinenam.add(eachLine);
                    try{
                    List<String> stationOfLineList = readAllLines("data/"+name+"-stations.data");
                    for(String staname:stationOfLineList){
                        Station st = stationMap.get(staname);
                        thisLine.addStation(st);//每一个Trainline对应一个stations的list
                        
                    }
                }catch(Exception e){UI.println("Exception");}
                    trainlineMap.put(name,thisLine);
                }
            }catch(Exception e){
                UI.println("Exception");
            }
    }
    public void listAllStations(){
        UI.clearText();
        Iterator iter = stationMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            UI.println(entry.getValue());
        }
    }
    public void listStationsByName(){
        UI.clearText();
        Set<String> ordername2 = new TreeSet<String>();
        for(String names :ordername){
            
            for(String key:trainlineMap.keySet()){
                TrainLine trainl = trainlineMap.get(key);
                List <Station> station = trainl.getStations();
                for(Station sta : station){
                    if(ordername2.contains(names)){
                        break;
                    }
                    if(sta.getName().equals(names)){
                        UI.println(sta);
                        ordername2.add(names);
                    }
                }   
            }
        }
    }
    public void listAllTrainLines(){
        UI.clearText();
        Iterator iter = trainlineMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            UI.println(entry.getValue());
        }
    }
    public void listLinesOfStation(String stationName){
        UI.clearText();
        for(String key:trainlineMap.keySet()){
            TrainLine trainl = trainlineMap.get(key);
            List <Station> station = trainl.getStations();
            for(Station sta : station){
                if(sta.getName().equals(stationName)){
                    UI.println(trainl);
                }
            }
        }
    }
    public void listStationsOnLine(String lineName){
        UI.clearText();
        List<Station>linestations= trainlineMap.get(lineName).getStations();
         
        for(Station eachstation: linestations){
            
            UI.println(eachstation);
         }

    }
    public void loadTrainServicesData(){
                try{
                List<String> trainlineList = readAllLines("data/train-lines.data");
                if(trainlineList ==null){
                    return;
                }
                for(String eachLine : trainlineList){
                    String name = eachLine;
                    TrainLine thisLine = new TrainLine(eachLine);
                    UI.println(eachLine);
                    try{List<String> serviceofTrainLine = readAllLines("data/"+name+"-services.data");
                        for(String eachService : serviceofTrainLine){
                            TrainService trainservice = new TrainService(thisLine);
                            Scanner scanner = new Scanner(eachService);
                            while(scanner.hasNextInt()){
                                trainservice.addTime(scanner.nextInt());
                            }
                            scanner.close();
                            thisLine.addTrainService(trainservice);
                        }
                    }catch(Exception e){
                        UI.println("Exception");
                    }
                    try{
                    List<String> stationOfLineList = readAllLines("data/"+name+"-stations.data");
                    for(String staname:stationOfLineList){
                        Station st = stationMap.get(staname);
                        thisLine.addStation(st);
                        
                    }
                }catch(Exception e){UI.println("Exception");}
                    trainlineMap.put(name,thisLine);
                }
            }catch(Exception e){
                     UI.println("Exception");
                    }
    }
    public void findNextServices(String stationName, int starTime){
        UI.clearText();
        Station station = stationMap.get(stationName);
        if(station == null){
            UI.println("wrong station name");
            return;
        }
        Set<TrainLine> trainLineSet = stationMap.get(stationName).getTrainLines();
        for(TrainLine c : trainLineSet){
            List<TrainService> traSerList = c.getTrainServices();
            int location = c.getStations().indexOf(station);
            for(TrainService e : traSerList){
                int time = e.getTimes().get(location);
                if(time>startTime && time>0){
                    UI.println("Next service on"+ c.getName()+"from"+stationName+"is at"+time);
                    break;
                }else{
                    UI.println("no services.");
                }
            }
        }
    }
    public void findTrip(String stationName, String destinationName, int startTime){
        UI.clearText();
        Station startStation = stationMap.get(stationName);
        Station destinationStation = stationMap.get(destinationName);
        if(startStation == null||destinationStation == null){
            UI.println("wrong station name.");
            return;
        }
        boolean isCheck = false;
        Set<TrainLine> trainLineSet = startStation.getTrainLines();
        Set<TrainLine> destinationSet = destinationStation.getTrainLines();
        int fareZone = destinationStation.getZone() - startStation.getZone()+1;
        for(TrainLine c : trainLineSet){
            for(TrainLine d : destinationSet){
                if(c.equals(d)){
                List<Station> stationList = c.getStations();
                    if(stationList.indexOf(startStation) < stationList.indexOf(destinationStation)){
                        List<TrainService> traSerList = c.getTrainServices();
                            for(TrainService e : traSerList){
                                int time1 = e.getTimes().get(stationList.indexOf(startStation));
                                int time2 = e.getTimes().get(c.getStations().indexOf(destinationStation));
                                if(time1>startTime && time1>0){
                                    isCheck = true;
                                    UI.println(e+"leaves"+stationName+" "+time1+",arral"+destinationName+"at"+time2);
                                    break;
                                }
                            }
                    }
                }
            }
        }
    }
    public void checkConnected( String stationName, String destinationName){
        UI.clearText();
        boolean connected = true;
        for(String key:trainlineMap.keySet()){
            TrainLine trainl = trainlineMap.get(key);
            List <Station> station = trainl.getStations();
            for(Station sta : station){
                if(sta.getName().equals(stationName)){
                    for(Station sta2 : station){
                        if(sta2.getName().equals(destinationName)){
                            UI.println(trainl+"is connected.");
                            connected = false;
                        }
                    }
                }
            }
        }
        if(connected){
            UI.println("no trainline from "+stationName+" to "+destinationName);
        }
    }
}
