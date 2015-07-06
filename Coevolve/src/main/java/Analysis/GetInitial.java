package Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GetInitial {
	private String url;
	private String[] cmd = { "/bin/sh", "-c", null };
	private final String DRIVER = "org.postgresql.Driver";
	private final String DATABASE_URL = "jdbc:postgresql://localhost:5432/CoEvolve";
	private Connection connection = null;
	private String tableName;
	private String folderName;

	public GetInitial(String url, String tableName, String folderName) {
		this.url = url;
		this.folderName = folderName;
		this.tableName = tableName;
	}

	private void init() {

		try {
			if (connection == null || connection.isClosed()) {
				Class.forName(DRIVER).newInstance();
				connection = DriverManager.getConnection(DATABASE_URL,
						"postgres", "admin");
			}
			try {

				String query = "CREATE TABLE \""
						+ this.tableName
						+ "\" (\"commitId\" character varying(4000) NOT NULL,\"commitDate\" date, \"ORMChange\" integer, CONSTRAINT \""
						+ this.tableName
						+ "_pkey\" PRIMARY KEY (\"commitId\")) WITH ( OIDS=FALSE);ALTER TABLE \""
						+ this.tableName + "\" OWNER TO postgres";

				PreparedStatement prep = connection.prepareStatement(query);

				prep.execute();

				prep.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void start() {
		init();
		if (!new File("/Users/Rav/Desktop/temp").exists()) {
			new File("/Users/Rav/Desktop/temp").mkdir();
		}
		String command = "cd /Users/Rav/Desktop/temp ;  git clone " + this.url
				+ " " + this.folderName;
		cmd[2] = command;
		executeCommand(false);
		command = "cd /Users/Rav/Desktop/temp/" + this.folderName
				+ " ; git rev-list HEAD --pretty=format:\"%cd\"";
		cmd[2] = command;
		executeCommand(true);
	}

	public List<String> executeCommand(boolean save) {
		try {

			ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line1 = r.readLine();
			List<String> list = new ArrayList<String>();
			while (line1 != null) {
				if (save) {
					String commitId = line1;
					line1 = r.readLine();
					String date = line1;
					insertCommitIdAndDate(commitId.replaceAll("commit ", "")
							.trim(), getDate(date));
				}
				line1 = r.readLine();
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Date getDate(String d) {
		Date date = null;
		SimpleDateFormat parserSDF = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
		try {
			date = parserSDF.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;

	}

	public void insertCommitIdAndDate(String commitId, Date date) {

		try {
			if (connection == null || connection.isClosed()) {
				Class.forName(DRIVER).newInstance();
				connection = DriverManager.getConnection(DATABASE_URL,
						"postgres", "admin");
			}
			try {

				String query = "INSERT INTO \"" + this.tableName
						+ "\"( \"commitId\", \"commitDate\") VALUES (?,?)";
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				java.sql.Date d = new java.sql.Date(cal.getTime().getTime());
				PreparedStatement prep = connection.prepareStatement(query);
				prep.setString(1, commitId);
				prep.setDate(2, d);
				prep.execute();

				prep.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
