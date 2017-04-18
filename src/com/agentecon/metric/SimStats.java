package com.agentecon.metric;

import java.util.Collection;

import com.agentecon.api.ISimulation;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;

public abstract class SimStats extends SimulationListenerAdapter {

	public abstract Collection<? extends Chart> getCharts(String simId);

	public void notifySimStarting(ISimulation sim) {
		sim.addListener(this);
	}
	
	public void notifySimEnded(ISimulation sim) {
	}
	
	public abstract Collection<TimeSeries> getTimeSeries();
	
}