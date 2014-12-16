package org.apromore.toolbox.similaritySearch.common.algos;

import org.apromore.toolbox.similaritySearch.graph.Edge;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fengz2 on 11/09/2014.
 * Overall Approach for Change Propagation from G to CG
 *
 *
 *
 *
 */
public class FindChangeOperationSet {

//****Given two similar graphs i.e. G1 and G2 with mapping ids, the class provides some methods
// to find the minimal change operations from G1 to G2**********//


    //**define a structure to record the list of high-leveled change operations**//



    public static List<ChangeOperation> findGraphEditDistance(Graph sg1, Graph sg2){


        //*create a list to record a sequence of change operations*//
        List<ChangeOperation> changeOperationList = new ArrayList<ChangeOperation>();



        List<Vertex> vertexList_sg1 = sg1.getVertices();

        List<Vertex> vertexList_original_sg1 = new ArrayList<Vertex>();  //'vertexList_original_sg1' is a copy of sg1 nodes
        for(Vertex v : vertexList_sg1){
            vertexList_original_sg1.add(v);
        }

        //**create a array list to save the id of 'vertexList_sg1'**//
        List<String> ids_vertexList_in_G1 = new ArrayList<String>();
        for(Vertex v : vertexList_original_sg1){

            ids_vertexList_in_G1.add(v.getID());
        }

        List<Vertex> vertexList_sg2 = sg2.getVertices();

        List<Vertex> vertexList_original_sg2 = new ArrayList<Vertex>();
        for(Vertex v : vertexList_sg2){
            vertexList_original_sg2.add(v);
        }

        //**create a array list to save the id of 'vertexList_sg2'**//
        List<String> ids_vertexList_in_G2 = new ArrayList<String>();
        for(Vertex v : vertexList_original_sg2){

            ids_vertexList_in_G2.add(v.getID());
        }


        //**get the disjoint set of 'vertexList_original_sg1' & 'vertexList_original_sg2'**//
        List<Vertex> disjoint_vertexList = new ArrayList<Vertex>();
        for(Vertex v_sg1 : vertexList_original_sg1){

            for(Vertex v_sg2: vertexList_original_sg2){
                if(v_sg2.getID().equals(v_sg1.getID())){

                    disjoint_vertexList.add(v_sg2);

                }
            }
        }

        //**get the vertexes that should be deleted from G1**//
        List<Vertex> deleted_vertexList_from_G1;
        //**get the iterator of 'VertexList_original_sg1'**//
        Iterator<Vertex> it_sg1 = vertexList_original_sg1.iterator();
        while (it_sg1.hasNext()){

            Vertex v_in_sg1 = it_sg1.next();
            for(Vertex v_disjoint : disjoint_vertexList){

                if(v_in_sg1.getID().equals(v_disjoint.getID())){

                    it_sg1.remove();
                }

            }
        }
        deleted_vertexList_from_G1 = vertexList_original_sg1;
        //**create a array list to save the id of 'deleted_vertexList_from_G1'**//
        List<String> ids_deleted_vertexList_from_G1 = new ArrayList<String>();
        for(Vertex v : deleted_vertexList_from_G1){

            ids_deleted_vertexList_from_G1.add(v.getID());
        }

        //**get the nodes that should be added to G1 **//
        List<Vertex> added_vertexList_to_G1;
        //**get the iterator of 'VertexList_sg2'**//
        Iterator<Vertex> it_sg2 = vertexList_original_sg2.iterator();
        while(it_sg2.hasNext()){

            Vertex v_in_sg2 = it_sg2.next();
            for(Vertex v_disjoint : disjoint_vertexList){

                if(v_in_sg2.getID().equals(v_disjoint.getID())){

                    it_sg2.remove();
                }
            }

        }
        added_vertexList_to_G1 = vertexList_original_sg2;
        //**create a array list to save the id of 'added_vertexList_to_G1'**//
        List<String> ids_added_vertexList_to_G1 = new ArrayList<String>();
        for(Vertex v : added_vertexList_to_G1){

            ids_added_vertexList_to_G1.add(v.getID());
        }

        /////////////////////////////////////////////////////////////////////////////

        List<Edge> edgeList_original_sg1 = new ArrayList<Edge>();
        for(Edge e : sg1.getEdges()){
            edgeList_original_sg1.add(e);
        }

        List<String> ids_edgeList_sg1 = new ArrayList<String>();
        for(Edge edge : edgeList_original_sg1){
            ids_edgeList_sg1.add(edge.getId());
        }

        List<Edge> edgeList_original_sg2 = new ArrayList<Edge>();
        for(Edge e : sg2.getEdges()){
            edgeList_original_sg2.add(e);
        }
        List<String> ids_edgeList_sg2 = new ArrayList<String>();
        for(Edge edge : edgeList_original_sg2){
            ids_edgeList_sg2.add(edge.getId());
        }

        //**get the disjoint set of 'edgeList_original_sg1' & 'edgeList_original_sg2'**//
        List<Edge> disjoint_edgeList = new ArrayList<Edge>();
        for(Edge e_sg1 : edgeList_original_sg1){

            for(Edge e_sg2: edgeList_original_sg2){
                if(e_sg2.getId().equals(e_sg1.getId())){

                    disjoint_edgeList.add(e_sg2);

                }
            }
        }

        //**get the edges that should be deleted from G1**//
        List<Edge> deleted_edgeList_from_G1;
        //**get the iterator of 'EdgeList_sg1'**//
        Iterator<Edge> it_sg14edge = edgeList_original_sg1.iterator();
        while (it_sg14edge.hasNext()){

            Edge e_in_sg1 = it_sg14edge.next();
            for(Edge e_disjoint : disjoint_edgeList){

                if(e_in_sg1.getId().equals(e_disjoint.getId())){

                    it_sg14edge.remove();
                }

            }
        }
        deleted_edgeList_from_G1 = edgeList_original_sg1;
        //**create a array list to save the id of 'deleted_edgeList_from_G1'**//
        List<String> ids_deleted_edgeList_from_G1 = new ArrayList<String>();
        for(Edge e : deleted_edgeList_from_G1){

            ids_deleted_edgeList_from_G1.add(e.getId());
        }

        //**get the edges that should be added to G1 **//
        List<Edge> added_edgeList_to_G1;
        //**get the iterator of 'EdgeList_sg2'**//
        Iterator<Edge> it_sg24edge = edgeList_original_sg2.iterator();
        while(it_sg24edge.hasNext()){

            Edge e_in_sg2 = it_sg24edge.next();
            for(Edge e_disjoint : disjoint_edgeList){

                if(e_in_sg2.getId().equals(e_disjoint.getId())){

                    it_sg24edge.remove();
                }
            }

        }
        added_edgeList_to_G1 = edgeList_original_sg2;
        //**create a array list to save the id of 'added_edgeList_to_G1'**//
        List<String> ids_added_edgeList_to_G1 = new ArrayList<String>();
        for(Edge e : added_edgeList_to_G1){

            ids_added_edgeList_to_G1.add(e.getId());
        }

        //****************************//


        //**the following the code is to
        // aggregate the low-leveled change primitives i.e. 1.delete node; 2. add node;
        // 3. delete edge; 4. add edge into more high-leveled change operations*************************//

        //*Loop 'added_edgeList_to_G1'**//
        EdgeObjWithVisitedFlag[] added_edge_array = new EdgeObjWithVisitedFlag[added_edgeList_to_G1.size()]; //set an empty array
        for(int i =0; i< added_edge_array.length; i++){

            EdgeObjWithVisitedFlag edgeObjWithVisitedFlag = new EdgeObjWithVisitedFlag();
            edgeObjWithVisitedFlag.setEdge(added_edgeList_to_G1.get(i));
            edgeObjWithVisitedFlag.setFlag(false); //initially set false, which means edge has not been visited
            added_edge_array[i] = edgeObjWithVisitedFlag;
        }

        //Iterator<Edge> it_add = added_edgeList_to_G1.iterator();
        while(checkEdgeVisited(added_edge_array)){

            for(int i=0; i < added_edge_array.length; i++){

                if(added_edge_array[i].getFlag() == false){

                    Edge e_added_into_G1 = added_edge_array[i].getEdge();
                    String starting_point = e_added_into_G1.getFromVertex();
                    String ending_point = e_added_into_G1.getToVertex();


                    //Case1: find out potential change operations for 'append node'//
                    if(ids_vertexList_in_G1.contains(starting_point)&&ids_added_vertexList_to_G1.contains(ending_point)){

                        ChangeOperation op = new ChangeOperation();
                        op.setChangeOperationName(ChangeOperationName.APPENDNODETOG);
                        List<String> changeOperationArgument = new ArrayList<String>();

                /*keep the order of arguments here in accordance with the order of arguments of change operation function*/
                        changeOperationArgument.add(sg1.ID);
                        changeOperationArgument.add(ending_point);  //**new_v
                        changeOperationArgument.add(starting_point);  //**v_p
                /*keep the order of arguments here in accordance with the order of arguments of change operation function*/

                        op.setChangeOperationArgues(changeOperationArgument);
                        changeOperationList.add(op);

                        //**remove this edge from 'added_edgeList_to_G1' and 'ids_added_edgeList_to_G1'**//
                        added_edge_array[i].setFlag(true);//set it to be true, which means this edge has been visited.
                        ids_added_edgeList_to_G1.remove(e_added_into_G1.getId());

                        //**remove the appended node from 'added_vertexList_to_G1' and 'ids_added_vertexList_to_G1'**//
                        Iterator<Vertex> it_2 = added_vertexList_to_G1.iterator();
                        while (it_2.hasNext()){

                            Vertex v = it_2.next();
                            if(v.getID().equals(ending_point)){

                                //*in the meanwhile, add the appended node to sg1. I.e. 'ids_vertex_G1'*//
                                //vertexList_original_sg1.add(v);
                                ids_vertexList_in_G1.add(v.getID());

                                it_2.remove();

                            }
                        }

                        ids_added_vertexList_to_G1.remove(ending_point);

                        //***add this edge to sg1. I.e. 'edgeList_original_G1' ***//
                        //edgeList_original_sg1.add(e_added_into_G1);
                        ids_edgeList_sg1.add(e_added_into_G1.getId());


                    }


                    /*****************************************************************************************/
                    ///*Case2: find out potential change operations for 'prepend node'**
                    //To be designed.../

                }
            }
        }

        EdgeObjWithVisitedFlag[] deleted_edge_array = new EdgeObjWithVisitedFlag[deleted_edgeList_from_G1.size()]; //set an empty array
        for(int i =0; i< deleted_edge_array.length; i++){

            EdgeObjWithVisitedFlag edgeObjWithVisitedFlag = new EdgeObjWithVisitedFlag();
            edgeObjWithVisitedFlag.setEdge(deleted_edgeList_from_G1.get(i));
            edgeObjWithVisitedFlag.setFlag(false); //initially set false, which means edge has not been visited
            deleted_edge_array[i] = edgeObjWithVisitedFlag;
        }

        while(checkEdgeVisited(deleted_edge_array)){

            //*Loop 'deleted_edgeList_from_G1'**//
            Iterator<Edge> it_del = deleted_edgeList_from_G1.iterator();
            while(it_del.hasNext()){

                Edge e_deleted_from_G1 = it_del.next();
                String starting_point = e_deleted_from_G1.getFromVertex();
                String ending_point = e_deleted_from_G1.getToVertex();

                //Case4: find out potential change operations for 'delete edge'//
                if(ids_vertexList_in_G1.contains(starting_point)&&ids_vertexList_in_G1.contains(ending_point)){

                    ChangeOperation op = new ChangeOperation();
                    op.setChangeOperationName(ChangeOperationName.DELETEEDGEFROMG);
                    List<String> changeOperationArgument = new ArrayList<String>();

                /*keep the order of arguments here in accordance with the order of arguments of change operation function*/
                    changeOperationArgument.add(starting_point);  //**v_p
                    changeOperationArgument.add(ending_point);  //**v_s
                    changeOperationArgument.add(sg1.ID);
                /*keep the order of arguments here in accordance with the order of arguments of change operation function*/

                    op.setChangeOperationArgues(changeOperationArgument);
                    changeOperationList.add(op);

                    //**remove this edge from 'deleted_edgeList_from_G1' and 'ids_deleted_edgeList_from_G1'**//
                    it_del.remove();
                    ids_deleted_edgeList_from_G1.remove(e_deleted_from_G1.getId());

                }
            }

        }


    return changeOperationList;
}

    /*if there is edge in the array, which have not been visited, return true*/
    public static boolean checkEdgeVisited(EdgeObjWithVisitedFlag[] edge_array){

        boolean unvisited = false;

        for(int i=0; i<edge_array.length; i++){

            if(edge_array[i].getFlag() == false){

                unvisited = true;

                break;

            }

            else {
                continue;

            }

        }

        return unvisited;
    }

}
