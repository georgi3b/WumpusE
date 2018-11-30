package wumpusworld;

import java.util.ArrayList;
import java.util.Random;
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
    private Random rand;
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
        rand = new Random();
        probabilitiesWum = new float [4][4];
        probabilitiesP = new float[4][4];
        //initializing the probabilities matrixes to unknown
        for (int i = 0; i< 4; i++){
            for (int j=0;j<4;j++){
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
        
        //System.out.println("Agent is doing an action.");
        
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
        
        
        Location start = new Location(cX,cY);
        this.checkLocation(start);
        //System.out.println("Probabilities wumpus:");
        
        //shoot the wumpus if we are sure of his position
        if(w.hasStench(start.x, start.y)){
            Location wum=null;
            for (int i = 0; i<4;i++){
                for (int j = 0; j<4; j++){
                    //System.out.print(probabilitiesWum[i][j]+"\t");
                    if(probabilitiesWum[i][j]>1){
                        wum=new Location(i+1,j+1);
                    }
                }
                //System.out.println();
            }
            if (wum != null){
                int i = indexOfNeighbour(start,wum);
                shoot(i);
                unknownNeighbours.add(start);
                checkLocation(start);
            }
        }
        
        /*System.out.println("Probabilities breeze:");
        for (int i = 0; i<4;i++){
                for (int j = 0; j<4; j++){
                    System.out.print(probabilitiesP[i][j]+"\t");
                }
               System.out.println();
        }
        */
        
        //Get the next location where to move
        Location end = null;
        try {
            end = bestDestination();
        } catch(ArrayIndexOutOfBoundsException ex){
            //no movement is possible, it means we are in a stench 
            //and we don't know for sure where the wumpus is
            Location u,r,d,l;
            u = start.neighbourUp();
            r = start.neighbourRight();
            d = start.neighbourDown();
            l = start.neighbourLeft();
            ArrayList<Location> wu = new ArrayList<Location>();
            if (w.isValidPosition(u.x,u.y) && probabilitiesWum[u.x-1][u.y-1] > 0) wu.add(u);
            if (w.isValidPosition(r.x,r.y) && probabilitiesWum[r.x-1][r.y-1] > 0) wu.add(r);
            if (w.isValidPosition(d.x,d.y) && probabilitiesWum[d.x-1][d.y-1] > 0) wu.add(d);
            if (w.isValidPosition(l.x,l.y) && probabilitiesWum[l.x-1][l.y-1] > 0) wu.add(l);
            
            int rnd = rand.nextInt(wu.size());
            int j = indexOfNeighbour(start,wu.get(rnd));
            shoot(j);
            unknownNeighbours.add(start);
            checkLocation(start);
            if (w.wumpusAlive()){
                float p = probabilitiesWum[wu.get(rnd).x-1][wu.get(rnd).y-1];
                redistributeProbDanger(wu.get(rnd),p,true);
            }
            end = bestDestination();
        }
        //move to the best location 
        ArrayList<Location> visited = new ArrayList<Location>();
        visited.add(start);
        while (!start.equals(end)){
            Location next = nextLocToDest(start,end,visited);
            int i = indexOfNeighbour(start,next);
            
            //System.out.println("Next location is " + next);
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
        
    }
    
    public void shoot(int i){
        if (w.getDirection() == i){
                 w.doAction(w.A_SHOOT); //Move forward
        }
        if (((w.getDirection()-1) == i) ||
                ((w.getDirection() == 0) && (i == 3))){
            w.doAction(w.A_TURN_LEFT); //Turn left and move
            w.doAction(w.A_SHOOT);
        }
        if ((((w.getDirection())+1)%4) == i){
            w.doAction(w.A_TURN_RIGHT);  //Turn right and move
            w.doAction(w.A_SHOOT);
        }
        if ((((w.getDirection())+2)%4) == i){
            w.doAction(w.A_TURN_RIGHT);  //Turn 180 and move
            w.doAction(w.A_TURN_RIGHT);
            w.doAction(w.A_SHOOT);
        }
    }
    
    
    public int indexOfNeighbour(Location start, Location neighbour){
        int i = -1;
        if (neighbour.equals(start.neighbourUp())){
                i = 0;
            } else if (neighbour.equals(start.neighbourRight())){
                i = 1;
            } else if (neighbour.equals(start.neighbourDown())){
                i = 2;
            } else if (neighbour.equals(start.neighbourLeft())){
                i = 3;
            }
        return i;
    }
    
     
    public void checkLocation(Location curr){
        boolean removed = false; //if removed, we have to update matrixes
        //check location (1,1) 
        Location u,r;
        u = curr.neighbourUp();
        r = curr.neighbourRight();
        if (curr.x == 1 && curr.y == 1 && !w.isVisited(u.x, u.y)
                &&!w.isVisited(r.x, r.y)){
            removed = true;
        }
        //check the other locations
        if (unknownNeighbours.contains(curr)) {
            unknownNeighbours.remove(curr);
            removed = true;
        }
        
        ArrayList<Location> unknownNofCurr = new ArrayList<>();
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
            
            if (!w.hasWumpus(curr.x,curr.y)){
                //safe cell
               // System.out.println("Location doesn't have wumpus.");
               // probabilitiesWum[curr.x-1][curr.y-1] = 0;
               float p=probabilitiesWum[curr.x-1][curr.y-1];
               redistributeProbDanger(curr,p,true);
                
            }
            
            if (!w.hasPit(curr.x, curr.y)){
                //System.out.println("Location doesn't have pit.");
                //probabilitiesP[curr.x-1][curr.y-1] = 0;
                float p=probabilitiesP[curr.x-1][curr.y-1];
               redistributeProbDanger(curr,p,false);
            }
            
            if(w.hasStench(curr.x, curr.y)){
                //System.out.println("Location has stench.");
                //update probabilities of neighbours
                checkPercept(true, curr, unknownNofCurr);
                 
                float max=-1000;
                for (int i = 0; i<4;i++){
                    for (int j = 0; j<4; j++){
                        if(probabilitiesWum[i][j]>max){
                            max=probabilitiesWum[i][j];
                            //System.out.println("Max is = " + max);
                        }
                    }
                }
                //for wumpus we retain only the maximum value (because
                //we have only ONE wumpus)
                for (int i = 0; i<4;i++){
                    for (int j = 0; j<4; j++){
                        float p = probabilitiesWum[i][j];
                        if(p!=-1000 && p!=max){
                           probabilitiesWum[i][j]=0;

                        }
                    }
                }
            }                    

            if (w.hasBreeze(curr.x,curr.y)){
               // System.out.println("Location has breeze.");
                //update probabilities of neighbours
                checkPercept(false, curr, unknownNofCurr);
            }
            
            if (!w.hasStench(curr.x,curr.y)){
                noDangerUpdate(true,curr);
            }
            
            if (!w.hasBreeze(curr.x, curr.y)){
                noDangerUpdate(false,curr);
            }
        }
    }
    /**
     * This method checks the unknown neighbours of a cell which has stench/breeze.
     * It updates the probabilities in the prob matrixes to have a Wumpus/pit in
     * the neighbouring cell. It does so by checking the neighbours of these neighbours:
     * if at least one is known and doesn't have a stench/breeze we can exclude
     * that neighbour from probability computation.
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
                   // System.out.println("Incrementing probability of wumpus.");
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
                        if(stench){
                            probabilitiesWum[l.x-1][l.y-1] +=1;
                        }
                        else if(probabilitiesP[l.x-1][l.y-1] < 4  ){
                            probabilitiesP[l.x-1][l.y-1] +=1;
                        }
                    }
            }
            Location n1,n2,n3,n4;
            int nrPit=0; int nrWall=0;
            n1=curr.neighbourUp();
            n2=curr.neighbourRight();
            n3=curr.neighbourDown();
            n4=curr.neighbourLeft();
            ArrayList<Location>breezeNeig=new ArrayList<>();
            Location pit = null;
            breezeNeig.add(n1); breezeNeig.add(n2); breezeNeig.add(n3); breezeNeig.add(n4);
            for(Location l: breezeNeig){
                if(!w.isValidPosition(l.x, l.y)){
                    nrWall++;
                }
                if(w.isValidPosition(l.x, l.y) && probabilitiesP[l.x-1][l.y-1]>0){
                    pit = l;
                    nrPit++;
                }
                
            }
            if(nrWall>0 && nrPit==1 && probabilitiesP[pit.x-1][pit.y-1] < 4  ){
                probabilitiesP[pit.x-1][pit.y-1]+=1;
            }
            
    }
    
    
    /**
     * This method is run on a cell which doesn't have stench or breeze 
     * in order to update the probability Wumpus/Pit of its neighbours
     * (no percept => no danger). It does so by redistributing their values 
     * among the neighbours of the neighbours that have the corresponding percept.
     * @param stench the percept that indicates which matrix are we looking at 
     * @param curr 
     */
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

                        if (pr!=0){
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
                        redistributeProbDanger(now, pr, true);
                    }
                    else{
                        pr = probabilitiesP[now.x-1][now.y-1];
                        redistributeProbDanger(now, pr, false);
                    }
                }
    }
    
    public void redistributeProbDanger(Location noDanger, float p,boolean stench){
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
                float split = ((p!=-1000)? p/length: 0);
                //System.out.println("Resetting " +  noDanger.x + " " + noDanger.y + " to 0.Split = " + split);
                if (stench) probabilitiesWum[noDanger.x-1][noDanger.y-1] = 0;
                else probabilitiesP[noDanger.x-1][noDanger.y-1] = 0;
                
                for (Location n:s){
                    
                    Location s_u,s_r,s_d,s_l;
                    s_u = n.neighbourUp();
                    s_r = n.neighbourRight();
                    s_d = n.neighbourDown();
                    s_l = n.neighbourLeft();
                    //check which location has already a certain probability of having wumpus/
                    //pit and add it into the array where the current value has to be redistributed
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
                        else if (probabilitiesP[danger.x-1][danger.y-1]<4) probabilitiesP[danger.x-1][danger.y-1] += finSplit;
                    }
                    
                }
               
    }
    
    /**
     * 
     * @param start
     * @param end
     * @param visited
     * @return 
     */
    public Location bestDestination(){
        
        ArrayList<Location> nextPossible =  new ArrayList<>();
        for (Location l: unknownNeighbours){
            nextPossible.add(l);
        }
        for (int i = 0; i< 4; i++){
            for (int j = 0; j< 4; j++){
                if (probabilitiesWum[i][j] > 0){
                    nextPossible.remove(new Location(i+1,j+1));
                }          
            }
        }
        ArrayList<Location> fromNext = new ArrayList<>();
      
        for (int i = 0; i<nextPossible.size(); i++){
            Location start = new Location(w.getPlayerX(),w.getPlayerY());
            ArrayList<Location> visited = new ArrayList<Location>();
            visited.add(start);
            Location end =  nextPossible.get(i);
           // System.out.println("Start = " + start);
           // System.out.println("End = " + end);
            fromNext.add(end);
            while (!start.equals(end)){
                Location next = nextLocToDest(start,end,visited);
                if (w.hasPit(next.x, next.y)){
                    fromNext.remove(end);
                    break;
                    
                }
                //System.out.println("Next location is " + next);
                visited.add(next);     
                start = next;
            }
            
        }
        
        ArrayList<Location> minimum = new ArrayList<>();
        float minp = +1000;
        for (Location nx : fromNext){
            float val = probabilitiesP[nx.x-1][nx.y-1];
            if (val<minp){
                minp = val;
            }
        }
        for (Location nx : fromNext){
            if (probabilitiesP[nx.x-1][nx.y-1] == minp){
                minimum.add(nx);
            }
        }
        
        // compute the closest possible next location
        int[] distances = new int[minimum.size()];
      
        for (int i = 0; i<minimum.size(); i++){
            Location start = new Location(w.getPlayerX(),w.getPlayerY());
            ArrayList<Location> visited = new ArrayList<Location>();
            visited.add(start);
            Location end = minimum.get(i);
            int d = 0;
           // System.out.println("Start = " + start);
           // System.out.println("End = " + end);

            while (!start.equals(end)){
                Location next = nextLocToDest(start,end,visited);
                //System.out.println("Next location is " + next);
                visited.add(next);     
                start = next;
                d++;
            }
            distances[i] = d;
        }
        int min = +1000;
        int index = -1;
        for (int j=0;j<distances.length; j++){
            if (distances[j] < min){
                min = distances[j];
                index = j;
            }
        }
        Location closest = minimum.get(index); 
        return closest;
    }
    
    public Location nextLocToDest(Location start, Location end, ArrayList<Location> visited){
        Location next = null;
        
        Location nUP = start.neighbourUp();
        Location nRIGHT = start.neighbourRight();
        Location nDOWN = start.neighbourDown();
        Location nLEFT = start.neighbourLeft();
        
        ArrayList<Location> possNext = new ArrayList<>();
        int x = nUP.x;
        int y = nUP.y;
        if (w.isValidPosition(x,y) /*&& !w.hasPit(x, y)*/){
            if (!w.isUnknown(x, y)||nUP.equals(end) ){
                possNext.add(nUP);
            }
        }
        x = nRIGHT.x; y = nRIGHT.y;
        if (w.isValidPosition(x,y) ){
            if (!w.isUnknown(x, y)||nRIGHT.equals(end) ){
                possNext.add(nRIGHT);
            }
        }
        x = nDOWN.x; y = nDOWN.y;
        if (w.isValidPosition(x,y)){
            if (!w.isUnknown(x, y)||nDOWN.equals(end) ){
                possNext.add(nDOWN);
            }
        }
        x = nLEFT.x; y = nLEFT.y;
        if (w.isValidPosition(x,y)){
            if (!w.isUnknown(x, y)||nLEFT.equals(end) ){
                possNext.add(nLEFT);
            }
        }        
        
        int currMin = Integer.MAX_VALUE;
        Location currentBest = null; 
        for (Location l : possNext){
            //System.out.println("Possible next is: " + l);
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

