package Analysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import util.Quartile;

public class CountMetrics {
	public static String[] cmd = { "/bin/sh", "-c", null };
	public static final String DRIVER = "org.postgresql.Driver";
	public static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/CoEvolve";
	private static Connection connection = null;
	public static Statement statement = null;
	private String tableName;

	public CountMetrics(String tableName) {
		this.tableName = tableName;
	}

	
	public void start() {
		List<Double> list = getData();

		Quartile q = Quartile.calculateQuartile(list);
		System.out.println("Commit");
		System.out.println("ORM");
		System.out.println("Lower Whisker:" + q.getLowerWhisker()
				+ " Lower Quartile:" + q.getLowerQuartile() + " Median:"
				+ q.getMedian() + " Upper Quartile:" + q.getUpperQuartile()
				+ " Upper Whisker:" + q.getUpperWhisker() + " Mean:"
				+ q.getMean() + " StandardDeviation:"
				+ q.getStandardDeviation());

		list = getData1();
		// System.out.println(list);
		q = Quartile.calculateQuartile(list);
		System.out.println("NONORM");
		System.out.println("Lower Whisker:" + q.getLowerWhisker()
				+ " Lower Quartile:" + q.getLowerQuartile() + " Median:"
				+ q.getMedian() + " Upper Quartile:" + q.getUpperQuartile()
				+ " Upper Whisker:" + q.getUpperWhisker() + " Mean:"
				+ q.getMean() + " StandardDeviation:"
				+ q.getStandardDeviation());

		List<Double> list1 = getDate();

		// System.out.println(list1);
		q = Quartile.calculateQuartile(list1);
		System.out.println("Date");
		System.out.println("ORM");
		System.out.println("Lower Whisker:" + q.getLowerWhisker()
				+ " Lower Quartile:" + q.getLowerQuartile() + " Median:"
				+ q.getMedian() + " Upper Quartile:" + q.getUpperQuartile()
				+ " Upper Whisker:" + q.getUpperWhisker() + " Mean:"
				+ q.getMean() + " StandardDeviation:"
				+ q.getStandardDeviation());

		list1 = getDate1();

		// System.out.println(list1);
		q = Quartile.calculateQuartile(list1);
		System.out.println("NONORM");
		System.out.println("Lower Whisker:" + q.getLowerWhisker()
				+ " Lower Quartile:" + q.getLowerQuartile() + " Median:"
				+ q.getMedian() + " Upper Quartile:" + q.getUpperQuartile()
				+ " Upper Whisker:" + q.getUpperWhisker() + " Mean:"
				+ q.getMean() + " StandardDeviation:"
				+ q.getStandardDeviation());
	}

	public List<Double> getData() {
		List<Double> list = null;
		try {
			Class.forName(DRIVER).newInstance();
			try {
				connection = DriverManager.getConnection(DATABASE_URL,
						"postgres", "admin");
				statement = connection.createStatement();

				String query = "SELECT \"commitId\", \"commitDate\", \"ORMChange\" FROM \""
						+ this.tableName + "\" order by  \"commitDate\"";

				ResultSet rs = statement.executeQuery(query);
				double count = 0;
				list = new ArrayList<Double>();
				while (rs.next()) {
					int c = rs.getInt(3);

					if (c == 1) {
						list.add(count);
						count = 0;
					} else {
						count++;
					}
				}
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;

	}

	public List<Double> getData1() {
		List<Double> list = null;
		try {
			Class.forName(DRIVER).newInstance();
			try {
				connection = DriverManager.getConnection(DATABASE_URL,
						"postgres", "admin");
				statement = connection.createStatement();

				String query = "SELECT \"commitId\", \"commitDate\", \"ORMChange\" FROM \""
						+ this.tableName + "\" order by  \"commitDate\"";

				ResultSet rs = statement.executeQuery(query);
				double count = 0;
				list = new ArrayList<Double>();
				while (rs.next()) {
					int c = rs.getInt(3);

					if (c == 0) {
						list.add(count);
						count = 0;
					} else {
						count++;
					}
				}
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;

	}

	public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return TimeUnit.MILLISECONDS.toDays(diffInMillies);
	}

	public List<Double> getDate() {
		List<Double> list = null;
		try {
			Class.forName(DRIVER).newInstance();
			try {
				connection = DriverManager.getConnection(DATABASE_URL,
						"postgres", "admin");
				statement = connection.createStatement();

				String query = "SELECT \"commitId\", \"commitDate\", \"ORMChange\" FROM \""
						+ this.tableName + "\" order by  \"commitDate\"";

				ResultSet rs = statement.executeQuery(query);
				Date oldDate = null;
				list = new ArrayList<Double>();
				while (rs.next()) {
					int c = rs.getInt(3);
					Date d = rs.getDate(2);
					if (c == 1) {
						if (oldDate != null) {
							double diff = getDateDiff(oldDate, d, TimeUnit.DAYS);
							list.add(diff);
						}
						oldDate = d;
					}
				}
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;

	}

	public List<Double> getDate1() {
		List<Double> list = null;
		try {
			Class.forName(DRIVER).newInstance();
			try {
				connection = DriverManager.getConnection(DATABASE_URL,
						"postgres", "admin");
				statement = connection.createStatement();

				String query = "SELECT \"commitId\", \"commitDate\", \"ORMChange\" FROM \""
						+ this.tableName + "\" order by  \"commitDate\"";

				ResultSet rs = statement.executeQuery(query);
				Date oldDate = null;
				list = new ArrayList<Double>();
				while (rs.next()) {
					int c = rs.getInt(3);
					Date d = rs.getDate(2);
					if (c == 0) {
						if (oldDate != null) {
							double diff = getDateDiff(oldDate, d, TimeUnit.DAYS);
							list.add(diff);
						}
						oldDate = d;
					}
				}
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;

	}
}
