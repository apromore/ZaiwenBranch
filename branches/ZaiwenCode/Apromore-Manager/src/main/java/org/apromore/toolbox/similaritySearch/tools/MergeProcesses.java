package org.apromore.toolbox.similaritySearch.tools;

import org.apromore.service.model.MergeData;
import org.apromore.toolbox.similaritySearch.algorithms.MergeModels;
import org.apromore.toolbox.similaritySearch.algorithms.MergedModels_Result_DO;
import org.apromore.toolbox.similaritySearch.common.CPFModelParser;
import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;
import org.apromore.toolbox.similaritySearch.graph.Graph;

import java.util.ArrayList;

/*Revised by Zaiwen FENG from 1ST July 2014*/

public class MergeProcesses {

    /**
     * Finds the Processes Similarity.
     * @param models    the Canonical Process Type
     * @param removeEnt The Canonical Process Type
     * @param algorithm the search Algorithm
     * @param threshold the search merge threshold
     * @param param     the search parameters
     * @return the similarity between processes
     */
    public static MergeProcesses_Result_DO mergeProcesses(ArrayList<MergeData> models, boolean removeEnt, String algorithm,
        double threshold, double... param) {

        /*Declare the result data object for the class*/
        MergeProcesses_Result_DO mergeProcessesResultDo = new MergeProcesses_Result_DO();

//        ArrayList<CanonicalProcessType> cpt = new ArrayList<>(models.values());
//        ArrayList<ProcessModelVersion> pmv = new ArrayList<>(models.keySet());

        IdGeneratorHelper idGenerator = new IdGeneratorHelper();
        Graph m1 = CPFModelParser.readModel(models.get(0).getCanonicalProcessType(), models.get(0).getNode_id_mapping(), models.get(0).getEdge_id_mapping());
        m1.setIdGenerator(idGenerator);
        m1.ID = String.valueOf(models.get(0).getModel_id());
        m1.removeEmptyNodes();
 //       m1.reorganizeIDs();

        Graph m2 = CPFModelParser.readModel(models.get(1).getCanonicalProcessType(), models.get(1).getNode_id_mapping(), models.get(1).getEdge_id_mapping());
        m2.setIdGenerator(idGenerator);
        m2.ID = String.valueOf(models.get(1).getModel_id());
        m2.removeEmptyNodes();
//        m2.reorganizeIDs();


        m1.addLabelsToUnNamedEdges();
        m2.addLabelsToUnNamedEdges();

//        Graph merged = MergeModels.mergeModels(m1, m2, idGenerator, removeEnt, algorithm, param);
        MergedModels_Result_DO mergeModelsResultDo = MergeModels.mergeModels(m1, m2, idGenerator, removeEnt, algorithm, param);

        if (models.size() > 2) {
            for (int i = 2; i < models.size(); i++) {
                Graph m3 = CPFModelParser.readModel(models.get(i).getCanonicalProcessType(), models.get(i).getNode_id_mapping(), models.get(i).getEdge_id_mapping());
                m3.setIdGenerator(idGenerator);
                m3.removeEmptyNodes();
                m3.reorganizeIDs();
                m3.addLabelsToUnNamedEdges();

                // merged = MergeModels.mergeModels(merged, m3, idGenerator, removeEnt, algorithm, param);
                mergeModelsResultDo = MergeModels.mergeModels(m1, m2, idGenerator, removeEnt, algorithm, param);
            }

        }

        /*Elicits the merged model from result of 'mergeModelsResultDo'*/
        Graph merged = mergeModelsResultDo.getGraph();
        /*Fill the result data object*/
        mergeProcessesResultDo.setVertexPairFromG2CGs(mergeModelsResultDo.getVertexMappingGandCG());
        mergeProcessesResultDo.setEdgePairFromG2CGs(mergeModelsResultDo.getEdgeMappingGandCG());
        mergeProcessesResultDo.setCanonicalProcessType(CPFModelParser.writeModel(merged, idGenerator));
        return mergeProcessesResultDo;
    }
}
