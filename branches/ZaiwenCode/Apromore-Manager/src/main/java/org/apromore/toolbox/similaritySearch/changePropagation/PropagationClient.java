package org.apromore.toolbox.similaritySearch.changePropagation;

import org.apromore.toolbox.similaritySearch.algorithms.MergedModels_Result_DO;
import org.apromore.toolbox.similaritySearch.changePropagation.PropagationCommandFromG2CG.AppendNodePropagationFromG2CGCommand;
import org.apromore.toolbox.similaritySearch.common.EdgePairFromG2CG;
import org.apromore.toolbox.similaritySearch.common.VertexPairFromG2CG;
import org.apromore.toolbox.similaritySearch.common.algos.ChangeOperation;
import org.apromore.toolbox.similaritySearch.common.algos.ChangeOperationName;
import org.apromore.toolbox.similaritySearch.common.algos.FindChangeOperationSet;
import org.apromore.toolbox.similaritySearch.common.algos.MappingObjBetweenCanonicalIdAndDatabaseId;
import org.apromore.toolbox.similaritySearch.graph.Edge;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;

import java.util.*;

/**
 * Created by fengz2 on 16/09/2014.
 */
public class PropagationClient {

    //*This function below is the overall propagation algorithm from G to CG*
    // **this function is used to 1) invoke the graph differentiation function to get the change
    // operation sets between these two graphs
    // 2) invoke the change propagation module to execute the actual change propagation operation



    // the INPUT parameters of this function is
    // 1) original version of 'g1' for variant
    // 2) the changed version 'g2' for variant
    // 3) the original version of merged model 'original_cg'
    // 4,5) the original mapping between the g1 and the original_cg, with pmv id***
    // the OUTPUT of this function is
    // 1) new version of the merged model, as well as node mapping and edge mapping between variant and merged model*************///
    public static MergedModels_Result_DO start(Graph g1, Graph g2, Graph original_cg, List<VertexPairFromG2CG> vertexPairFromG2CGs, List<EdgePairFromG2CG> edgePairFromG2CGs){

            //*first, differentiate 'g1' and 'g2' to get change operation set from g1 to g2***//
        List<ChangeOperation> changeOperationList = FindChangeOperationSet.findGraphEditDistance(g1, g2);

        //Next, convert vertexPairs and edgePairs with 'pmv' id, to the pairs with canonical id, which is the INITIAL vertex mapping list between change variant and changed merged model
        List<VertexPairFromG2CG> vertexPairFromG2CGs_canonicalId = convertVertexPairsFromPmvIdToCanonicalId(vertexPairFromG2CGs,g1,original_cg);

        //In the following, convert edgePairs with 'pmv' id to pairs with canonical id, which is the INITIAL edge mapping list between change variant and changed merged model
        List<EdgePairFromG2CG> edgePairFromG2CGs_canonicalId = convertEdgePairsFromPmvIdToCanonicalId(edgePairFromG2CGs, g1,original_cg);


        //*based on the information given by 'changeOperationList', begin to construct class of Command**///
            //*set up *//
        PropagationInvoker invoker = new PropagationInvoker();

        /*loop the change operation list*/
        Iterator<ChangeOperation> it = changeOperationList.iterator();
        while (it.hasNext()){
            ChangeOperation op = it.next();
            ChangeOperationName changeOperationName = op.getChangeOperationName();
            List<String> changeOperationArgues = op.getChangeOperationArgues();

            //**add command to the list**//

            if(changeOperationName.equals(ChangeOperationName.APPENDNODETOG)){

                ///*NOTE: keep the order of arguments here in accordance with the order of arguments of change operation function*/
                String id_Of_G = changeOperationArgues.get(0);
                String ending_vertex_id = changeOperationArgues.get(1);
                Vertex new_v_to_g = g2.getVertexMap().get(ending_vertex_id);//'new_v_to_g' is the node appended to 'g1'. We will use it to set up new node mapping relationship, but 'new_v_to_g' is the node on 'g2' as well

                String starting_vertex_id = changeOperationArgues.get(2);
                Vertex v_p = g1.getVertexMap().get(starting_vertex_id); //'v_p' is the node on the 'g1', we will use it to find the corresponding node in CG, it's very important
                /*keep the order of arguments here in accordance with the order of arguments of change operation function*/

                //Here, we also have to get the identifier for the edge between 'v_p_in_g2' and 'new_v_to_g'. This edge should be on the 'g2'
                Vertex v_p_in_g2 = g2.getVertexMap().get(starting_vertex_id); //'v_p_in_g2' is the node on the 'g2', corresponding to 'v_p' in 'g1'
                Edge newEdge = g2.connectVertices(v_p_in_g2, new_v_to_g);
                HashSet<String> label_in_hashSet = newEdge.getLabels();
                String label = convertedHashSetToString(label_in_hashSet);
                String edge_id = newEdge.getId();

                /*construct the command--*/
                AppendNodePropagationFromG2CGCommand appendNodePropagationFromG2CGCommand = new AppendNodePropagationFromG2CGCommand(original_cg, g1,
                        label, edge_id, v_p, new_v_to_g, vertexPairFromG2CGs_canonicalId, edgePairFromG2CGs_canonicalId);

                /*add this command to the list of commands*/
                invoker.loadCommand(appendNodePropagationFromG2CGCommand);

            }else if(changeOperationName.equals(ChangeOperationName.PREPENDNODETOG)){


                //*To be designed*/

            }

            //**execute the commands**//
            invoker.invokeCommands();
        }



        MergedModels_Result_DO changedMergedModel = new MergedModels_Result_DO();
        changedMergedModel.setGraph(original_cg);
        changedMergedModel.setVertexMappingGandCG(vertexPairFromG2CGs_canonicalId);
        changedMergedModel.setEdgeMappingGandCG(edgePairFromG2CGs_canonicalId);

        return changedMergedModel;

    }

    //convert vertexPairs with 'pmv' id, to the pairs with canonical id
    public static List<VertexPairFromG2CG> convertVertexPairsFromPmvIdToCanonicalId(List<VertexPairFromG2CG> vertexPairFromG2CGs, Graph g, Graph cg){
        List<VertexPairFromG2CG> vertexPairFromG2CGs_canonicalId = new ArrayList<VertexPairFromG2CG>();

        MappingObjBetweenCanonicalIdAndDatabaseId[] nodeMappingBetweenCanonicalIdAndDatabaseIds_inG = convertMapToObjArray(g.getNode_id_mapping());
        MappingObjBetweenCanonicalIdAndDatabaseId[] nodeMappingBetweenCanonicalIdAndDatabaseIds_inCG = convertMapToObjArray(cg.getNode_id_mapping());

        //loop the 'vertexPairFromG2CGs'
        Iterator<VertexPairFromG2CG> it = vertexPairFromG2CGs.iterator();

        while (it.hasNext()){
            VertexPairFromG2CG vertexPairFromG2CG = it.next();

            String left_pmv_id = vertexPairFromG2CG.getLeft_G_v_id();
            String right_pmv_id = vertexPairFromG2CG.getRight_CG_v_id();
            String pid_g = vertexPairFromG2CG.getPid_G();
            String pid_cg = vertexPairFromG2CG.getPid_CG();

            String left_canonical_id = new String("");
            String right_canonical_id = new String("");

            //convert 'left_pmv_id' to 'left_canonical_id'
            for(MappingObjBetweenCanonicalIdAndDatabaseId mappingObjBetweenCanonicalIdAndDatabaseId : nodeMappingBetweenCanonicalIdAndDatabaseIds_inG){

                if(mappingObjBetweenCanonicalIdAndDatabaseId.getDatabase_id().equals(left_pmv_id)){
                    left_canonical_id = mappingObjBetweenCanonicalIdAndDatabaseId.getCanonical_id();
                }

            }

            //convert 'right_pmv_id' to 'right_canonical_id'
            for(MappingObjBetweenCanonicalIdAndDatabaseId mappingObjBetweenCanonicalIdAndDatabaseId : nodeMappingBetweenCanonicalIdAndDatabaseIds_inCG){

                if(mappingObjBetweenCanonicalIdAndDatabaseId.getDatabase_id().equals(right_pmv_id)){
                    right_canonical_id = mappingObjBetweenCanonicalIdAndDatabaseId.getCanonical_id();
                }
            }

            VertexPairFromG2CG vertexPairFromG2CG_canonicalId = new VertexPairFromG2CG(left_canonical_id, right_canonical_id, pid_g, pid_cg);
            vertexPairFromG2CGs_canonicalId.add(vertexPairFromG2CG_canonicalId);

        }



        return vertexPairFromG2CGs_canonicalId;
    }


    //convert edgePairs with 'pmv' id, to the pairs with canonical id
    public static List<EdgePairFromG2CG> convertEdgePairsFromPmvIdToCanonicalId(List<EdgePairFromG2CG> edgePairFromG2CGs, Graph g, Graph cg){
        List<EdgePairFromG2CG> edgePairFromG2CGs_canonicalId = new ArrayList<EdgePairFromG2CG>();

        MappingObjBetweenCanonicalIdAndDatabaseId[] edgeMappingBetweenCanonicalIdAndDatabaseIds_inG = convertMapToObjArray(g.getEdge_id_mapping());
        MappingObjBetweenCanonicalIdAndDatabaseId[] edgeMappingBetweenCanonicalIdAndDatabaseIds_inCG = convertMapToObjArray(cg.getEdge_id_mapping());

        //loop the 'vertexPairFromG2CGs'
        Iterator<EdgePairFromG2CG> it = edgePairFromG2CGs.iterator();

        while (it.hasNext()){
            EdgePairFromG2CG edgePairFromG2CG = it.next();

            String left_pmv_id = edgePairFromG2CG.getLeft_G_e_id();
            List<String> right_pmv_ids = edgePairFromG2CG.getRight_CG_p_e_id();
            String pid_g = edgePairFromG2CG.getPid_G();
            String pid_cg = edgePairFromG2CG.getPid_CG();
            Boolean is_multiple_mapping = edgePairFromG2CG.getIs_part_of_mapping();

            String left_canonical_id = new String("");
            List<String> right_canonical_ids = new ArrayList<String>();

            //convert 'left_pmv_id' to 'left_canonical_id' for edges
            for(MappingObjBetweenCanonicalIdAndDatabaseId mappingObjBetweenCanonicalIdAndDatabaseId : edgeMappingBetweenCanonicalIdAndDatabaseIds_inG){

                if(mappingObjBetweenCanonicalIdAndDatabaseId.getDatabase_id().equals(left_pmv_id)){
                    left_canonical_id = mappingObjBetweenCanonicalIdAndDatabaseId.getCanonical_id();
                }

            }

            //convert a list 'right_pmv_ids' to 'right_canonical_id' for edges
                //loop
            Iterator<String> ite = right_pmv_ids.iterator();
            while(ite.hasNext()){

                String right_pmv_id = ite.next();
                String right_canonical_id = new String("");

                for(MappingObjBetweenCanonicalIdAndDatabaseId mappingObjBetweenCanonicalIdAndDatabaseId : edgeMappingBetweenCanonicalIdAndDatabaseIds_inCG){


                    if(mappingObjBetweenCanonicalIdAndDatabaseId.getDatabase_id().equals(right_pmv_id)){
                        right_canonical_id = mappingObjBetweenCanonicalIdAndDatabaseId.getCanonical_id();
                    }
                }
                right_canonical_ids.add(right_canonical_id);
            }



            EdgePairFromG2CG edgePairFromG2CG_canonicalId = new EdgePairFromG2CG(left_canonical_id, right_canonical_ids, pid_g, pid_cg, is_multiple_mapping);
            edgePairFromG2CGs_canonicalId.add(edgePairFromG2CG_canonicalId);

        }



        return edgePairFromG2CGs_canonicalId;
    }

    //convert maps to object array for edge mapping and node mapping
    public static MappingObjBetweenCanonicalIdAndDatabaseId[] convertMapToObjArray(Map<String, String> map){

        MappingObjBetweenCanonicalIdAndDatabaseId[] mappingArray = new MappingObjBetweenCanonicalIdAndDatabaseId[map.size()];

        //'i' is pointer to array
        int i = 0;

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()){



            Map.Entry pairs  = (Map.Entry)it.next();
            String canonical_id = (String)pairs.getKey();
            String database_id = (String)pairs.getValue();

            //create obj
            MappingObjBetweenCanonicalIdAndDatabaseId mappingObj = new MappingObjBetweenCanonicalIdAndDatabaseId();
            mappingObj.setCanonical_id(canonical_id);
            mappingObj.setDatabase_id(database_id);

            mappingArray[i] = mappingObj;

            i++;

            it.remove();

        }

        return mappingArray;

    }

    //covert a hash set which saves labels on the edge, into a comma separated string
    public static String convertedHashSetToString(HashSet<String> label_in_hashSet){

        String separator = ",";
        int total = label_in_hashSet.size()*separator.length();
        for(String s : label_in_hashSet){
            total += s.length();
        }

        StringBuilder sb = new StringBuilder(total);
        for(String s : label_in_hashSet){

            sb.append(separator).append(s);
        }

        String result = sb.substring(separator.length());//remove the leading separator

        return result;
    }

}
