package org.apromore.toolbox.similaritySearch.changePropagation;

import org.apromore.toolbox.similaritySearch.graph.Edge;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;

import java.util.HashSet;

/**
 * Created by fengz2 on 18/09/2014.
 */
public class ChangeOperationOnProcessVariant {

    /**primitive operation**/
    public static void InsertEdgeToG (Graph processVariant, Vertex v_p, Vertex v_s, String label, String edge_id){


        /*set up a new edge between v_p and v_s*/
        Edge e = processVariant.connectVertices(v_p, v_s);
        e.setId(edge_id);

        /**add label to the edge**/
        HashSet<String> labels = new HashSet<String>();
        labels.add(label);
        e.addLabels(labels);

    }

    //*primitive operation**/
    public static void DeleteEdgeFromG (Graph processVariant, Vertex v_p, Vertex v_s){


        //*judge if the edge (v_p, v_s) is contained in the process variant*//
        Edge e = processVariant.containsEdge(v_p.getID(), v_s.getID());
        if(e != null){

            processVariant.removeEdge(v_p.getID(), v_s.getID());
        }

        //**remove the orphan node in the changed model, cleaning operation**//
        processVariant.removeEmptyNodes();


    }

    /**change operation***/
    //*append a node to the 'graph'*//
    public static void AppendNodeToG (Graph processVariant, Vertex new_v, Vertex v_p, String label, String edge_id ){


        processVariant.addVertex(new_v);
        InsertEdgeToG(processVariant, v_p, new_v, label, edge_id);


    }

    /**change operation****/
    public static void InsertNodeToG (Graph processVariant, Vertex new_v, Vertex v_p, Vertex v_s, String edgeId_1, String edgeId_2){

        processVariant.addVertex(new_v);
        //InsertEdgeToG(processVariant, v_p, new_v, edgeId_1);
        //InsertEdgeToG(processVariant, new_v, v_s, edgeId_2);

        /*remove the edge (v_p, v_s) if it does exist*/
        Edge e = processVariant.containsEdge(v_p.getID(), v_s.getID());
        if(e != null){

            processVariant.removeEdge(v_p.getID(), v_s.getID());
        }

    }


}
