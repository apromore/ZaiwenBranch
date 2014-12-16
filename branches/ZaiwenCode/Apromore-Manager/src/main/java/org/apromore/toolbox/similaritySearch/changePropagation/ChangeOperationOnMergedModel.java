package org.apromore.toolbox.similaritySearch.changePropagation;

import org.apromore.toolbox.similaritySearch.graph.Edge;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fengz2 on 18/09/2014.
 */
public class ChangeOperationOnMergedModel {

    //**primitive operation**
    // **Insert an edge into a graph, of which start node is v_p and end node is v_s*//
    public static Edge InsertEdgeToCG (Graph mergedModel, Vertex v_p, Vertex v_s){

        /*set up an edge with empty label*/
        Edge e = mergedModel.connectVertices(v_p, v_s);
        //changedMergedGraph.addEdge(newEdge);
        //It probably should be extended further....//

        return e;
    }

    //*primitive operation*//
    /*Delete an edge from a graph, of which start node is v_p and end node is v_s*/
    public static void DeleteEdgeFromCG (Graph mergedModel, Vertex v_p, Vertex v_s){

        if(mergedModel.getEdgeLabels(v_p.getID(), v_s.getID()).size()==0){

            mergedModel.removeEdge(v_p.getID(), v_s.getID());

        }

    }


    /**primitive operation***/
    /***Insert an edge annotation to an edge **********/
    public static void InsertEdgeAnnotationToCG (Graph mergedModel, Vertex v_p, Vertex v_s, HashSet<String> labels){

        /**add an annotation on the edge, of which the start node is v_p as well as the end node is v_s****/
            /*get the edge from v_p to v_s**/
        Edge e = mergedModel.containsEdge(v_p.getID(), v_s.getID());
        if(e != null){
            e.addLabels(labels);
        }


    }

    //*primitive operation*//
    /*delete an edge annotation from an edge*/
    public static void DeleteEdgeAnnotationFromCG (Graph mergedModel, String fromVertex, String toVertex, String label){

        HashSet<String> labels = mergedModel.containsEdge(fromVertex, toVertex).getLabels();

        /*make it sure that the annotation on the edge between v_p and v_s contains 'label', before removing label from it*/
        if(labels.contains(label)){

            labels.remove(label);
        }

    }

    //**primitive change operation*****///
    /***
     * Configuralize connector*****/
    public static void ConfiguralizeConnector (Graph mergedModel, Vertex v){


        /*loop all of vertices in the graph, identify the vertex to be configuralized**/
        List<Vertex> vertices = mergedModel.getVertices();
        Iterator<Vertex> it = vertices.iterator();
        while(it.hasNext()){
            Vertex vertex = it.next();
            if(vertex.getID().equals(v.getID())){

                vertex.setConfigurable(true);
                vertex.setGWType(Vertex.GWType.or);
            }
        }

    }

    //**change operation**//
    //***Append a node to the graph*********/
    public static void AppendNodeToCG (Graph mergedModel, Vertex v_p, Vertex new_v, String incrementalLabel){

        mergedModel.addVertex(new_v);
        InsertEdgeToCG(mergedModel, v_p, new_v);

        //**insert a node annotation onto the edge between v_p and new_v**///
        HashSet<String> labels = new HashSet<String>();
        labels.add(new String(incrementalLabel));
        InsertEdgeAnnotationToCG(mergedModel, v_p, new_v, labels);

    }

    /**change operation*******/
    //**Insert a node to an edge of CG**//
    public static void InsertNodeToCG (Graph mergedModel, Vertex new_v, Vertex v_p, Vertex v_s){

        mergedModel.addVertex(new_v);
        InsertEdgeToCG(mergedModel, v_p, new_v);
        InsertEdgeToCG(mergedModel, new_v, v_s);

                /*remove the edge (v_p, v_s) if it does exist*/
        Edge e = mergedModel.containsEdge(v_p.getID(), v_s.getID());
        if(e != null){

            mergedModel.removeEdge(v_p.getID(), v_s.getID());
        }

    }

    /*change operation*/
    //*Split the common edge into two , the annotation on the original edge is 'pid'

    // param1---mergedModel: graph to be changed
    // param2---v_p: starting node of the edge
    // param3---v_s: ending node of the edge
    // param4---v_to_be_cp: node to be copied
    // param5---updatedPartLabels: pid set that is remained on the previous edge*/
    public static void SplitCommonRegion (Graph mergedModel, Vertex v_p, Vertex v_s, Vertex v_to_be_copied ,HashSet<String> updatedPartLabels){

        /*case 1: v_p is the vertex that to be copied*/
        if(v_to_be_copied.getID().equals(v_p.getID())){

            /*copy 'v_p'*/

            Vertex copied_right_CG_v = Vertex.copyVertex(v_p);
            v_p.setConfigurable(false);
            copied_right_CG_v.setConfigurable(false);
            mergedModel.addVertex(copied_right_CG_v);

            /*get the label set between v_p and v_s*/
            HashSet<String> originalLabels = mergedModel.getEdgeLabels(v_p.getID(), v_s.getID());

            /*create the new hash set to store label set on another label*/
            HashSet<String> otherPartLabels = new HashSet<String>();

            //loop
            Iterator<String> it = originalLabels.iterator();
            while (it.hasNext()){

                String label = it.next();

                otherPartLabels.add(label);
                if(updatedPartLabels.contains(label)){

                    originalLabels.remove(label);

                }

            }

            /*loop the 'otherPartLabels' set*/
            Iterator<String> ite = otherPartLabels.iterator();
            while (ite.hasNext()){

                String label = ite.next();
                if(updatedPartLabels.contains(label)){

                    otherPartLabels.remove(label);
                }

            }
            originalLabels = updatedPartLabels;

            /*connect 'copied_right_CG_v' with v_s with an edge*/
            InsertEdgeToCG(mergedModel, copied_right_CG_v, v_s);
            /*lastly, add annotation on it*/
            InsertEdgeAnnotationToCG(mergedModel, copied_right_CG_v, v_s, otherPartLabels);
        }

        /*case 2: v_s is the vertex that to be copied*/
        else if(v_to_be_copied.getID().equals(v_s.getID())){

                        /*copy 'v_s'*/

            Vertex copied_right_CG_v = Vertex.copyVertex(v_s);
            v_s.setConfigurable(false);
            copied_right_CG_v.setConfigurable(false);
            mergedModel.addVertex(copied_right_CG_v);

            /*get the label set between v_p and v_s*/
            HashSet<String> originalLabels = mergedModel.getEdgeLabels(v_p.getID(), v_s.getID());

            /*create an empty hash set to store label set on another label*/
            HashSet<String> otherPartLabels = new HashSet<String>();

            //loop
            Iterator<String> it = originalLabels.iterator();
            while (it.hasNext()){

                String label = it.next();

                otherPartLabels.add(label);
                if(updatedPartLabels.contains(label)){

                    originalLabels.remove(label);

                }

            }

            /*loop the 'otherPartLabels' set*/
            Iterator<String> ite = otherPartLabels.iterator();
            while (ite.hasNext()){

                String label = ite.next();
                if(updatedPartLabels.contains(label)){

                    otherPartLabels.remove(label);
                }

            }
            originalLabels = updatedPartLabels;

            /*connect 'v_p' and 'copied_right_CG_v'  with an edge*/
            InsertEdgeToCG(mergedModel, v_p, copied_right_CG_v);
            /*lastly, add annotation on it*/
            InsertEdgeAnnotationToCG(mergedModel, v_p, copied_right_CG_v, otherPartLabels);

        }
    }

    /*change operation*/
    /*change an edge from one to another with the same label remained*/
    public static void ChangeEdgeInCG (Graph mergedModel, Vertex v_p, Vertex v_s, Vertex new_v_p, Vertex new_v_s){

        if((v_p.getID().equals(new_v_p.getID()))||(v_p.getID().equals(new_v_s.getID()))||(v_s.getID().equals(new_v_p.getID()))||(v_s.getID().equals(new_v_s.getID()))){

            /*get the label on the edge from 'v_p' to 'v_s' and remove it*/
            HashSet<String> edgeLabels = mergedModel.containsEdge(v_p.getID(), v_s.getID()).getLabels();

            Iterator<String> it = edgeLabels.iterator();
            while (it.hasNext()){
                String label = it.next();

                /*delete annotations from the edge*/
                DeleteEdgeAnnotationFromCG(mergedModel, v_p.getID(), v_s.getID(), label);

            }

            /*delete the edge from the graph*/
            DeleteEdgeFromCG(mergedModel, v_p, v_s);

            /*insert a new edge*/
            InsertEdgeToCG(mergedModel, new_v_p, new_v_s);

            /*insert new annotation on the new edge*/
            InsertEdgeAnnotationToCG(mergedModel, new_v_p, new_v_s, edgeLabels);
        }

    }

}
