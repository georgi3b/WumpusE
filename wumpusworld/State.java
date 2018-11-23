/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpusworld;

import java.util.Arrays;

/**
 *
 * @author Georgiana
 */
public class State {
    
    //array of 4 integers, position in array = direction
    public static final int N_SAFE = 0;
    public static final int N_UNKNOWN = 1;
    public static final int N_WALL = 2;
    public static final int N_PIT = 3;
    public static final int N_WUMPUS = 4;
    
    private Location loc;
    boolean breeze, stench, glitter, pit, wumpus, unknown, wAlive, hasArrow;
    boolean[] percepts;
    int[] neighbours;
    
    public State(Location loc, boolean[] percepts, int[] neighbours) throws Exception{
        this.loc = loc;
        if (percepts.length != 8) {
            throw new Exception("Percepts length must be 8");
        }
        this.percepts = percepts;
        breeze = percepts[0];
        stench = percepts[1];
        glitter = percepts[2];
        pit = percepts[3];
        wumpus = percepts[4];
        unknown = percepts[5];
        wAlive = percepts[6];
        hasArrow = percepts[7];
        this.neighbours = neighbours;
    }
    
    public Location getLocation(){
        return this.loc;
    }
    
    public boolean equals(Object o){
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        State other = (State) o;
        
        return Arrays.equals(percepts, other.percepts)
                && Arrays.equals(neighbours, other.neighbours) &&
                loc.equals(other.loc);
    }
    
    public int hashCode(){
        int hash = 5;
        hash = 49*hash + (this.breeze ? 1:0 );
        hash = 49*hash + (this.stench ? 1:0 );
        hash = 49*hash + (this.glitter ? 1:0 );
        hash = 49*hash + (this.pit ? 1:0 );
        hash = 49*hash + (this.wumpus ? 1:0 );
        hash = 49*hash + (this.unknown ? 1:0 );
        hash = 49*hash + (this.wAlive ? 1:0 );
        hash = 49*hash + (this.hasArrow ? 1:0 );
        hash = 49*hash + neighbours[0];
        hash = 48*hash + neighbours[1];
        hash = 47*hash + neighbours[2];
        hash = 46*hash + neighbours[3]; 
        return hash;
    }
    
    public String toString(){
        String sep = ";";
        String res = loc.x + sep + loc.y + sep;
        String s = Arrays.toString(percepts);
        res += s.substring(1,s.length()-1) + sep;
        String n = Arrays.toString(neighbours);
        res += n.substring(1,n.length()-1);
        return res;
    }
    
}
