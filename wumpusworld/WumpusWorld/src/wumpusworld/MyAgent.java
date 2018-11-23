package wumpusworld;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private World w;
    int rnd;
    private float[][] probabilitiesWum;
    private float[][] probabilitiesP; 
    private ArrayList<Location> unknownNeighbours;
    
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;  
        probabilitiesWum = new float [4][4];
        probabilitiesP = new float[4][4];
        for (int i = 0; i< 4; i++){
            for (int j=0;j<4;j++){
                //probabilitiesWum unknown
                probabilitiesWum[i][j] = -1000;
                probabilitiesP[i][j]=-1000;
            }
        }
        unknownNeighbours = new ArrayList<Location>();
    }
   
            
    /**
     * Asks your solver agent to execute an action.
     */

    public void doAction()
    {
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
        
        System.out.println("Agent is doing an action.");
        
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY))
        {
            w.doAction(World.A_GRAB);
            return;
        }
        
        //Basic action:
        //We are in a pit. Climb up.
        if (w.isInPit())
        {
            w.doAction(World.A_CLIMB);
            return;
        }
        
        //Test the environment
        if (w.hasBreeze(cX, cY))
        {
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(cX, cY))
        {
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(cX, cY))
        {
            System.out.println("I am in a Pit");
        }
        if (w.getDirection() == World.DIR_RIGHT)
        {
            System.out.println("I am facing Right");
        }
        if (w.getDirection() == World.DIR_LEFT)
        {
            System.out.println("I am facing Left");
        }
        if (w.getDirection() == World.DIR_UP)
        {
            System.out.println("I am facing Up");
        }
        if (w.getDirection() == World.DIR_DOWN)
        {
            System.out.println("I am facing Down");
        }
        
        
        Location start = new Location(cX,cY);
        /*
        Location end = new Location(2,1);
        ArrayList<Location> visited = new ArrayList<Location>();
        visited.add(start);
        while (!start.equals(end)){
            Location next = searchPath(start,end,visited);
            int i = -1;
            if (next.equals(start.neighbourUp())){
                i = 0;
            } else if (next.equals(start.neighbourRight())){
                i = 1;
            } else if (next.equals(start.neighbourDown())){
                i = 2;
            } else if (next.equals(start.neighbourLeft())){
                i = 3;
            }
            
            System.out.println("Next location is " + next);
            visited.add(next);
           
            if (w.getDirection() == i){
                 w.doAction(w.A_MOVE); //Move forward
            }
            if (((w.getDirection()-1) == i) ||
                    ((w.getDirection() == 0) && (i == 3))){
                w.doAction(w.A_TURN_LEFT); //Turn left and move
                 w.doAction(w.A_MOVE);
            }
            if ((((w.getDirection())+1)%4) == i){
                w.doAction(w.A_TURN_RIGHT);  //Turn right and move
                 w.doAction(w.A_MOVE);
            }
            if ((((w.getDirection())+2)%4) == i){
                w.doAction(w.A_TURN_RIGHT);  //Turn 180 and move
                w.doAction(w.A_TURN_RIGHT);
                w.doAction(w.A_MOVE);
            }
            
            start = next;
        
        }
        */
        checkLocation(cX,cY);
        System.out.println("Probabilities wumpus:");
        for (int i = 0; i<4;i++){
                for (int j = 0; j<4; j++){
                    System.out.print(probabilitiesWum[i][j]+"\t");
                }
                System.out.println();
        }
       System.out.println("Probabilities breeze:");

        for (int i = 0; i<4;i++){
                for (int j = 0; j<4; j++){
                    System.out.print(probabilitiesP[i][j]+"\t");
                }
               System.out.println();
        }
        
    }    
    
     /**
     * Genertes a random instruction for the Agent.
     */
    
    
    public void checkPercept(boolean stench,Location curr, ArrayList<Location> unknownNeighbours){
                for(Location l: unknownNeighbours){
                    
                if(stench){
                    if(probabilitiesWum[l.x-1][l.y-1] ==-1000){
                        probabilitiesWum[l.x-1][l.y-1]=0;
                    }
                }
                else{
                    if(probabilitiesP[l.x-1][l.y-1] ==-1000){
                        probabilitiesP[l.x-1][l.y-1]=0;
                    }
                }
                    System.out.println("Incrementing probability of wumpus.");
                    ArrayList<Location> nofn = new ArrayList<>();
                    Location pUP = l.neighbourUp();
                    Location pRIGHT = l.neighbourRight();
                    Location pDOWN = l.neighbourDown();
                    Location pLEFT = l.neighbourLeft();
                    if (!curr.equals(pUP)) nofn.add(pUP);
                    if (!curr.equals(pRIGHT)) nofn.add(pRIGHT);
                    if (!curr.equals(pDOWN))nofn.add(pDOWN); 
                    if (!curr.equals(pLEFT))nofn.add(pLEFT);
                    boolean excluded = false;
                    for(Location n : nofn){
                        if(stench){
                            if(w.isValidPosition(n.x,n.y) && !w.isUnknown(n.x, n.y)
                                && !w.hasStench(n.x, n.y)){
                                excluded = true;
                                break;
                            }
                        }
                        else{
                            if(w.isValidPosition(n.x,n.y) && !w.isUnknown(n.x, n.y)
                                && !w.hasBreeze(n.x, n.y)){
                                excluded = true;
                                break;
                            }
                        }
                        
                    }
                    if(!excluded)
                    {
                        if(stench)
                            probabilitiesWum[l.x-1][l.y-1] +=1;
                        else
                            probabilitiesP[l.x-1][l.y-1] +=1;
                    }
                    
            }
        
    }
    
    public void checkLocation(int x,int y){
        
        Location curr = new Location(x,y);
        
        if (!unknownNeighbours.contains(curr)) {
            unknownNeighbours.add(curr);
        }
        
        ArrayList<Location> unknownNofCurr = new ArrayList<>();
        boolean removed = false;
        if (unknownNeighbours.contains(curr)) {
            unknownNeighbours.remove(curr);
            removed = true;
        }
        
        Location up = curr.neighbourUp();
        if(w.isValidPosition(up.x, up.y) && w.isUnknown(up.x, up.y)){
            unknownNofCurr.add(up);
            if (!unknownNeighbours.contains(up)) unknownNeighbours.add(up);
        }
        Location right = curr.neighbourRight();
        if(w.isValidPosition(right.x, right.y) && w.isUnknown(right.x, right.y)){
            unknownNofCurr.add(right);
            if (!unknownNeighbours.contains(right)) unknownNeighbours.add(right);
        }
        Location down = curr.neighbourDown();
        if(w.isValidPosition(down.x, down.y) && w.isUnknown(down.x, down.y)){
            unknownNofCurr.add(down);
            if (!unknownNeighbours.contains(down)) unknownNeighbours.add(down);
        }
        Location left = curr.neighbourLeft();
        if(w.isValidPosition(left.x, left.y) && w.isUnknown(left.x, left.y)){
            unknownNofCurr.add(left);
            if (!unknownNeighbours.contains(left)) unknownNeighbours.add(left);
        }
        if (removed){
            
            if (!w.hasWumpus(x,y)){
                //safe cell
                System.out.println("Location doesn't have wumpus.");
                probabilitiesWum[x-1][y-1] = 0;
                
            }
            
            if (!w.hasPit(x, y)){
                System.out.println("Location doesn't have pit.");

                probabilitiesP[x-1][y-1] = 0;
            }
            
            if(w.hasStench(x, y)){
                System.out.println("Location has stench.");

                checkPercept(true, curr, unknownNofCurr);
                 
                float max=-1000;
                for (int i = 0; i<4;i++){
                    for (int j = 0; j<4; j++){
                        if(probabilitiesWum[i][j]>max){
                            max=probabilitiesWum[i][j];
                            System.out.println("Max is = " + max);
                        }
                    }
                }

                for (int i = 0; i<4;i++){
                    for (int j = 0; j<4; j++){
                        float p =probabilitiesWum[i][j];
                        if(p!=-1000 && p!=max){
                           probabilitiesWum[i][j]=0;

                        }
                    }
                }
            }                    

            if (w.hasBreeze(x,y)){
                System.out.println("Location has breeze.");

                checkPercept(false, curr, unknownNofCurr);
            } 

            if (w.hasPit(x, y)){

            }

            
            
            if (!w.hasStench(x,y)){
                noDangerUpdate(true,curr);
            }
            
            if (!w.hasBreeze(x, y)){
                noDangerUpdate(false,curr);
            }
        }
    }
    
    public void noDangerUpdate(boolean stench, Location curr){
                Location u = curr.neighbourUp();
                Location r = curr.neighbourRight();
                Location d = curr.neighbourDown();
                Location l = curr.neighbourLeft();
                ArrayList<Location> n = new ArrayList<>();
                n.add(u); n.add(r); n.add(d); n.add(l);
                ArrayList<Location> d_n = new ArrayList<Location>();
                
                for (Location loc:n){
                    float pr = 0;
                    if(w.isValidPosition(loc.x,loc.y)){
                        if (stench)
                            pr = probabilitiesWum[loc.x-1][loc.y-1];
                        else pr = probabilitiesP[loc.x-1][loc.y-1];

                        if (pr>0){
                            d_n.add(loc);
                        } 
                    }
                    }
                for (Location now :d_n){
                    //call the method which updates the wumpus probability in this cell
                    //(to 0) and in its neighbours of neighbours(by redisrtibuting this
                    //probability among them.
                    float pr;
                    if (stench){
                        pr = probabilitiesWum[now.x-1][now.y-1];
                        updateProbabilities(now, pr, true);

                    }
                    else{
                        pr = probabilitiesP[now.x-1][now.y-1];
                        updateProbabilities(now, pr, false);
                    }
                    
                }
    }
    
    public void updateProbabilities(Location noDanger, float p,boolean stench){
        //if stench, we are considering all the probabilities in the wumpus matrix; else we
        //consider the Pit matrix.
                Location u,r,d,l;
                u = noDanger.neighbourUp();
                r = noDanger.neighbourRight();
                d = noDanger.neighbourDown();
                l = noDanger.neighbourLeft();
                ArrayList<Location> s = new ArrayList<>();
                if (stench){
                    if (w.hasStench(u.x, u.y)) s.add(u);
                    if (w.hasStench(r.x, r.y)) s.add(r);
                    if (w.hasStench(d.x, d.y)) s.add(d);
                    if (w.hasStench(l.x, l.y)) s.add(l);
                } else {
                    if (w.hasBreeze(u.x, u.y)) s.add(u);
                    if (w.hasBreeze(r.x, r.y)) s.add(r);
                    if (w.hasBreeze(d.x, d.y)) s.add(d);
                    if (w.hasBreeze(l.x, l.y)) s.add(l);
                }
                int length = s.size();
                float split = p/length;
                //System.out.println("Resetting " +  noDanger.x + " " + noDanger.y + " to 0.Split = " + split);
                if (stench) probabilitiesWum[noDanger.x-1][noDanger.y-1] = 0;
                else probabilitiesP[noDanger.x-1][noDanger.y-1] = 0;
                
                for (Location n:s){
                    
                    Location s_u,s_r,s_d,s_l;
                    s_u = n.neighbourUp();
                    s_r = n.neighbourRight();
                    s_d = n.neighbourDown();
                    s_l = n.neighbourLeft();
                    ArrayList<Location> wu = new ArrayList<Location>();
                    if (stench){
                        if (w.isValidPosition(s_u.x,s_u.y) && probabilitiesWum[s_u.x-1][s_u.y-1] > 0) wu.add(s_u);
                        //System.out.println("Probability in " + s_u.x + " " + s_u.y + " is = " + probabilitiesWum[s_u.x-1][s_u.y-1]);
                        if (w.isValidPosition(s_r.x,s_r.y) && probabilitiesWum[s_r.x-1][s_r.y-1] > 0) wu.add(s_r);
                        if (w.isValidPosition(s_d.x,s_d.y) && probabilitiesWum[s_d.x-1][s_d.y-1] > 0) wu.add(s_d);
                        if (w.isValidPosition(s_l.x,s_l.y) && probabilitiesWum[s_l.x-1][s_l.y-1] > 0) wu.add(s_l);
                    }
                    else{
                        if (w.isValidPosition(s_u.x,s_u.y) && probabilitiesP[s_u.x-1][s_u.y-1] > 0) wu.add(s_u);
                        //System.out.println("Probability in " + s_u.x + " " + s_u.y + " is = " + probabilitiesWum[s_u.x-1][s_u.y-1]);
                        if (w.isValidPosition(s_r.x,s_r.y) && probabilitiesP[s_r.x-1][s_r.y-1] > 0) wu.add(s_r);
                        if (w.isValidPosition(s_d.x,s_d.y) && probabilitiesP[s_d.x-1][s_d.y-1] > 0) wu.add(s_d);
                        if (w.isValidPosition(s_l.x,s_l.y) && probabilitiesP[s_l.x-1][s_l.y-1] > 0) wu.add(s_l);
                    }
                    float finSplit = split/wu.size();
                    //System.out.println("Splitting probability among " + wu.size() + " locations. Final split = " + finSplit);
                    for (Location danger : wu){
                        if (stench) probabilitiesWum[danger.x-1][danger.y-1] += finSplit;
                        else probabilitiesP[danger.x-1][danger.y-1] += finSplit;
                    }
                    
                }
               
    }
    
    public Location searchPath(Location start, Location end, ArrayList<Location> visited){
        Location next = null;
        
        Location nUP = start.neighbourUp();
        Location nRIGHT = start.neighbourRight();
        Location nDOWN = start.neighbourDown();
        Location nLEFT = start.neighbourLeft();
        
        ArrayList<Location> possNext = new ArrayList<>();
        int x = nUP.x;
        int y = nUP.y;
        if (w.isValidPosition(x,y) && !w.hasPit(x, y)){
            if (!w.isUnknown(x, y)||nUP.equals(end) ){
                possNext.add(nUP);
            }
        }
        x = nRIGHT.x; y = nRIGHT.y;
        if (w.isValidPosition(x,y) && !w.hasPit(x, y)){
            if (!w.isUnknown(x, y)||nRIGHT.equals(end) ){
                possNext.add(nRIGHT);
            }
        }
        x = nDOWN.x; y = nDOWN.y;
        if (w.isValidPosition(x,y) && !w.hasPit(x, y)){
            if (!w.isUnknown(x, y)||nDOWN.equals(end) ){
                possNext.add(nDOWN);
            }
        }
        x = nLEFT.x; y = nLEFT.y;
        if (w.isValidPosition(x,y) && !w.hasPit(x, y)){
            if (!w.isUnknown(x, y)||nLEFT.equals(end) ){
                possNext.add(nLEFT);
            }
        }        
        
        int currMin = Integer.MAX_VALUE;
        Location currentBest = null; 
        for (Location l : possNext){
            System.out.println("Possible next is: " + l);
            int distX = Math.abs(l.x - end.x);
            int distY = Math.abs(l.y - end.y);
            int d = distX + distY;
            if (!visited.contains(l)){
                if (d<currMin) {
                    currMin = d;
                    currentBest = l;
                }
            }
        }
        
        next = currentBest;
        
        return next;
    }
    
}

