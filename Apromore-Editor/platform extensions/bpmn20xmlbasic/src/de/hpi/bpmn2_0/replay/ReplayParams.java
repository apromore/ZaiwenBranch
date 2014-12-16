package de.hpi.bpmn2_0.replay;

public class ReplayParams {
    
    private double maxCost;
    private int maxDepth;
    private double minHits;
    private double maxHits;
    private int maxDiffSeries;
    private double ActivityMatchCost;
    private double ActivitySkippedCost;
    private double EventSkippedCost;
    private double NonActivityMoveCost;
    private double traceChunkSize; //maximum length of trace to be searched one at a time
    private int MaxNumberOfNodesVisited;
    private double MaxActivitySkipPercent;
    private int MaxNodeDistance;
    private int TimelineSlots;
    private int TotalEngineSeconds;
    private int ProgressCircleBarRadius;
    private int SequenceTokenDiffThreshold;
    private String BacktrackingDebug;
    
    public String getBacktrackingDebug() {
        return BacktrackingDebug;
    }

    public void setBacktrackingDebug(String BacktrackingDebug) {
        this.BacktrackingDebug = BacktrackingDebug;
    }
    
    public boolean isBacktrackingDebug() {
        return this.BacktrackingDebug.toLowerCase().equals("true");
    }


    public int getSequenceTokenDiffThreshold() {
        return SequenceTokenDiffThreshold;
    }

    public void setSequenceTokenDiffThreshold(int SequenceTokenDiffThreshold) {
        this.SequenceTokenDiffThreshold = SequenceTokenDiffThreshold;
    }


    public int getProgressCircleBarRadius() {
        return ProgressCircleBarRadius;
    }

    public void setProgressCircleBarRadius(int ProgressCircleBarRadius) {
        this.ProgressCircleBarRadius = ProgressCircleBarRadius;
    }


    public int getTotalEngineSeconds() {
        return TotalEngineSeconds;
    }

    public void setTotalEngineSeconds(int TotalEngineSeconds) {
        this.TotalEngineSeconds = TotalEngineSeconds;
    }


    public int getTimelineSlots() {
        return TimelineSlots;
    }

    public void setTimelineSlots(int TimelineSlots) {
        this.TimelineSlots = TimelineSlots;
    }


    public int getMaxNodeDistance() {
        return MaxNodeDistance;
    }

    public void setMaxNodeDistance(int MaxNodeDistance) {
        this.MaxNodeDistance = MaxNodeDistance;
    }


    public double getMaxActivitySkip() {
        return MaxActivitySkipPercent;
    }

    public void setMaxActivitySkip(double ActivitySkipPercent) {
        this.MaxActivitySkipPercent = ActivitySkipPercent;
    }


    public double getTraceChunkSize() {
        return traceChunkSize;
    }

    public void setTraceChunkSize(int traceChunkSize) {
        this.traceChunkSize = traceChunkSize;
    }
    
    public int getMaxNumberOfNodesVisited() {
        return MaxNumberOfNodesVisited;
    }

    public void setMaxNumberOfNodesVisited(int MaxNumberOfNodesVisited) {
        this.MaxNumberOfNodesVisited = MaxNumberOfNodesVisited;
    }    

    public double getActivityMatchCost() {
        return ActivityMatchCost;
    }

    public void setActivityMatchCost(double ActivityMatchCost) {
        this.ActivityMatchCost = ActivityMatchCost;
    }

    public double getEventSkipCost() {
        return EventSkippedCost;
    }

    public void setEventSkipCost(double EventSkippedCost) {
        this.EventSkippedCost = EventSkippedCost;
    }


    public double getActivitySkipCost() {
        return ActivitySkippedCost;
    }

    public void setActivitySkipCost(double ActivitySkippedCost) {
        this.ActivitySkippedCost = ActivitySkippedCost;
    }

    public double getNonActivityMoveCost() {
        return NonActivityMoveCost;
    }

    public void setNonActivityMoveCost(double NonActivityMoveCost) {
        this.NonActivityMoveCost = NonActivityMoveCost;
    }

    public int getMaxDiffSeries() {
        return maxDiffSeries;
    }

    public void setMaxDiffSeries(int maxDiffSeries) {
        this.maxDiffSeries = maxDiffSeries;
    }

    public double getMaxMatch() {
        return maxHits;
    }

    public void setMaxMatch(double maxHits) {
        this.maxHits = maxHits;
    }


    public double getMinMatch() {
        return minHits;
    }

    public void setMinMatch(double minHits) {
        this.minHits = minHits;
    }


    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

}