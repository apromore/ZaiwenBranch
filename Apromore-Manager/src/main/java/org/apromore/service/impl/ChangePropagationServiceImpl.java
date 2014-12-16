package org.apromore.service.impl;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.exception.ExceptionChangePropagation;
import org.apromore.exception.ImportException;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.SerializationException;
import org.apromore.helper.Version;
import org.apromore.model.ProcessVersionIdType;
import org.apromore.service.CanoniserService;
import org.apromore.service.ChangePropagationService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.MergeData;
import org.apromore.service.model.ToolboxData;
import org.apromore.toolbox.similaritySearch.common.EdgePairFromG2CG;
import org.apromore.toolbox.similaritySearch.common.VertexPairFromG2CG;
import org.apromore.toolbox.similaritySearch.tools.MergeProcesses_Result_DO;
import org.apromore.toolbox.similaritySearch.tools.PropagateChanges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * Created by fengz2 on 1/10/2014.
 */
@Service
public class ChangePropagationServiceImpl implements ChangePropagationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangePropagationServiceImpl.class);

    private ProcessModelVersionRepository processModelVersionRepo;
    private CanoniserService canoniserSrv;
    private ProcessService processSrv;
    private NodeRepository nodeSrv;
    private EdgeRepository edgeSrv;
    private NodeMappingGandCGRepository nodeMappingGandCGRepo;
    private EdgeMappingGandCGRepository edgeMappingGandCGRepo;
    private ProcessRepository processRepo;
    private SecurityService securitySrv;
    private ProcessBranchRepository processBranchRepository;

    private List<ProcessModelVersion> models = new ArrayList<ProcessModelVersion>();


    /*Default Constructor allowing Spring to Autowire for testing and normal use*/
    /**
     * @param processModelVersionRepository*
     * @param canoniserService
     * @param processService
     * @param  nodeRepository
     * @param  edgeRepository
     * @param nodeMappingGandCGRepository
     * @param edgeMappingGandCGRepository*/

    @Inject
    public ChangePropagationServiceImpl(final ProcessModelVersionRepository processModelVersionRepository, final CanoniserService canoniserService, final ProcessService processService,
                                        final NodeMappingGandCGRepository nodeMappingGandCGRepository, final EdgeMappingGandCGRepository edgeMappingGandCGRepository, final NodeRepository nodeRepository,
                                        final EdgeRepository edgeRepository, final ProcessRepository processRepository, final SecurityService securityService, final ProcessBranchRepository branchRepository){

        processModelVersionRepo = processModelVersionRepository;
        canoniserSrv = canoniserService;
        processSrv = processService;
        nodeMappingGandCGRepo = nodeMappingGandCGRepository;
        edgeMappingGandCGRepo = edgeMappingGandCGRepository;
        nodeSrv = nodeRepository;
        edgeSrv = edgeRepository;
        processRepo = processRepository;
        securitySrv = securityService;
        processBranchRepository = branchRepository;
    }




    public List<ProcessModelVersion> changePropagationFromGtoCG(String username, ProcessVersionIdType originalVariantVersion,
                                                          ProcessVersionIdType changedVariantVersion, String newBranchName,
                                                          Version newVersionForMergedModel,NativeType natType) throws ExceptionChangePropagation {
        List<ProcessModelVersion> changedMergedModelsVersion = new ArrayList<ProcessModelVersion>();

        ProcessModelVersion model_original_variant = processModelVersionRepo.getProcessModelVersion(originalVariantVersion.getProcessId(), originalVariantVersion.getBranchName(),
                originalVariantVersion.getVersionNumber());

        ProcessModelVersion model_changed_variant = processModelVersionRepo.getProcessModelVersion(changedVariantVersion.getProcessId(), changedVariantVersion.getBranchName(),
                changedVariantVersion.getVersionNumber());

        //here, we should get the pmv list of the original merged model, which should be changed , based on the pmv of the original process variant.
        String pmv_id_original_variant = model_original_variant.getId().toString();
        List<String> pmv_ids_original_merged = nodeMappingGandCGRepo.findMergedModelIdByVariantId(pmv_id_original_variant);
        List<ProcessModelVersion> models_original_merged = new ArrayList<ProcessModelVersion>();
        for(String pmv_id_original_merged : pmv_ids_original_merged){

            models_original_merged.add(processModelVersionRepo.getProcessModelVersionById(Integer.parseInt(pmv_id_original_merged)));
        }


        //loop the list of the original merged model version, for each, update the process
        for(ProcessModelVersion model_original_merged : models_original_merged){
            //clear the 'models'
            models.clear();

            models.add(model_original_variant);
            models.add(model_changed_variant);
            models.add(model_original_merged); //note that 'models' does imply the sequence of models, 1. original variant; 2. changed variant; 3. original merged

            ProcessModelVersion pmv = null;

            try{

                ToolboxData data = convertModelsToCPT(models);

                //*get the pmv id of the original model variant*//
                Integer pmv_id_model_original_variant = model_original_variant.getId();

                //*get the pmv id of the original merged model*//
                Integer pmv_id_model_original_merged = model_original_merged.getId();

                //get the process name of the original merged model
                Integer process_BranchId_original_merged = model_original_merged.getProcessBranch().getId();
                Integer processId_original_merged = processBranchRepository.FindProcessByBranchId(process_BranchId_original_merged).getId();
                String name_original_merged = processRepo.findUniqueById(processId_original_merged).getName();
//            String name_original_merged = "process100";

                //get the branch name of the original merged model
                String branch_name_original_merged = model_original_merged.getProcessBranch().getBranchName();

                //create the version number of the original merged model
                String version_number_original_merged = model_original_merged.getVersionNumber();
                Version originalVersion = new Version(version_number_original_merged);

                //get the new version number of process to be evolved.
//            Version newVersion = new Version(newVersionNumber);

                //create user who updates the process
                User user = securitySrv.getUserByName(username);

                //get the lock status for the original merged process
                String lockStatus = model_original_merged.getLockStatus().toString();

                //get the native type of the original merged model
                //           NativeType nativeType = model_original_merged.getNativeType();

                List<NodeMappingGandCG> nodeMappingGandCGs;

            /*get the original node mappings between the original process variant G and the original merged model CG*/
                nodeMappingGandCGs=
                        nodeMappingGandCGRepo.findNodeMappingBetweenGAndCGByVariantIdAndMergedId(pmv_id_model_original_variant.toString(), pmv_id_model_original_merged.toString());

                List<EdgeMappingGandCG> edgeMappingGandCGs;

            /*get the original edge mapping between the original process variant G and the original merged model CG*/
                edgeMappingGandCGs = edgeMappingGandCGRepo.findEdgeMappingBetweenGAndCGByVariantIdAndMergedId(pmv_id_model_original_variant.toString(), pmv_id_model_original_merged.toString());


            /*invoke, and get the evolved process cpf*/
                MergeProcesses_Result_DO changedMergedCanonicalModel = performChangePropagation(data, nodeMappingGandCGs, edgeMappingGandCGs);

                CanonisedProcess cp = new CanonisedProcess();
                cp.setCpt(changedMergedCanonicalModel.getCanonicalProcessType());
                cp.setCpf(new ByteArrayInputStream(canoniserSrv.CPFtoString(cp.getCpt()).getBytes()));

                //update the process targeted to be evolved.
                pmv = processSrv.updateProcess(processId_original_merged, name_original_merged, branch_name_original_merged, newBranchName, newVersionForMergedModel,
                        originalVersion, user, lockStatus, natType, cp);

                String pid_cg = pmv.getId().toString();

                String pid_g = model_changed_variant.getId().toString();

                            /*declare an array list for storing vertex mapping*/
                List<VertexPairFromG2CG> vertexPairFromG2CGs = changedMergedCanonicalModel.getVertexPairFromG2CGs();

        /*declare an array list for storing edge mapping*/
                List<EdgePairFromG2CG> edgePairFromG2CGs = changedMergedCanonicalModel.getEdgePairFromG2CGs();

                    /*get an iterator*/
                Iterator<VertexPairFromG2CG> ite = vertexPairFromG2CGs.iterator();
                int i = 60000;
        /*fill Node Mapping beans and persist them into data base*/
                while(ite.hasNext()){
                    VertexPairFromG2CG vpg2cg = ite.next();

                    NodeMappingGandCG np = new NodeMappingGandCG();

                    np.setId(new Integer(i));

                /*UPDATE NODE IN IN G INFORMATION: here need to transform the left_g_v_id (canonical node id) in cpf to 'node id' in table of 'node'*/
                    String uri_G_n = vpg2cg.getLeft_G_v_id();
                    FragmentVersion fv_g = model_changed_variant.getRootFragmentVersion();
                    Integer fv_g_id = fv_g.getId();
                    Node node_in_g  = nodeSrv.findNodeByUriAndFragmentVersion(uri_G_n, fv_g_id);
                    Integer node_in_g_id = node_in_g.getId();
                    np.setId_G_n(node_in_g_id.toString());

                    np.setPID_G(pid_g);

                    //np.setId_CG_n(vpg2cg.getRight_CG_v_id());
                /*UPDATE NODE ID IN CG INFORMATION: here need to transform the right_cg_v_id (canonical node id) to 'node id' in table of 'node'**/
                    String uri_CG_n = vpg2cg.getRight_CG_v_id();
                           /*get root_fragment_version of merged model by pmv*/
                    FragmentVersion fv = pmv.getRootFragmentVersion();
           /*get root_fragment_version_id from root_fragment_version*/
                    Integer fv_id = fv.getId();
                    Node node = nodeSrv.findNodeByUriAndFragmentVersion(uri_CG_n, fv_id);
                    Integer node_id = node.getId();
                    //**save node_id into the bean**//
                    np.setId_CG_n(node_id.toString());

                    np.setPID_CG(pid_cg);
             /*persistence of node mapping data between variants and merged model*/
                    nodeMappingGandCGRepo.save(np);
                    i++;
                }

                /*get an iterator of 'EdgePairFromG2CG' and then loop it*/
                Iterator<EdgePairFromG2CG> ite_edge = edgePairFromG2CGs.iterator();
                int j = 60000;
        /*fill Edge Mapping beans and persist them into data base*/
                while(ite_edge.hasNext()){
                    EdgePairFromG2CG epg2cg = ite_edge.next();
                    List<String> right_CG_p_e_id = new ArrayList<String>();
                    right_CG_p_e_id = epg2cg.getRight_CG_p_e_id();

                    if(right_CG_p_e_id.size() == 1){

                        EdgeMappingGandCG ep = new EdgeMappingGandCG();
                        ep.setId(new Integer(j));

                    /*UPDATE EDGE ID IN G INFORMATION: here need to transform the left_g_v_id (canonical edge id) to 'edge id' in database table of 'edge'**/
                        String canonicalId_G_e=epg2cg.getLeft_G_e_id();
                        FragmentVersion fv_g = model_changed_variant.getRootFragmentVersion();
                        Integer fv_g_id = fv_g.getId();
                        Edge edge_in_g = edgeSrv.findEdgeByOriginalIdAndFragmentVersion(canonicalId_G_e, fv_g_id);
                        Integer edge_id_in_g = edge_in_g.getId();
                        ep.setId_G_e(edge_id_in_g.toString());

                        ep.setPID_G(pid_g);
                        //                   ep.setId_CG_e(epg2cg.getRight_CG_p_e_id().get(0));

                    /*UPDATE EDGE ID IN CG INFORMATION: here need to transform the right_cg_v_id (canonical edge id) to 'edge id' in database table of 'edge'**/
                        String originalId_CG_e = epg2cg.getRight_CG_p_e_id().get(0);
                           /*get root_fragment_version of merged model by pmv*/
                        FragmentVersion fv = pmv.getRootFragmentVersion();
           /*get root_fragment_version_id from root_fragment_version*/
                        Integer fv_id = fv.getId();
                        Edge edge = edgeSrv.findEdgeByOriginalIdAndFragmentVersion(originalId_CG_e, fv_id);
                        Integer edge_id = edge.getId();
                        //**save edge_id into the bean**//
                        ep.setId_CG_e(edge_id.toString());

                        ep.setPID_CG(pid_cg);
                        ep.setPart_of_mapping(false);
                        edgeMappingGandCGRepo.save(ep);
                        j++;
                    }else{
                            /*if the size of 'right_cg_p_e_id' is more than one, get the iterator for 'right_cg_p_e_id' and then loop it*/
                        Iterator<String> it = right_CG_p_e_id.iterator();

                        while (it.hasNext()){

                            String right_CG_e_id = it.next();

                            EdgeMappingGandCG ep = new EdgeMappingGandCG();
                            ep.setId(new Integer(j));

                        /*UPDATE EDGE ID IN G INFORMATION: here need to transform the left_g_v_id (canonical edge id) to 'edge id' in database table of 'edge'**/
                            String canonicalId_G_e=epg2cg.getLeft_G_e_id();
                            FragmentVersion fv_g = model_changed_variant.getRootFragmentVersion();
                            Integer fv_g_id = fv_g.getId();
                            Edge edge_in_g = edgeSrv.findEdgeByOriginalIdAndFragmentVersion(canonicalId_G_e, fv_g_id);
                            Integer edge_id_in_g = edge_in_g.getId();
                            ep.setId_G_e(edge_id_in_g.toString());

                            ep.setPID_G(pid_g);
                            //ep.setId_CG_e(right_CG_e_id);

                        /*UPDATE EDGE ID IN CG INFORMATION: here need to transform the right_cg_v_id (canonical edge id) to 'edge id' in database table of 'edge'**/
                                             /*get root_fragment_version of merged model by pmv*/
                            FragmentVersion fv = pmv.getRootFragmentVersion();
           /*get root_fragment_version_id from root_fragment_version*/
                            Integer fv_id = fv.getId();
                            Edge edge = edgeSrv.findEdgeByOriginalIdAndFragmentVersion(right_CG_e_id, fv_id);
                            Integer edge_id = edge.getId();
                            //**save edge_id into the bean**//
                            ep.setId_CG_e(edge_id.toString());


                            ep.setPID_CG(pid_cg);
                            ep.setPart_of_mapping(true);

                /*persistence of node mapping data between variants and merged model*/
                            edgeMappingGandCGRepo.save(ep);
                            j++;
                        }
                    }
                }





            }catch (SerializationException se){

                LOGGER.error("Failed to convert the models into the Canonical Format.", se);
            }catch (ImportException | JAXBException ie) {
                LOGGER.error("Failed Import the newly changed merged model after change propagation.", ie);
            }catch (RepositoryException re){

                LOGGER.error("Data base related errors." , re);
            }

            changedMergedModelsVersion.add(pmv);

        }

        return changedMergedModelsVersion;

    }


    /* Responsible for getting all the Models and converting them to CPT internal format */
    private ToolboxData convertModelsToCPT(List<ProcessModelVersion> models) throws SerializationException {
        ToolboxData data = new ToolboxData();

        for (ProcessModelVersion pmv : models) {
            data.addModel(pmv, processSrv.getCanonicalFormat(pmv));
        }

        return data;
    }





    /*Does change propagation*/

    private MergeProcesses_Result_DO performChangePropagation(ToolboxData data,
                                                              List<NodeMappingGandCG> nodeMappingGandCGs, List<EdgeMappingGandCG> edgeMappingGandCGs ){

        Map<ProcessModelVersion, CanonicalProcessType> models_original = new HashMap<>(data.getModel());//note that this 'models_original' does not consider the sequence of models,

        ArrayList<MergeData> mergeData = new ArrayList<MergeData>();//note that 'mergeData' does consider the sequence of models

        /*loop the map named 'models_original'*/
        for(int i = 0; i<models_original.size(); i++){

            //get pmv from 'models', note sequence of models is implied in it.
            ProcessModelVersion pmv = models.get(i);

            //get 'canonical process type' from map named 'models_original'
            CanonicalProcessType cpt = models_original.get(pmv);

            Integer pmv_id = pmv.getId();

            /*get root_fragment_version by pmv*/
            FragmentVersion fv = pmv.getRootFragmentVersion();
           /*get root_fragment_version_uri from root_fragment_version*/
            String fv_uri = fv.getUri();

                       /*get node set in a fragment version*/
            List<Node> nodes = nodeSrv.getNodesByFragmentURI(fv_uri);
           /*set up a hash map to record the mapping between node_id_in_canonical (uri) and node id in NODE TABLE*/
            HashMap<String, String> node_id_mapping = new HashMap<String, String>();
            Iterator<Node> ite = nodes.iterator();/*get the iterator*/
            while(ite.hasNext()){

                Node node = ite.next();
                node_id_mapping.put(node.getUri(), node.getId().toString());
            }

           /*get edge set in a fragment version*/
            List<Edge> edges = edgeSrv.getEdgesByFragmentURI(fv_uri);
           /*set up a hash map to record the mapping between edge_id_in_canonical (uri) and edge id in EDGE TABLE*/
            HashMap<String, String> edge_id_mapping = new HashMap<String, String>();
            Iterator<Edge> ite_edge = edges.iterator();/*get the iterator*/
            while (ite_edge.hasNext()){

                Edge edge = ite_edge.next();
                edge_id_mapping.put(edge.getOriginalId(), edge.getId().toString());
            }

            MergeData model = new MergeData();
            model.setModel_id(pmv_id);
            model.setNode_id_mapping(node_id_mapping);
            model.setEdge_id_mapping(edge_id_mapping);
            model.setCanonicalProcessType(cpt);

            mergeData.add(model);



        }

        /*invoke*/
        MergeProcesses_Result_DO changedMergedCanonicalModel = PropagateChanges.propagateChangesFromG2CG(mergeData, nodeMappingGandCGs, edgeMappingGandCGs);

        return changedMergedCanonicalModel;


    }



}
