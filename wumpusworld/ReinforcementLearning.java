/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpusworld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class ReinforcementLearning {
    
    private final double ALPHA = 0.2;
    private final double GAMMA = 0.95;
    private final double MAX_ER = 1;
    private final double MIN_ER = 0.01;
    private final double DECAY = 0.005;
            
    private final int EPOQUES = 200 , MAX_STEPS = 100;
    private String S_PATH = "States.txt";
    private String Q_PATH = "QTable.txt";
    private World w, startWorld;  //Two variables needed for the restoration in the simulation
    private int[] actions = {w.DIR_UP,w.DIR_RIGHT,w.DIR_DOWN,w.DIR_LEFT, 
                   w.DIR_UP + 4,w.DIR_RIGHT +4,w.DIR_DOWN +4,w.DIR_LEFT+4};
                    //up, right, left, down + Move, same + shoot
    
    private int xPlayer,yPlayer,size;
    private double explorationRate;
    int reward;
    private Random random;
    private ArrayList<State> allStates; 
    private double[][] qtable;
    
    final int REWARD_MOVE = -10;
    final int REWARD_SHOOT = -20;
    final int REWARD_WUMPUS_KILLED = 20;
    final int REWARD_PIT = -50;
    final int REWARD_WUMPUS = -500;
    final int REWARD_INVALID = -25; //bumb into wall/shoot a non present arrow
    final int REWARD_GOLD = 500;
    final int REWARD_EXPLORE = 20;
    
    public ReinforcementLearning(){
        allStates = readSavedStates();
    }
    
    public ReinforcementLearning(World world){
        this.startWorld = world;
        this.w = world.cloneWorld();
        this.size = w.getSize();
        this.xPlayer = w.getPlayerX();
        this.yPlayer = w.getPlayerY();
        this.explorationRate = 1;
        random = new Random();
        
        //allStates = readSavedStates();
    }
    
    public static void main(String[] args){
        ReinforcementLearning rl = new ReinforcementLearning();
        System.out.println("Reading...");
        ArrayList<State> states = rl.readSavedStates();
        
        for (int i = 0 ; i< states.size(); i++){
            System.out.println(i + " " + states.get(i));
        }
    }
 
    
    public boolean saveStatesToFile(ArrayList<State> states){
        boolean done = false;
        int size = states.size();
        PrintWriter printer = null;
        try{
            printer = new PrintWriter(new BufferedWriter(new FileWriter(S_PATH)));
            for(int i = 0; i<size; i++){
                printer.println(states.get(i).toString());
            }
            printer.close();
            done = true;
        } catch (IOException iox){
            System.out.println("Error in creating file");
            printer.close();
            return false;
        }
        return done;
    }
    
    public State createFromDescription(String s){
        Location loc; State state = null;
        boolean[] percepts = new boolean[8];
        int[] neighbours = new int[4];
        String sep = ";";
        String[] tokens = s.split(sep);
        loc = new Location(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]));
        String[] bools = tokens[2].split(",");
        for (int i = 0; i<8; i++){
            percepts[i] = Boolean.parseBoolean(bools[i]);
        }
        String[] n = tokens[3].split(",");
        for (int i = 0; i<4;i++){
            neighbours[i] = Integer.parseInt(n[i].replaceAll("\\s", ""));
        }
        try {
            state = new State(loc,percepts,neighbours);
        } catch (Exception ex) {
            Logger.getLogger(ReinforcementLearning.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }
    
    public ArrayList<State> readSavedStates(){
        ArrayList<State> states = new ArrayList<>();
        BufferedReader reader = null;
        try {
           reader = new BufferedReader(new FileReader(S_PATH));
           String line = reader.readLine();
           while ( line != null){
               State s = createFromDescription(line);
               states.add(s);
           }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
            try {
                reader.close();
            } catch (IOException ex1) {
                Logger.getLogger(ReinforcementLearning.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return states;
        } catch (IOException ex) {
            try {
                reader.close();
            } catch (IOException ex1) {
                Logger.getLogger(ReinforcementLearning.class.getName()).log(Level.SEVERE, null, ex1);
            }
            ex.printStackTrace();
            Logger.getLogger(ReinforcementLearning.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(ReinforcementLearning.class.getName()).log(Level.SEVERE, null, ex);
        }
        return states;
    }
     
    
}
