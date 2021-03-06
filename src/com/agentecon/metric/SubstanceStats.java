package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.agentecon.agent.IAgents;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Position;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;

public class SubstanceStats extends SimStats {

	private TimeSeries innerValue;
	private TimeSeries outerValue;
	private TimeSeries valueRatio;
	private TimeSeries realOuterValue;

	private MarketStats market;
	private StockMarketStats stocks;

	public SubstanceStats(IAgents agents, StockMarketStats stocks, MarketStats market) {
		super(agents);
		this.stocks = stocks;
		this.market = market;
		this.innerValue = new TimeSeries("Inner Value");
		this.outerValue = new TimeSeries("Outer Value");
		this.valueRatio = new TimeSeries("Ratio");
		this.realOuterValue = new TimeSeries("Real Outer Value");
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		try {
			Collection<? extends IShareholder> holders = agents.getShareHolders();
			double outerValue = 0.0;
			double innerValue = 0.0;
			for (IShareholder holder : holders) {
				if (holder instanceof IFirm) {
					// innerValue += holder.getMoney().getAmount();
				}
				for (Position pos : holder.getPortfolio().getPositions()) {
					double value = pos.getAmount() * stocks.getPrice(pos.getTicker());
					IFirm heldCompany = agents.getFirm(pos.getTicker());
					if (holder instanceof IFirm) {
						// only count real companies they hold
						if (heldCompany instanceof IFirm) {
							innerValue += value;
						}
					} else {
						assert holder instanceof IConsumer;
						// Count all financial companies for outer value
						if (heldCompany instanceof IShareholder) {
							outerValue += value;
						}
					}
				}
			}
			if (!Double.isNaN(innerValue) && !Double.isNaN(outerValue) && innerValue != 0.0) {
				this.innerValue.set(day, innerValue);
				this.outerValue.set(day, outerValue);
				this.valueRatio.set(day, outerValue / innerValue);
				double realOuterValue = outerValue / market.getIndex();
				if (!Double.isNaN(realOuterValue)){
				this.realOuterValue.set(day, realOuterValue);
				}
//				System.out.println(day + "\t" + innerValue + "\t" + outerValue + "\t" + outerValue / innerValue + "\t" + realOuterValue);
			}
		} catch (AbstractMethodError e) {
		}
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		return Arrays.asList(new Chart(simId, "Financial Sector Value", "Outer: Combined market cap of all financial firms. Inner: non-financial shares they own.", innerValue, outerValue),
				new Chart(simId, "Financial Sector Real Value ", "Real outer value (i.e. outer value divided by consumer price index)", realOuterValue),
				new Chart(simId, "Financial Sector Value Ratio", "Outer: Combined market cap of all financial firms. Inner: non-financial shares they own.", valueRatio));
	}
	
	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.add(innerValue);
		list.add(outerValue);
		list.add(realOuterValue);
		return list;
	}

}
