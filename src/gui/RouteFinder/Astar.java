/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.RouteFinder;

import Planner.Actions;
import Planner.Plan;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;
import worldmodel.World;

/**
 *
 * @author jakobsuper
 */
public class Astar {
    private int movecost = 1;
    private Map<Long,Long> map;
    private long agent;
    
    public Plan findPlan(World w,String action) throws InterruptedException {
        map = w.simpleMap();
        
        long[] startogoal = w.parseAction(action);
        long current = startogoal[0];//w.getAgentPos(agent);
        long goal = startogoal[1];//w.getAgentPos(agent);
        agent = startogoal[2];
        TreeSet<Node> frontier = new TreeSet<Node>();
        Node init = makeInitialNode(current);
        frontier.add(init);
        int states = 0;
        while (true) {
            if (frontier.isEmpty()) {
                return null;
            }
            Node n = frontier.pollFirst();
            if (n.curPosition == goal) {
                return makeSolution(n);
            }
            for (long position : actions(n.curPosition)) {
                long s1 = position;
                frontier.add(new Node(s1, n, n.g + movecost, heuristik(s1,goal)));
                states++;
            }
          
        }
    }

    public double heuristik(long cp,long goal) {
            int[] c = coordsFor(cp);
            int[] g = coordsFor(goal);
            double h = (Math.abs(c[0] - g[0]) + Math.abs(c[1] - g[1]));
            return h;
    }
    
    public ArrayList<Long> actions(long cp) {
        ArrayList<Long> actionsReturn = new ArrayList<Long>();
        for(Long a:neighborsFor(cp)){
            if(a!=null){
              actionsReturn.add(a);
            }
        }
        return actionsReturn;
    }
    
    private Node makeInitialNode(long cp) {
        return new Node(cp, null, 0, 0);
    }

    private Plan makeSolution(Node n) {
        Plan p = new Plan();
        Node node = n;
        while (node != null) {
            int[] cords = coordsFor(node.curPosition);
            Actions action = new Actions("moveAtomic(" + this.agent + ", [" + cords[0] + "," + cords[1] + "])", true, true);
            
            //p.add(node.curPosition);
            p.add(action);
            node = node.parent;
        }
        //p.printSolution();
        return p;
    }
    
    private ArrayList<Long> neighborsFor(long k){    
        int x = (int)(k & 0xFFFFFFFF) - 1000000000;
        int y = (int)((k >>> 32) & 0xFFFFFFFF) - 1000000000;
        ArrayList<Long> newlist = new ArrayList<Long>();
        newlist.add(k-1);
        newlist.add(k+1);
        newlist.add(map.get(keyFor(x,y-1)));
        newlist.add(map.get(keyFor(x,y+1)));
        newlist.add(map.get(keyFor(x-1,y-1)));
        newlist.add(map.get(keyFor(x-1,y-1)));
        newlist.add(map.get(keyFor(x+1,y+1)));
        newlist.add(map.get(keyFor(x+1,y+1)));
        return newlist;
    }
    
    public Long keyFor(int x, int y) {
        int kx = x + 1000000000;
        int ky = y + 1000000000;
        return (long)kx | (long)ky << 32;
    }
    
    private int[] coordsFor(long k) {
        int x = (int)(k & 0xFFFFFFFF) - 1000000000;
        int y = (int)((k >>> 32) & 0xFFFFFFFF) - 1000000000;
        return new int[]{x,y};
    }
    
}
