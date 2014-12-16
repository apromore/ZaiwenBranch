package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class AnimationLog {
    private XLog xlog = null;
    private Map<XTrace, ReplayTrace> traceMap = new HashMap();
    private Set<XTrace> unplayTraces = new HashSet();
    private String name = "";
    private DateTime startDate = null;
    private DateTime endDate = null;
    private Interval interval = null;
    private String color = "";
    private long totalTime = 0; //in milliseconds
    private static final Logger LOGGER = Logger.getLogger(ReplayTrace.class.getCanonicalName());
    
    public AnimationLog(XLog xlog) {
        this.xlog = xlog;
    }
    
    public Map<XTrace, ReplayTrace> getTraceMap() {
        return this.traceMap;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public XLog getXLog() {
        return this.xlog;
    }
    
    public DateTime getStartDate() {
        if (startDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(2020, 1, 1);
            DateTime logStartDate = new DateTime(cal.getTime());
            
            for (ReplayTrace trace : this.getTraces()) {
                if (logStartDate.isAfter(trace.getStartDate())) {
                    logStartDate = trace.getStartDate();
                }
            }
            startDate = logStartDate;
        }
        return startDate;
    }
    
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }
    
    public DateTime getEndDate() {
        if (endDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(1920, 1, 1);
            DateTime logEndDate = new DateTime(cal.getTime());
            
            for (ReplayTrace trace : this.getTraces()) {
                if (logEndDate.isBefore(trace.getEndDate())) {
                    logEndDate = trace.getEndDate();
                }
            }
            endDate = logEndDate;
        }
        return endDate;
    }
    
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;        
    }    
    
    public Interval getInterval() {
        if (this.startDate != null && this.endDate != null) {
            return new Interval(this.startDate, this.endDate);
        } else {
            return null;
        }
    }    
    
    public void add(XTrace trace, ReplayTrace replayTrace) {
        traceMap.put(trace, replayTrace);
    }
    
    public void addUnreplayTrace(XTrace trace) {
        this.unplayTraces.add(trace);
    }
    
    public Set<XTrace> getUnplayTraces() {
        return this.unplayTraces;
    }
    
    public Collection<ReplayTrace> getTraces() {
        return this.traceMap.values();
    }
    
    public double getCostBasedMoveLogFitness() {
        double avgCost = 0;
        for (ReplayTrace trace : this.getTraces()) {
            avgCost += trace.getCostBasedMoveLogFitness();
        }
        if (this.getTraces().size() > 0) {
            return 1.0*avgCost/this.getTraces().size();
        }
        else {
            return 1.00;
        }
    }
    
    public double getCostBasedMoveModelFitness() {
        double avgCost = 0;
        for (ReplayTrace trace : this.getTraces()) {
            avgCost += trace.getCostBasedMoveModelFitness();
        }
        if (this.getTraces().size() > 0) {
            return 1.0*avgCost/this.getTraces().size();
        }
        else {
            return 1.00;
        }
    }   
    
    public double getTraceFitness(double minBoundMoveCostOnModel) {
        double avgCost = 0;
        for (ReplayTrace trace : this.getTraces()) {
            avgCost += trace.getTraceFitness(minBoundMoveCostOnModel);
        }
        if (this.getTraces().size() > 0) {
            return 1.0*avgCost/this.getTraces().size();
        }
        else {
            return 1.00;
        }        
    }  
    
    /**
     * Get the approximate trace fitness. This is approximate measure since
     * the calculation is based on approximate fitness value of every trace
     * The moves for every trace alignment are not optimal and the minimum
     * move on model cost is also approximated for the whole log
     * @return 
     */
    public double getApproxTraceFitness() {
        double minMMCost = this.getApproxMinMoveModelCost();
        double totalTraceFitness = 0;
        for (ReplayTrace trace : this.getTraces()) {
            totalTraceFitness += trace.getApproxTraceFitness(minMMCost);
        }        
        if (this.getTraces().size() > 0) {
            return 1.0*totalTraceFitness/this.getTraces().size();
        }
        else {
            return 1.00;
        }
    }
    
    /**
     * Get the minimum move model cost assuming that all events in every trace
     * does not match any activities on the model.
     * @return 
     */
    public double getApproxMinMoveModelCost() {
        double minUpperMMCost = Double.MAX_VALUE;
        for (ReplayTrace trace : this.getTraces()) {
            if (minUpperMMCost > trace.getUpperMoveCostOnModel()) {
                minUpperMMCost = trace.getUpperMoveCostOnModel();
            }
        }    
        return minUpperMMCost;
    }
    
    public boolean isEmpty() {
        return this.traceMap.isEmpty();
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return this.color;
    }
    
    public long getTotalTime() {
        return this.totalTime;
    }
    
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
    
    public long getAlgoRuntime() {
        long totalRuntime = 0;
        for (ReplayTrace trace : this.getTraces()) {
            totalRuntime += trace.getAlgoRuntime();
        }
        return totalRuntime;
    }
    
    /*
    * Return a map of intervals by sequenceIds for this log
    * Note that a trace might contain many overlapping intervals created from sequence flows    
    * key: sequence Id
    * value: list of intervals, each reprenseting a token transfer via the sequence flow (Id is the key)
    * The intervals of one sequenceId are sorted by start date, something look like below.
    * |--------------|
    *      |---------| 
    *      |---------|
    *        |------------------|
    *            |----------|
    *                  |-----------------------|
    */    
    public Map<String, SortedSet<Interval>> getIntervalMap() {
        Map<String, SortedSet<Interval>> sequenceByIds = new HashMap();
        
        SortedSet<Interval> transfers;
                
        for (ReplayTrace trace : traceMap.values()) {
            for (SequenceFlow seqFlow : trace.getSequenceFlows()) {
                if (!sequenceByIds.containsKey(seqFlow.getId())) {
                    transfers = new TreeSet<>(
                        new Comparator<Interval>() {
                            @Override
                            public int compare(Interval o1, Interval o2) {
                                if (o1.getStart().isBefore(o2.getStart())) {
                                    return -1;
                                } else {
                                    return +1;
                                }
                            }
                        });
                    sequenceByIds.put(seqFlow.getId(), transfers);
                }
                //LOGGER.info("Node1:" + seqFlow.getSourceRef().getName() + " id:" + seqFlow.getSourceRef().getId() + 
                //            "Node2:" + seqFlow.getTargetRef().getName() + " id:" + seqFlow.getTargetRef().getId());
                sequenceByIds.get(seqFlow.getId()).add(TimeUtilities.getInterval(seqFlow));
            }
        }
        return sequenceByIds;
    }

    public void clear() {
        this.xlog = null;
        for (ReplayTrace trace : this.traceMap.values()) {
            trace.clear();
        }
        traceMap.clear();
        
        for (XTrace xTrace : this.unplayTraces) {
            xTrace.clear();
        }
        unplayTraces.clear();
    }

}