/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.data;

import com.agentecon.goods.Good;
import com.agentecon.util.Average;
import com.agentecon.util.Numbers;

public class Edge {

	public String label;
	public double weight;
	public String source, destination;

	private transient Good good;
	private transient Average price;

	public Edge(Node source, Node destination, Good good) {
		this.source = source.label;
		this.destination = destination.label;
		this.good = good;
		this.weight = 0.0;
		this.price = new Average();
	}

	public void include(double amount, double payment) {
		this.price.add(amount, payment / amount);
	}

	public void finish() {
		this.label = (int) price.getTotWeight() + " " + good + " @ " + Numbers.toShortString(price.getAverage()) + "$";
		this.weight = calcWeight(price.getTotal());
	}

	/**
	 * Return a number between 1 and 10 ?
	 */
	private double calcWeight(double total) {
		return Math.max(1.0, Math.min(10, Math.log(total) - 3));
	}

	@Override
	public int hashCode() {
		return source.hashCode() ^ destination.hashCode() ^ good.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		Edge otherEdge = (Edge) other;
		return source.equals(otherEdge.source) && destination.equals(otherEdge.destination) && good.equals(otherEdge.good);
	}
}
