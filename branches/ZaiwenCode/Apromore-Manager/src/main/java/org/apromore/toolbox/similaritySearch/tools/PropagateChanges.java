package org.apromore.toolbox.similaritySearch.tools;

import org.apromore.dao.model.EdgeMappingGandCG;
import org.apromore.dao.model.NodeMappingGandCG;
import org.apromore.service.model.MergeData;
import org.apromore.toolbox.similaritySearch.algorithms.MergedModels_Result_DO;
import org.apromore.toolbox.similaritySearch.changePropagation.PropagationClient;
import org.apromore.toolbox.similaritySearch.common.CPFModelParser;
import org.apromore.toolbox.similaritySearch.common.EdgePairFromG2CG;
import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;
import org.apromore.toolbox.similaritySearch.common.VertexPairFromG2CG;
import org.apromore.toolbox.similaritySearch.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengz2 on 2/10/2014.
 */
public class PropagateChanges {

    /*Propagates changes from variants to merged model
    * @param models: a set of canonical models which includes the original process variant, the changed variant and the original merged model
    * @param nodeMappingGandCGs: node mappings between G and CG, the node refers to database node
    * @param edgeMappingGandCGs: edge mappings between G and CG, the edge refers to database edges
    * @return changedMergedCanonicalModel: the changed merged canonical model, which includes the changed canonical merged  models, the updated vertex and  edge mappings*/


    public static MergeProcesses_Result_DO propagateChangesFromG2CG(ArrayList<MergeData> models, List<NodeMappingGandCG> nodeMappingGandCGs, List<EdgeMappingGandCG> edgeMappingGandCGs ){

                /*Declare the result data object for the class*/
        MergeProcesses_Result_DO changedMergedCanonicalModel = new MergeProcesses_Result_DO();

        IdGeneratorHelper idGenerator = new IdGeneratorHelper();

        //note: the id of nodes and edges are canonical id
//        Graph originalVariantGraph = CPFModelParser.readModel(models.get(0).getCanonicalProcessType(),models.get(0).getNode_id_mapping(),models.get(0).getEdge_id_mapping());
        Graph originalVariantGraph = CPFModelParser.readModel(models.get(0).getCanonicalProcessType());//with canonical ids
        originalVariantGraph.setIdGenerator(idGenerator);
        originalVariantGraph.ID = String.valueOf(models.get(0).getModel_id());
        originalVariantGraph.removeEmptyNodes();

//        Graph changedVariantGraph = CPFModelParser.readModel(models.get(1).getCanonicalProcessType(),models.get(1).getNode_id_mapping(),models.get(1).getEdge_id_mapping());
        Graph changedVariantGraph = CPFModelParser.readModel(models.get(1).getCanonicalProcessType());//with canonical ids
        changedVariantGraph.setIdGenerator(idGenerator);
        changedVariantGraph.ID = String.valueOf(models.get(1).getModel_id());
        changedVariantGraph.removeEmptyNodes();

//        Graph originalMergedGraph = CPFModelParser.readModel(models.get(2).getCanonicalProcessType(),models.get(2).getNode_id_mapping(),models.get(2).getEdge_id_mapping());
        Graph originalMergedGraph = CPFModelParser.readModel(models.get(2).getCanonicalProcessType()); //with canonical ids
        originalMergedGraph.setIdGenerator(idGenerator);
        originalMergedGraph.ID = String.valueOf(models.get(2).getModel_id());
        originalMergedGraph.removeEmptyNodes();

        /////***here we should get the mappings between canonical id and databased id for nodes and edges....
        originalVariantGraph.setNode_id_mapping(models.get(0).getNode_id_mapping());
        originalVariantGraph.setEdge_id_mapping(models.get(0).getEdge_id_mapping());

        changedVariantGraph.setNode_id_mapping(models.get(1).getNode_id_mapping());
        changedVariantGraph.setEdge_id_mapping(models.get(1).getEdge_id_mapping());

        originalMergedGraph.setNode_id_mapping(models.get(2).getNode_id_mapping());
        originalMergedGraph.setEdge_id_mapping(models.get(2).getEdge_id_mapping());

        //above. we should get the mappings between canonical id and databased id for nodes and edges..

        originalVariantGraph.addLabelsToUnNamedEdges();
        changedVariantGraph.addLabelsToUnNamedEdges();
        originalMergedGraph.addLabelsToUnNamedEdges();


                    /*convert the node mapping beans to node pairs*/
        List<VertexPairFromG2CG> vertexPairFromG2CGs= convertNodeMappingToVertexPair(nodeMappingGandCGs);

                /*convert the edge mapping beans to edge pairs*/
        List<EdgePairFromG2CG> edgePairFromG2CGs = convertEdgeMappingToEdgePair(edgeMappingGandCGs);

        MergedModels_Result_DO changedMergedGraphModel = PropagationClient.start(originalVariantGraph, changedVariantGraph, originalMergedGraph,
                vertexPairFromG2CGs, edgePairFromG2CGs);

                /*Elicits the merged model from result of 'mergeModelsResultDo'*/
        Graph changedMergedGraph = changedMergedGraphModel.getGraph();
        /*Fill the result data object*/
        changedMergedCanonicalModel.setVertexPairFromG2CGs(changedMergedGraphModel.getVertexMappingGandCG());
        changedMergedCanonicalModel.setEdgePairFromG2CGs(changedMergedGraphModel.getEdgeMappingGandCG());
        changedMergedCanonicalModel.setCanonicalProcessType(CPFModelParser.writeModel(changedMergedGraph, idGenerator));

        return changedMergedCanonicalModel;

    }





    /*convert the node mapping beans to node pairs*/
    private static List<VertexPairFromG2CG> convertNodeMappingToVertexPair(List<NodeMappingGandCG> nodeMappingGandCGs){
        List<VertexPairFromG2CG> vertexPairFromG2CGs = new ArrayList<VertexPairFromG2CG>();

        for(NodeMappingGandCG nodeMappingGandCG : nodeMappingGandCGs){

            String left_G_v_id = nodeMappingGandCG.getId_G_n();
            String right_CG_v_id = nodeMappingGandCG.getId_CG_n();
            String pid_G = nodeMappingGandCG.getPID_G();
            String pid_CG = nodeMappingGandCG.getPID_CG();

            VertexPairFromG2CG vertexPairFromG2CG = new VertexPairFromG2CG(left_G_v_id, right_CG_v_id, pid_G, pid_CG);
            vertexPairFromG2CGs.add(vertexPairFromG2CG);
        }
        return vertexPairFromG2CGs;
    }





    /*convert the edge mapping beans to edge pairs*/
    private static List<EdgePairFromG2CG> convertEdgeMappingToEdgePair(List<EdgeMappingGandCG> edgeMappingGandCGs){

        List<EdgePairFromG2CG> edgePairFromG2CGs = new ArrayList<EdgePairFromG2CG>();
        /*covert the list 'edgeMappingGandCGs' to an array*/
        EdgeMappingGandCG[] edgeMappingGandCGsArray = edgeMappingGandCGs.toArray(new EdgeMappingGandCG[edgeMappingGandCGs.size()]);

        /*loop the the list of 'edgeMappingGandCGs'*/
        for(int i=0; i<edgeMappingGandCGsArray.length; i++){

            EdgeMappingGandCG edgeMappingGandCG = edgeMappingGandCGsArray[i];

            Integer id = edgeMappingGandCG.getId();
            String id_CG_e = edgeMappingGandCG.getId_CG_e();
            String id_G_e = edgeMappingGandCG.getId_G_e();
            String pid_CG = edgeMappingGandCG.getPID_CG();
            String pid_G = edgeMappingGandCG.getPID_G();
            boolean part_of_mapping = edgeMappingGandCG.getPart_of_mapping();


            if(!id_G_e.equals("visited")){

                List<String> right_CG_p_e_id = new ArrayList<String>();
                //Normally, the right_cg_p_e just contains only one corresponding edge in CG
                right_CG_p_e_id.add(id_CG_e);

                        /*loop the list of 'edgeMappingGandCGs' again to merge multiple mapping, i.e. one edge in G
            * is mapped to multiple edges in CG*/
                for(int j=0; j<edgeMappingGandCGsArray.length; j++){

                    String id_CG_e_cp = edgeMappingGandCGsArray[j].getId_CG_e();
                    String id_G_e_cp = edgeMappingGandCGsArray[j].getId_G_e();

                    //if both edge id in G and edge id in CG are same as, set the element already 'visited'
                    if(id_G_e.equals(id_G_e_cp) && id_CG_e.equals(id_CG_e_cp)){

                        EdgeMappingGandCG visited = new EdgeMappingGandCG();
                        visited.setId(0);
                        visited.setId_G_e(new String("visited"));
                        visited.setId_CG_e(new String("visited"));
                        visited.setPID_G(new String("visited"));
                        visited.setPID_CG(new String("visited"));
                        visited.setPart_of_mapping(false);
                        edgeMappingGandCGsArray[j] = visited;

                    }else if( id_G_e.equals(id_G_e_cp) && !id_CG_e.equals(id_CG_e_cp)){

                    /*merge multiple mapping between G and CG.
                    NOTE: the edges has not sorted  so far, maybe it should be sorted ....*/
                        right_CG_p_e_id.add(id_CG_e_cp);
                        /*Then, set the element 'already visited' to avoid more comparison*/
                        EdgeMappingGandCG visited = new EdgeMappingGandCG();
                        visited.setId(0);
                        visited.setId_G_e(new String("visited"));
                        visited.setId_CG_e(new String("visited"));
                        visited.setPID_G(new String("visited"));
                        visited.setPID_CG(new String("visited"));
                        visited.setPart_of_mapping(false);
                        edgeMappingGandCGsArray[j] = visited;
                    }

                }

                EdgePairFromG2CG edgePairFromG2CG = new EdgePairFromG2CG(id_G_e, right_CG_p_e_id, pid_G, pid_CG, part_of_mapping);
                edgePairFromG2CGs.add(edgePairFromG2CG);
            }


        }
        return edgePairFromG2CGs;
    }




}
