package org.apromore.toolbox.similaritySearch.common;

import org.apromore.cpf.*;
import org.apromore.toolbox.similaritySearch.graph.*;
import org.apromore.toolbox.similaritySearch.graph.Vertex.GWType;
import org.apromore.toolbox.similaritySearch.graph.Vertex.Type;

import java.util.*;

/**
 *Partly Extended by Zaiwen FENG
 *
 * <a href="mailto:fzwbmw@gmail.com"></a>
 */

public class CPFModelParser {

    public static Graph readModel(CanonicalProcessType cpf) {
        Graph epcGraph = new Graph();
        epcGraph.name = cpf.getName();

        NetType mainNet = null;
        for (NetType n : cpf.getNet()) {
            if (n.getNode().size() > 0) {
                mainNet = n;
                break;
            }
        }

        if (mainNet == null) {
            return epcGraph;
        }

        addNodes(mainNet, epcGraph);
        addEdges(mainNet, epcGraph);
        epcGraph.linkVertices();

        // read references and objects
        addObjectsAndResources(cpf, epcGraph);

        return epcGraph;
    }

    /***the function is the same as the above one, the difference is the creation of the id of nodes and edges*/
    public static Graph readModel(CanonicalProcessType cpf, Map<String, String> node_mapping, Map<String, String> edge_mapping) {
        Graph epcGraph = new Graph();
        epcGraph.name = cpf.getName();

        NetType mainNet = null;
        for (NetType n : cpf.getNet()) {
            if (n.getNode().size() > 0) {
                mainNet = n;
                break;
            }
        }

        if (mainNet == null) {
            return epcGraph;
        }

        addNodes(mainNet, epcGraph, node_mapping);
        addEdges(mainNet, epcGraph, node_mapping, edge_mapping);
        epcGraph.linkVertices();

        // read references and objects
        addObjectsAndResources(cpf, epcGraph);

        return epcGraph;
    }

    public static List<Graph> readModels(CanonicalProcessType cpf) {
        LinkedList<Graph> l = new LinkedList<Graph>();

        for (NetType mainNet : cpf.getNet()) {
            if (mainNet.getNode().size() > 0) {
                Graph epcGraph = new Graph();

                addNodes(mainNet, epcGraph);
                addEdges(mainNet, epcGraph);
                epcGraph.linkVertices();

                // read references and objects
                addObjectsAndResources(cpf, epcGraph);
                l.add(epcGraph);
            }
        }
        return l;
    }

    private static void addObjectsAndResources(CanonicalProcessType cpf, Graph epcGraph) {
        for (NetType net : cpf.getNet()) {
            for (ObjectType o : net.getObject()) {
                epcGraph.addObject(
                        new VertexObject(o.getId(), o.getName(), o.isConfigurable(),
                                o instanceof HardType ? VertexObject.SoftHart.Hard : (o instanceof SoftType ? VertexObject.SoftHart.Soft : VertexObject.SoftHart.Other))
                );
            }
        }

        for (ResourceTypeType r : cpf.getResourceType()) {
            epcGraph.addResource(new VertexResource(r.getId(), r.getName(), r.isConfigurable(),
                    r instanceof HumanType ? VertexResource.Type.Human : (r instanceof NonhumanType ? VertexResource.Type.NonHuman : VertexResource.Type.Other)));
        }
    }

    private static void addNodes(NetType mainNet, Graph epcGraph) {
        for (NodeType n : mainNet.getNode()) {
            HashMap<String, String> annotationMap = parseAnnotationForEventsAndFunctions(getFromAnnotations("annotation", n.getAttribute()));
            // gateways
            if (n instanceof RoutingType && !(n instanceof StateType)) {
                boolean initialGW = true;
                String initial = getFromAnnotations("added", n.getAttribute());
                if (initial != null && "true".equals(initial)) {
                    initialGW = false;
                }
                Vertex v = new Vertex(
                        (n instanceof XORJoinType || n instanceof XORSplitType) ? "xor" :
                                ((n instanceof ANDJoinType || n instanceof ANDSplitType) ? "and" :
                                        "or"),
                        n.getId());

                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }
                // this is initial gateway
                // TODO if we don't have graph that is already merged, then
                // set all gateways to initials
                if (initialGW) {
                    v.setInitialGW();
                }
                if (n.isConfigurable() != null && n.isConfigurable()) {
                    v.setConfigurable(true);
                }

                epcGraph.addVertex(v);
            } else if (n instanceof TaskType) {
                Vertex v = new Vertex(Type.function, n.getName(), n.getId());/*pass id of nodes of net to nodes in graph*/
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }

                addResourcesAndObjects((WorkType) n, v);

                epcGraph.addVertex(v);
            } else if (n instanceof EventType) {
                Vertex v = new Vertex(Type.event, n.getName(), n.getId());
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }

                addResourcesAndObjects((WorkType) n, v);

                epcGraph.addVertex(v);
            } else if (n instanceof StateType) {
                Vertex v = new Vertex(Type.state, n.getName(), n.getId());
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }
                epcGraph.addVertex(v);
            }
        }
    }

    //**the function is the same as the above one, the difference is the creation of updated identifier of nodes***//
    private static void addNodes(NetType mainNet, Graph epcGraph, Map<String, String> node_mapping) {
        for (NodeType n : mainNet.getNode()) {
            HashMap<String, String> annotationMap = parseAnnotationForEventsAndFunctions(getFromAnnotations("annotation", n.getAttribute()));
            // gateways
            if (n instanceof RoutingType && !(n instanceof StateType)) {
                boolean initialGW = true;
                String initial = getFromAnnotations("added", n.getAttribute());
                if (initial != null && "true".equals(initial)) {
                    initialGW = false;
                }
                Vertex v = new Vertex(
                        (n instanceof XORJoinType || n instanceof XORSplitType) ? "xor" :
                                ((n instanceof ANDJoinType || n instanceof ANDSplitType) ? "and" :
                                        "or"),
                        node_mapping.get(n.getId()).toString());

                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }
                // this is initial gateway
                // TODO if we don't have graph that is already merged, then
                // set all gateways to initials
                if (initialGW) {
                    v.setInitialGW();
                }
                if (n.isConfigurable() != null && n.isConfigurable()) {
                    v.setConfigurable(true);
                }

                epcGraph.addVertex(v);
            } else if (n instanceof TaskType) {
                Vertex v = new Vertex(Type.function, n.getName(), node_mapping.get(n.getId()).toString());/*pass id of nodes of net combined with pmv to the nodes in graph*/
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }

                addResourcesAndObjects((WorkType) n, v);

                epcGraph.addVertex(v);
            } else if (n instanceof EventType) {
                Vertex v = new Vertex(Type.event, n.getName(), node_mapping.get(n.getId()).toString());
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }

                addResourcesAndObjects((WorkType) n, v);

                epcGraph.addVertex(v);
            } else if (n instanceof StateType) {
                Vertex v = new Vertex(Type.state, n.getName(), node_mapping.get(n.getId()).toString());
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }
                epcGraph.addVertex(v);
            }
        }
    }

    private static void addResourcesAndObjects(WorkType n, Vertex v) {
        // add resources
        for (ResourceTypeRefType r : n.getResourceTypeRef()) {
            v.resourceRefs.add(new VertexResourceRef(
                    r.getResourceTypeId(),
                    r.getQualifier(),
                    parseModelsFromAnnotations(getFromAnnotations("annotation", r.getAttribute()))));
        }

        // add objects
        for (ObjectRefType o : n.getObjectRef()) {
            v.objectRefs.add(new VertexObjectRef(
                    o.isOptional(),
                    o.getObjectId(),
                    o.isConsumed(),
                    (o.getType().equals(InputOutputType.INPUT) ?
                            VertexObjectRef.InputOutput.Input :
                            VertexObjectRef.InputOutput.Output),
                    parseModelsFromAnnotations(getFromAnnotations("annotation", o.getAttribute()))));
        }
    }

    private static String getFromAnnotations(String typeRef, List<TypeAttribute> attributes) {
        for (TypeAttribute a : attributes) {
            if (a.getName().equals(typeRef)) {
                return a.getValue();
            }
        }
        return "";
    }

    // the annotation attribute for events and functions are in format
    // model1:name in model1;model2:name in modelass 2;
    // the names must not contain ';', otherwise the name parsing fails
    private static HashMap<String, String> parseAnnotationForEventsAndFunctions(String nodeValue) {
        HashMap<String, String> annotationMap = new HashMap<String, String>();
        if (nodeValue == null) {
            return annotationMap;
        }

        StringTokenizer st = new StringTokenizer(nodeValue, ";");
        while (st.hasMoreTokens()) {
            String nextToken = st.nextToken();
            int colon = nextToken.indexOf(':');
            String modelName = nextToken.substring(0, colon);
            String value = nextToken.substring(colon + 1, nextToken.length());
            annotationMap.put(modelName, value);
        }

        return annotationMap;
    }

    private static void addEdges(NetType mainNet, Graph epcGraph) {

        List<EdgeType> edges = mainNet.getEdge();
        HashSet<String> graphLabels = new HashSet<String>();

        // add elements
        for (EdgeType e : edges) {
            Edge toAdd = new Edge(e.getSourceId(), e.getTargetId(), e.getId());
            String annotations = getFromAnnotations("name", e.getAttribute());

            graphLabels = parseModelsFromAnnotations(annotations);
            toAdd.addLabels(graphLabels);
            epcGraph.addEdge(toAdd);
        }

        if (graphLabels.size() > 0) {
            epcGraph.setGraphConfigurable();
            epcGraph.addGraphLabels(graphLabels);
        }
    }


    /**the function is the same as the above one, the difference is the creation of the edge id**/
    private static void addEdges(NetType mainNet, Graph epcGraph, Map<String, String> node_mapping, Map<String, String> edge_mapping) {

        List<EdgeType> edges = mainNet.getEdge();
        HashSet<String> graphLabels = new HashSet<String>();

        // add elements
        for (EdgeType e : edges) {
            Edge toAdd = new Edge(node_mapping.get(e.getSourceId()).toString(), node_mapping.get(e.getTargetId()).toString(), edge_mapping.get(e.getId()).toString());
            String annotations = getFromAnnotations("name", e.getAttribute());

            graphLabels = parseModelsFromAnnotations(annotations);
            toAdd.addLabels(graphLabels);
            epcGraph.addEdge(toAdd);
        }

        if (graphLabels.size() > 0) {
            epcGraph.setGraphConfigurable();
            epcGraph.addGraphLabels(graphLabels);
        }
    }

    private static HashSet<String> parseModelsFromAnnotations(String annotations) {
        HashSet<String> graphLabels = new HashSet<String>();
        if (annotations != null && annotations.length() > 0) {
            StringTokenizer st = new StringTokenizer(annotations, ";");
            while (st.hasMoreTokens()) {
                String l = st.nextToken();
                graphLabels.add(l);
            }
        }
        return graphLabels;
    }

    public static CanonicalProcessType writeModel(Graph g, IdGeneratorHelper idGenerator) {
        CanonicalProcessType toReturn = new CanonicalProcessType();

        // objects and resources
        for (VertexObject o : g.getObjects().values()) {
            ObjectType ot = new ObjectType();
            if (o.getSofthard().equals(VertexObject.SoftHart.Soft)) {
                ot = new SoftType();
            } else if (o.getSofthard().equals(VertexObject.SoftHart.Hard)) {
                ot = new HardType();
            }

            ot.setConfigurable(o.isConfigurable());
            ot.setId(o.getId());
            ot.setName(o.getName());
            TypeAttribute a = new TypeAttribute();
            a.setName("annotation");
            a.setValue(parseAnnotationFromSet(o.getModels()));
            ot.getAttribute().add(a);
            toReturn.getNet().get(0).getObject().add(ot);
        }

        // objects and resources
        for (VertexResource r : g.getResources().values()) {
            ResourceTypeType rt = new ResourceTypeType();
            if (r.getType().equals(VertexResource.Type.Human)) {
                rt = new HumanType();
            } else if (r.getType().equals(VertexResource.Type.NonHuman)) {
                rt = new NonhumanType();
            }

            rt.setConfigurable(r.isConfigurable());
            rt.setId(r.getId());
            rt.setName(r.getName());
            TypeAttribute a = new TypeAttribute();
            a.setName("annotation");
            a.setValue(parseAnnotationFromSet(r.getModels()));
            rt.getAttribute().add(a);
            toReturn.getResourceType().add(rt);
        }

        NetType net = new NetType();
        net.setId(idGenerator.getNextId());
        toReturn.getNet().add(net);

        for (Vertex v : g.getVertices()) {
            NodeType n = new NodeType();
            if (v.getType().equals(Vertex.Type.state)) {
                n = new StateType();
            } else if (v.getType().equals(Vertex.Type.event)) {
                n = new EventType();
            } else if (v.getType().equals(Vertex.Type.function)) {
                n = new TaskType();
            } else if (v.getType().equals(Vertex.Type.gateway)) {
                if (v.getParents().size() > 1) {
                    if (v.getGWType().equals(GWType.and)) {
                        n = new ANDJoinType();
                    } else if (v.getGWType().equals(GWType.or)) {
                        n = new ORJoinType();
                    } else if (v.getGWType().equals(GWType.xor)) {
                        n = new XORJoinType();
                    }
                } else if (v.getChildren().size() > 1) {
                    if (v.getGWType().equals(GWType.and)) {
                        n = new ANDSplitType();
                    } else if (v.getGWType().equals(GWType.or)) {
                        n = new ORSplitType();
                    } else if (v.getGWType().equals(GWType.xor)) {
                        n = new XORSplitType();
                    }
                }
                if (v.isAddedGW()) {
                    TypeAttribute a = new TypeAttribute();
                    a.setName("added");
                    a.setValue("true");
                    n.getAttribute().add(a);
                }
            }

            n.setConfigurable(v.isConfigurable());
            n.setId(v.getID());
            n.setName(v.getLabel());
            TypeAttribute a = new TypeAttribute();
            a.setName("annotation");
            a.setValue(parseAnnotationFromMap(v.getAnnotationMap()));
            n.getAttribute().add(a);

            if (v.getType().equals(Vertex.Type.event) || v.getType().equals(Vertex.Type.function)) {
                // object ref
                for (VertexObjectRef o : v.objectRefs) {
                    ObjectRefType oRef = new ObjectRefType();
                    if (o.getInputOutput().equals(VertexObjectRef.InputOutput.Input)) {
                        oRef.setType(InputOutputType.INPUT);
                    } else if (o.getInputOutput().equals(VertexObjectRef.InputOutput.Output)) {
                        oRef.setType(InputOutputType.OUTPUT);
                    }
                    oRef.setConsumed(o.getConsumed());
                    oRef.setObjectId(o.getObjectID());
                    oRef.setOptional(o.isOptional());
                    // attributes
                    TypeAttribute a1 = new TypeAttribute();
                    a1.setName("annotation");
                    a1.setValue(parseAnnotationFromSet(o.getModels()));
                    oRef.getAttribute().add(a1);

                    ((WorkType) n).getObjectRef().add(oRef);
                }
                // resource ref
                for (VertexResourceRef r : v.resourceRefs) {
                    ResourceTypeRefType rRef = new ResourceTypeRefType();
                    rRef.setResourceTypeId(r.getResourceID());
                    rRef.setQualifier(r.getQualifier());
                    // attrubutes
                    TypeAttribute a1 = new TypeAttribute();
                    a1.setName("annotation");
                    a1.setValue(parseAnnotationFromSet(r.getModels()));
                    rRef.getAttribute().add(a1);

                    ((WorkType) n).getResourceTypeRef().add(rRef);
                }
            }
            net.getNode().add(n);
        }

        for (Edge e : g.getEdges()) {
            EdgeType et = new EdgeType();
            et.setId(e.getId());
            // from and to vertex
            et.setSourceId(e.getFromVertex());
            et.setTargetId(e.getToVertex());
            // attributes
            TypeAttribute a = new TypeAttribute();
            a.setName("name");
            a.setValue(parseAnnotationFromSet(e.getLabels()));
            et.getAttribute().add(a);
            net.getEdge().add(et);
        }

        return toReturn;
    }

    private static String parseAnnotationFromMap(
            HashMap<String, String> annotationMap) {
        String toReturn = "";
        for (Map.Entry<String, String> a : annotationMap.entrySet()) {
            toReturn += a.getKey() + ":" + a.getValue() + ";";
        }
        return toReturn;
    }

    private static String parseAnnotationFromSet(HashSet<String> annotationSet) {
        String toReturn = "";
        for (String a : annotationSet) {
            toReturn += a + ",";
        }
        //if the last annotation of 'toReturn' is a comma, then delete this comma.
        if(toReturn.length() > 0 && toReturn.charAt(toReturn.length()-1)==','){

            toReturn = toReturn.substring(0, toReturn.length()-1);
        }
        return toReturn;
    }
}
