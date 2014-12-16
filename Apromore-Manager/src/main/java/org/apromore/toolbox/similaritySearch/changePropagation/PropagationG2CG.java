package org.apromore.toolbox.similaritySearch.changePropagation;

import org.apromore.toolbox.similaritySearch.common.EdgePairFromG2CG;
import org.apromore.toolbox.similaritySearch.common.VertexPairFromG2CG;
import org.apromore.toolbox.similaritySearch.graph.Edge;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *Defines propagation algorithms from variants to merged model
 *
 * it inherits the class 'Merged Model'
 *
 * implemented by:
 *
 * @author <a href="mailto:fzwbmw@gmail.com">Zaiwen FENG</a>
 */



public class PropagationG2CG {

    //the change propagation operations are actually a sequence of change operations on the merged graph as well as change operation on process graph.
    //*parameter1: initial merged model
    // 2: initial variant
    //3. v_p: a node on the initial variant 'variant', which will be appended by 'new_v'
    //4. new_v: a node appended to 'v_p'
    //5. label: a label between 'v_p' and 'new_v'
    //6. edge_id
    // 7. initial vertex mapping between g and cg, with canonical id
    // 8. initial edge mapping between g and cg, with canonical id
    // return: void**//



    public static void AppendNodePropagation2CG (Graph mergedModel, Graph variant, Vertex v_p, Vertex new_v, String label, String edge_id, List<VertexPairFromG2CG> vertexPairFromG2CGs,
                                               List<EdgePairFromG2CG> edgePairFromG2CGs){

        //first of all, append 'new_v' to 'v_p' in  'variant', then variant has been changed for one step
        ChangeOperationOnProcessVariant.AppendNodeToG(variant,new_v,v_p,label, edge_id);

        //get the vertex id in CG which is corresponding to v_p in G
        String right_CG_v_id = findCorrespondingNodeIdInCG(v_p.getID(), vertexPairFromG2CGs);

        //**get the vertex in CG according to 'right_CG_v_id'***//
        Vertex right_CG_v = new Vertex(v_p.getType(),new String(""), new String(""));
        List<Vertex> vertexes = mergedModel.getVertices();
        Iterator<Vertex> it = vertexes.iterator();
        while (it.hasNext()){
            Vertex v = it.next();
            if(v.getID().equals(right_CG_v_id)){

                    right_CG_v = v;
                break;
            }
        }

        //*judge if the 'v_p' is 'function' or 'event'*
        // and if the incoming edge to 'v_p'' has multiple annotations*//

            /*get the 'pre-set' of 'v_p'*/
        List<Vertex> preset_of_right_CG_v = mergedModel.getPreset(right_CG_v_id);
        Vertex pre_of_right_CG_v = preset_of_right_CG_v.get(0);

            /*get the label on the edge between 'v_p'' and its 'pre-set'  */
        Edge e = mergedModel.containsEdge(pre_of_right_CG_v.getID(), right_CG_v_id);
        HashSet<String> label_of_e = e.getLabels();



        if((v_p.getType().toString().equals("function") || v_p.getType().toString().equals("event")) && label_of_e.size() > 1){

            //**create a new auxiliary node 'v'''***//
            Vertex newGw = new Vertex(Vertex.GWType.xor, mergedModel.getIdGenerator().getNextId());
            newGw.setConfigurable(true);

            //**insert the new auxiliary node between 'right_CG_v' and its preceding vertex***********//
            ChangeOperationOnMergedModel.InsertNodeToCG(mergedModel, newGw, pre_of_right_CG_v, right_CG_v);

                 /*get the 'pre-set' of 'v_p*/
            List<Vertex> preset_of_v_p = variant.getPreset(v_p.getID());
            Vertex pre_of_v_p = preset_of_v_p.get(0);

                /*get the label on the edge between 'v_p' and its 'pre-set' in the variant before changing*/
            Edge edge = variant.containsEdge(pre_of_v_p.getID(), v_p.getID());
            HashSet<String> label_of_edge = edge.getLabels();

            /*split the common edge between the newly created auxiliary node and 'right_CG_v'*/
            ChangeOperationOnMergedModel.SplitCommonRegion(mergedModel, newGw, right_CG_v, right_CG_v, label_of_edge);

        }

        //**append a node to CG**//
        Vertex new_v_in_CG = new Vertex(new_v.getType(), new_v.getLabel(), mergedModel.getIdGenerator().getNextId());
        ChangeOperationOnMergedModel.AppendNodeToCG(mergedModel, right_CG_v, new_v_in_CG, label);


            /*****************************************************************************************/
        ///**update the edge mapping between G and CG*,very important*//

        VertexPairFromG2CG vpg2cg = new VertexPairFromG2CG(new_v.getID(), new_v_in_CG.getID(), variant.ID, mergedModel.ID);
        vertexPairFromG2CGs.add(vpg2cg);

        ///**update the edge mapping between G and CG*,very important*//

        Edge right_CG_e = mergedModel.containsEdge(right_CG_v.getID(), new_v_in_CG.getID());
        List<String> right = new ArrayList<String>();
        right.add(right_CG_e.getId());
        Edge left_G_e = variant.containsEdge(v_p.getID(), new_v.getID());
        EdgePairFromG2CG epg2cg = new EdgePairFromG2CG(left_G_e.getId(), right, variant.ID, mergedModel.ID, new Boolean(false) );
        edgePairFromG2CGs.add(epg2cg);

    }

    /*this function is responsible for propagating change to configurable process graph if one edge is deleted from process graph*/
    //input parameter 1.: merged model before change propagating
    //2. change variant before change propagating
    //3. v_p: the start node of the edge to be deleted in process variant
    //4. v_s: then end node of the edge to be deleted in process variant
    // 5. initial edge mapping between g and cg, with canonical id
    //return: void
    public static void DeleteEdgePropagation2CG (Graph mergedModel, Graph variant, Vertex v_p, Vertex v_s, List<EdgePairFromG2CG> edgePairFromG2CGs){

        //get the edge id
        Edge e = variant.containsEdge(v_p.getID(), v_s.getID());

        ChangeOperationOnProcessVariant.DeleteEdgeFromG(variant, v_p, v_s);

        //get the edge ids of the path which is correspondent to the edge (v_p,v_s)
        List<String> right_CG_e_ids = findCorrespondingEdgeSetIdInCG(e.getId(), edgePairFromG2CGs);


        //'right_CG_p' is a list which is used to save the path that corresponds to the edge (v_p,v_s) in G
        List<Edge> right_CG_p = new ArrayList<Edge>();

        //get the edges in CG by 'right_CG_e_ids', and save them into 'right_CG_p'
        List<Edge> right_CG_all_edge = mergedModel.getEdges();

        Iterator<Edge> it = right_CG_all_edge.iterator();
        while (it.hasNext()){

            Edge edge = it.next();
            if(right_CG_e_ids.contains(edge.getId())){

                right_CG_p.add(edge);
            }

        }

        //loop each edge of the path 'right_CG_p', remove the edge annotation of 'Pid(variant)' on each part of the path
        Iterator<Edge> ite = right_CG_p.iterator();

        while(ite.hasNext()){

            Edge edge = ite.next();

            //for each edge, remove the edge annotation 'pid(variant)' on it
            ChangeOperationOnMergedModel.DeleteEdgeAnnotationFromCG(mergedModel, edge.getFromVertex(), edge.getToVertex(), variant.ID);

        }

        //delete (update) edge mappings between the edge in G and path in CG from 'edgePairFromG2CGs', so we loop the 'edgePairFromG2CGs'
        Iterator<EdgePairFromG2CG> iterator = edgePairFromG2CGs.iterator();
        while(iterator.hasNext()){

            EdgePairFromG2CG edgePairFromG2CG = iterator.next();
            //delete the edge Pair, where the edge in variant is deleted
            if (edgePairFromG2CG.getLeft_G_e_id().equals(e.getId())){

                iterator.remove();
            }
        }

    }



    /**this function is used to find the corresponding vertex id in CG if we know the vertex id in G***/
    /*input: 1.vertex canonical id in G
     2. vertexPairs, with canonical id
    output: vertex canonical id in CG*/
    public static String findCorrespondingNodeIdInCG(String vertexCanonicalIdInG, List<VertexPairFromG2CG> vertexPairFromG2CGs){

        String vertex_id_in_CG = new String("");

        //loop
        Iterator<VertexPairFromG2CG> it = vertexPairFromG2CGs.iterator();

        while (it.hasNext()){

            VertexPairFromG2CG vertexPairFromG2CG = it.next();

            if(vertexPairFromG2CG.getLeft_G_v_id().equals(vertexCanonicalIdInG)){
                vertex_id_in_CG = vertexPairFromG2CG.getRight_CG_v_id();
            }
        }



        return vertex_id_in_CG;

    }

    //**this function is used to find the corresponding set of edge id in CG if we know the edge id in G**//
    /*input: 1. edge canonical id in G
    * 2. edge Pairs, with canonical id
    * output: edge (set) with canonical id in CG*/
    public static List<String> findCorrespondingEdgeSetIdInCG(String edgeCanonicalIdInG, List<EdgePairFromG2CG> edgePairFromG2CGs){

        List<String> edge_ids_in_CG = new ArrayList<String>();

        //loop
        Iterator<EdgePairFromG2CG> it = edgePairFromG2CGs.iterator();

        while(it.hasNext()){

            EdgePairFromG2CG edgePairFromG2CG = it.next();

            if(edgePairFromG2CG.getLeft_G_e_id().equals(edgeCanonicalIdInG)){

                edge_ids_in_CG = edgePairFromG2CG.getRight_CG_p_e_id();

            }

        }

        return edge_ids_in_CG;
    }

}
