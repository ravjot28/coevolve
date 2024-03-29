package util;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class encapsulates all information about quartiles.
 * 
 * @author Ingo Mierswa
 */
public class Quartile {

	public static final int QUARTILE_WIDTH = 20;

	private double median;
	private double mean;
	private double standardDeviation;
	private double lowerQuartile;
	private double upperQuartile;
	private double lowerWhisker;
	private double upperWhisker;
	private double[] outliers;

	public Quartile(double median, double mean, double standardDeviation,
			double lowerQuartile, double upperQuartile, double lowerWhisker,
			double upperWhisker, double[] outliers) {
		this.median = median;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.lowerQuartile = lowerQuartile;
		this.upperQuartile = upperQuartile;
		this.lowerWhisker = lowerWhisker;
		this.upperWhisker = upperWhisker;
		this.outliers = outliers;
	}

	/** Returns the smallest value occupied by this quartile. */
	public double getMin() {
		double min = Math.min(lowerWhisker, mean - standardDeviation);
		for (int i = 0; i < outliers.length; i++)
			min = Math.min(min, outliers[i]);
		return min;
	}

	/** Returns the biggest value occupied by this quartile. */
	public double getMax() {
		double max = Math.max(upperWhisker, mean + standardDeviation);
		for (int i = 0; i < outliers.length; i++)
			max = Math.max(max, outliers[i]);
		return max;
	}

	public double getMedian() {
		return median;
	}

	public double getMean() {
		return mean;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public double getLowerQuartile() {
		return lowerQuartile;
	}

	public double getUpperQuartile() {
		return upperQuartile;
	}

	public double getLowerWhisker() {
		return lowerWhisker;
	}

	public double getUpperWhisker() {
		return upperWhisker;
	}

	public double[] getOutliers() {
		return outliers;
	}

	public static Quartile calculateQuartile(List<Double> table, int column) {
		double mean = 0.0d;
		double squaredSum = 0.0d;

		for (Double l : table) {

			double value = l;
			mean += value;
			squaredSum += value * value;
		}
		mean /= table.size();
		squaredSum /= table.size();
		double standardDeviation = Math.sqrt(squaredSum - (mean * mean));
		return calculateQuartile(mean, standardDeviation, table);
	}

	public static Quartile calculateQuartile(List<Double> values) {
		double mean = 0.0d;
		double squaredSum = 0.0d;
		Iterator<Double> i = values.iterator();
		while (i.hasNext()) {
			double value = i.next();
			mean += value;
			squaredSum += value * value;
		}
		mean /= values.size();
		squaredSum /= values.size();
		double standardDeviation = Math.sqrt(squaredSum - (mean * mean));
		return calculateQuartile(mean, standardDeviation, values);
	}

	private static Quartile calculateQuartile(double mean,
			double standardDeviation, List<Double> values) {
		Collections.sort(values);
		int medianIndex = (int) (values.size() * 0.5d);
		int lowerQuartileIndex = (int) (values.size() * 0.25d);
		int upperQuartileIndex = (int) (values.size() * 0.75d);
		int lowerWhiskerIndex = (int) (values.size() * 0.05d);
		int upperWhiskerIndex = (int) (values.size() * 0.95d);
		double median = values.get(medianIndex);
		double lowerQuartile = values.get(lowerQuartileIndex);
		double upperQuartile = values.get(upperQuartileIndex);
		double lowerWhisker = values.get(lowerWhiskerIndex);
		double upperWhisker = values.get(upperWhiskerIndex);

		double[] outliers = null;
		int numberOfOutliers = (lowerWhiskerIndex - 1)
				+ (values.size() - upperWhiskerIndex);
		if (numberOfOutliers >= 0) {
			outliers = new double[numberOfOutliers];
			int counter = 0;
			for (int i = 0; i < lowerWhiskerIndex; i++) {
				outliers[counter++] = values.get(i);
			}
			for (int i = upperWhiskerIndex + 1; i < values.size(); i++) {
				outliers[counter++] = values.get(i);
			}
		}
		return new Quartile(median, mean, standardDeviation, lowerQuartile,
				upperQuartile, lowerWhisker, upperWhisker, outliers);
	}

	@Override
	public String toString() {
		return "Quartile (median: " + median + ", lower q: " + lowerQuartile
				+ ", upper q: " + upperQuartile + "lower w: " + lowerWhisker
				+ ", upper w: " + upperWhisker + ", mean: " + mean + ", sd: "
				+ standardDeviation + ", number of outliers: "
				+ outliers.length + ")";
	}
}
