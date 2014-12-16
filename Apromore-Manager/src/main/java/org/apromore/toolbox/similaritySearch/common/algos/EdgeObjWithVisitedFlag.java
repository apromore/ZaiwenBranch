package org.apromore.toolbox.similaritySearch.common.algos;

import org.apromore.toolbox.similaritySearch.graph.Edge;

/**
 * Created by fengz2 on 7/12/2014.
 */
public class EdgeObjWithVisitedFlag {

    //this class help to record if an added edge or a deleted edge has been visited or not, it's used in 'findChangeOperationSet' class
    private boolean flag = false; //1. false means the edge has not been visited. 2. true means it has been visited
    private Edge edge;

    public boolean getFlag(){
        return flag;
    }

    public void setFlag(boolean flag){
        this.flag = flag;
    }

    public Edge getEdge(){
        return edge;
    }

    public void setEdge(Edge edge){

        this.edge = edge;
    }

}
