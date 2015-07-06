package Analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Analyse {
	public static String[] cmd = { "/bin/sh", "-c", null };
	public static final String DRIVER = "org.postgresql.Driver";
	public static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/CoEvolve";
	private static Connection connection = null;
	public static Statement statement = null;

	private String tableName;
	private String folderName;
	private String language;

	public Analyse(String tableName, String folderName, String language) {
		this.folderName = folderName;
		this.tableName = tableName;
		this.language = language;
	}

	public void start() {
		List<String> list = getCommitIds();

		for (String commitId : list) {
			String command = "cd /Users/Rav/Desktop/temp/" + this.folderName
					+ " ; git log -p -2 " + commitId;
			cmd[2] = command;
			executeCommand(false, commitId);
		}

		update();
	}

	private void update() {
		try {
			if (connection == null || connection.isClosed()) {
				Class.forName(DRIVER).newInstance();
				connection = DriverManager.getConnection(DATABASE_URL,
						"postgres", "admin");
			}
			statement = connection.createStatement();

			String query = "update \"" + this.tableName
					+ "\" set \"ORMChange\" = 0 where \"commitId\" is null";

			statement.executeUpdate(query);

			statement.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void executeCommand(boolean save, String commitId) {
		BufferedReader r = null;
		try {

			ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line1 = r.readLine();
			while (line1 != null) {
				if ((line1.startsWith("+") || line1.startsWith("-"))
						&& !(line1.startsWith("++") || line1.startsWith("--"))) {
					if (isORM(this.language, line1)) {
						try {
							if (connection == null || connection.isClosed()) {
								Class.forName(DRIVER).newInstance();
								connection = DriverManager.getConnection(
										DATABASE_URL, "postgres", "admin");
							}
							statement = connection.createStatement();

							String query = "update \""
									+ this.tableName
									+ "\" set \"ORMChange\" = 1 where \"commitId\" = '"
									+ commitId + "'";

							statement.executeUpdate(query);

							statement.close();
							connection.close();

						} catch (Exception ex) {
							ex.printStackTrace();
						}
						break;
					}
				}
				line1 = r.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				r.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<String> getCommitIds() {
		List<String> list = null;
		try {
			Class.forName(DRIVER).newInstance();
			try {
				connection = DriverManager.getConnection(DATABASE_URL,
						"postgres", "admin");
				statement = connection.createStatement();

				String query = "select \"commitId\" from \"" + this.tableName
						+ "\"";

				ResultSet rs = statement.executeQuery(query);
				list = new ArrayList<String>();
				while (rs.next()) {
					String commitId = rs.getString(1);
					list.add(commitId);
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

	private boolean isORM(String language, String line1) {
		boolean result = false;
		switch (language) {
		case "java":
			result = (isDbConnectionJava(line1) > 0 || isLoggingJava(line1) > 0
					|| isPerformanceJava(line1) > 0 || isSchemaJava(line1) > 0);
			break;
		case "php":
			result = (isDbConnectionPhp(line1) > 0 || isLoggingPhp(line1) > 0
					|| isPerformancePhp(line1) > 0 || isSchemaPhp(line1) > 0);
			break;
		case "python":
			result = (isDbConnectionPython(line1) > 0
					|| isLoggingPython(line1) > 0
					|| isPerformancePython(line1) > 0 || isSchemaPython(line1) > 0);
			break;
		case "csharp":
			result = (isDbConnectionCsharp(line1) > 0
					|| isLoggingCsharp(line1) > 0
					|| isPerformanceCsharp(line1) > 0 || isSchemaCsharp(line1) > 0);
			break;
		}

		return result;
	}

	private int isDbConnectionPhp(String line) {

		int countOfDbConnection = 0;

		if (line.contains("EntityManager::create(")) {
			countOfDbConnection++;
		}

		if (line.contains("\\Doctrine\\ORM\\Mapping\\Driver\\DatabaseDriver")) {
			countOfDbConnection++;
		}

		return countOfDbConnection;

	}

	private int isSchemaPhp(String line) {
		int countOfSchema = 0;

		if (line.contains("@Entity")) {
			countOfSchema++;
		}
		if (line.contains("@Table")) {
			countOfSchema++;
		}
		if (line.contains("@Id")) {
			countOfSchema++;
		}
		if (line.contains("@Column")) {
			countOfSchema++;
		}
		if (line.contains("@GenerateValue")) {
			countOfSchema++;
		}
		if (line.contains("@SequenceGenerator")) {
			countOfSchema++;
		}
		if (line.contains("@OneToMany")) {
			countOfSchema++;
		}
		if (line.contains("@ManyToOne")) {
			countOfSchema++;
		}
		if (line.contains("@OneToOne")) {
			countOfSchema++;
		}
		if (line.contains("@JoinColumn")) {
			countOfSchema++;
		}
		if (line.contains("@ManyToMany")) {
			countOfSchema++;
		}
		if (line.contains("@AttributeOverrides")) {
			countOfSchema++;
		}
		if (line.contains("@Embeddable")) {
			countOfSchema++;
		}
		if (line.contains("@JoinTable(")) {
			countOfSchema++;
		}
		if (line.contains("@InheritanceType(")) {
			countOfSchema++;
		}
		if (line.contains("<entity")) {
			countOfSchema++;
		}
		if (line.contains("<many-to-one")) {
			countOfSchema++;
		}
		if (line.contains("<many-to-many")) {
			countOfSchema++;
		}
		if (line.contains("<one-to-one")) {
			countOfSchema++;
		}
		if (line.contains("<one-to-many")) {
			countOfSchema++;
		}
		if (line.contains("<embedded")) {
			countOfSchema++;
		}
		if (line.contains("<sequence-generator")) {
			countOfSchema++;
		}
		if (line.contains("<join-column")) {
			countOfSchema++;
		}
		if (line.contains("<join-table")) {
			countOfSchema++;
		}
		if (line.contains("oneToMany:")) {
			countOfSchema++;
		}
		if (line.contains("oneToOne:")) {
			countOfSchema++;
		}
		if (line.contains("manyToOne:")) {
			countOfSchema++;
		}
		if (line.contains("joinColumn:")) {
			countOfSchema++;
		}
		if (line.contains("joinTable:")) {
			countOfSchema++;
		}
		if (line.contains("inverseJoinColumns:")) {
			countOfSchema++;
		}
		if (line.contains("type: entity")) {
			countOfSchema++;
		}
		if (line.contains("inheritanceType:")) {
			countOfSchema++;
		}
		if (line.contains("@MappedSuperclass")) {
			countOfSchema++;
		}
		if (line.contains("type: mappedSuperclass")) {
			countOfSchema++;
		}
		if (line.contains("@UniqueConstraint")) {
			countOfSchema++;
		}
		if (line.contains("<mapped-superclass")) {
			countOfSchema++;
		}
		if (line.contains("@PostUpdate")) {
			countOfSchema++;
		}
		if (line.contains("@PrePersist")) {
			countOfSchema++;
		}
		if (line.contains("@PreRemove")) {
			countOfSchema++;
		}
		if (line.contains("@PreUpdate")) {
			countOfSchema++;
		}
		if (line.contains("@PostPersist")) {
			countOfSchema++;
		}
		if (line.contains("@PostRemove")) {
			countOfSchema++;
		}
		if (line.contains("@PostLoad")) {
			countOfSchema++;
		}
		return countOfSchema;
	}

	private int isPerformancePhp(String line) {
		int countOfPerformance = 0;

		if (line.contains("new Paginator(")) {
			countOfPerformance++;
		}
		if (line.contains("@Version")) {
			countOfPerformance++;
		}
		if (line.contains("setQueryCacheDriver")) {
			countOfPerformance++;
		}
		if (line.contains("setQueryCacheLifeTime")) {
			countOfPerformance++;
		}
		if (line.contains("expireQueryCache")) {
			countOfPerformance++;
		}
		if (line.contains("setMaxResults")) {
			countOfPerformance++;
		}
		if (line.contains("setFirstResult")) {
			countOfPerformance++;
		}
		if (line.contains("setHint")) {
			countOfPerformance++;
		}
		if (line.contains("setResultCacheDriver")) {
			countOfPerformance++;
		}
		if (line.contains("setResultCacheLifeTime")) {
			countOfPerformance++;
		}
		if (line.contains("expireResultCache")) {
			countOfPerformance++;
		}
		if (line.contains("setResultCacheId")) {
			countOfPerformance++;
		}
		if (line.contains("useResultCache")) {
			countOfPerformance++;
		}
		if (line.contains("setFetchMode")) {
			countOfPerformance++;
		}
		if (line.contains("<named-native-queries")) {
			countOfPerformance++;
		}
		if (line.contains("namedNativeQueries:")) {
			countOfPerformance++;
		}
		if (line.contains("@ChangeTrackingPolicy")) {
			countOfPerformance++;
		}
		if (line.contains("<indexes")) {
			countOfPerformance++;
		}
		if (line.contains("@Index")) {
			countOfPerformance++;
		}
		if (line.contains("@Cache")) {
			countOfPerformance++;
		}
		if (line.contains("@ChangeTrackingPolicy")) {
			countOfPerformance++;
		}
		if (line.contains("AbstractCache")) {
			countOfPerformance++;
		}
		if (line.contains("Doctrine\\Common\\Cache")) {
			countOfPerformance++;
		}
		if (line.contains("MemcacheCache")) {
			countOfPerformance++;
		}
		if (line.contains("ArrayCache")) {
			countOfPerformance++;
		}
		if (line.contains("RedisCache")) {
			countOfPerformance++;
		}
		if (line.contains("XcacheCache")) {
			countOfPerformance++;
		}
		if (line.contains("setMetadataCacheImpl")) {
			countOfPerformance++;
		}

		return countOfPerformance;
	}

	private int isLoggingPhp(String line) {
		int countOfLogging = 0;
		if (line.contains("getSQL()")) {
			countOfLogging++;
		}
		if (line.contains("getDQL()")) {
			countOfLogging++;
		}

		return countOfLogging;
	}

	private int isDbConnectionPython(String line) {

		int countOfDbConnection = 0;

		if (line.contains("create_engine(")) {
			countOfDbConnection++;
		}

		if (line.contains("Session(")) {
			countOfDbConnection++;
		}

		if (line.contains("prepare(")) {
			countOfDbConnection++;
		}

		if (line.contains("contextual_connect(")) {
			countOfDbConnection++;
		}

		if (line.contains("pool_size")) {
			countOfDbConnection++;
		}

		if (line.contains("poolclass")) {
			countOfDbConnection++;
		}

		if (line.contains("unique_connection(")) {
			countOfDbConnection++;
		}

		if (line.contains("sqlalchemy.pool.Pool")) {
			countOfDbConnection++;
		}

		if (line.contains("AssertionPool(")) {
			countOfDbConnection++;
		}

		return countOfDbConnection;

	}

	private int isSchemaPython(String line) {
		int countOfSchema = 0;

		if (line.contains("Table(")) {
			countOfSchema++;
		}
		if (line.contains("UniqueConstraint(")) {
			countOfSchema++;
		}
		if (line.contains("sqlalchemy.schema.")) {
			countOfSchema++;
		}
		if (line.contains("Column(")) {
			countOfSchema++;
		}
		if (line.contains("relationship(")) {
			countOfSchema++;
		}
		if (line.contains("mapper(")) {
			countOfSchema++;
		}
		if (line.contains("@event.listens_for(")) {
			countOfSchema++;
		}
		if (line.contains("sqlalchemy.orm.column_property(")) {
			countOfSchema++;
		}
		if (line.contains("column_property(")) {
			countOfSchema++;
		}
		if (line.contains("exclude_properties")) {
			countOfSchema++;
		}
		if (line.contains("deferred(")) {
			countOfSchema++;
		}
		if (line.contains("undefer(")) {
			countOfSchema++;
		}
		if (line.contains("orm.undefer_group(")) {
			countOfSchema++;
		}
		if (line.contains("defer(")) {
			countOfSchema++;
		}
		if (line.contains("orm.defaultload(")) {
			countOfSchema++;
		}
		if (line.contains("undefer_group(")) {
			countOfSchema++;
		}
		if (line.contains("orm.column_property(")) {
			countOfSchema++;
		}
		if (line.contains("ForeignKey(")) {
			countOfSchema++;
		}
		if (line.contains("@validates(")) {
			countOfSchema++;
		}
		if (line.contains("@property")) {
			countOfSchema++;
		}
		if (line.contains("@hybrid_property")) {
			countOfSchema++;
		}
		if (line.contains("synonym(")) {
			countOfSchema++;
		}
		if (line.contains("composite(")) {
			countOfSchema++;
		}
		if (line.contains("Bundle")) {
			countOfSchema++;
		}
		if (line.contains("object_mapper(")) {
			countOfSchema++;
		}
		if (line.contains("configure_mappers(")) {
			countOfSchema++;
		}
		if (line.contains("clear_mappers(")) {
			countOfSchema++;
		}
		if (line.contains("add_properties(")) {
			countOfSchema++;
		}
		if (line.contains("add_property(")) {
			countOfSchema++;
		}
		if (line.contains("ForeignKeyConstraint(")) {
			countOfSchema++;
		}
		if (line.contains("lazy=")) {
			countOfSchema++;
		}
		if (line.contains("join_depth")) {
			countOfSchema++;
		}
		if (line.contains("primaryjoin")) {
			countOfSchema++;
		}
		if (line.contains("back_populates")) {
			countOfSchema++;
		}
		if (line.contains("collection_class")) {
			countOfSchema++;
		}
		if (line.contains("@collection")) {
			countOfSchema++;
		}
		if (line.contains("UniqueConstraint(")) {
			countOfSchema++;
		}
		if (line.contains("@declared_attr")) {
			countOfSchema++;
		}
		if (line.contains("automap_base(")) {
			countOfSchema++;
		}
		return countOfSchema;
	}

	private int isPerformancePython(String line) {
		int countOfPerformance = 0;

		/*
		 * if (line.contains("index")) { countOfPerformance++; }
		 */
		if (line.contains("index=")) {
			countOfPerformance++;
		}
		if (line.contains("Index(")) {
			countOfPerformance++;
		}
		if (line.contains("autoflush")) {
			countOfPerformance++;
		}
		if (line.contains("with_hint(")) {
			countOfPerformance++;
		}
		if (line.contains("with_lockmode(")) {
			countOfPerformance++;
		}
		if (line.contains("FromCache(")) {
			countOfPerformance++;
		}

		return countOfPerformance;
	}

	private int isLoggingPython(String line) {
		int countOfLogging = 0;
		if (line.contains("logging.basicConfig()")) {
			countOfLogging++;
		}
		if (line.contains("getLogger('sqlalchemy.engine')")) {
			countOfLogging++;
		}
		return countOfLogging;
	}

	private int isDbConnectionJava(String line) {

		int countOfDbConnection = 0;

		if (line.contains("hibernate.connection.driver_class")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.datasource")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.url")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.username")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.password")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.dialect")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.c3p0.min_size")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.c3p0.max_size")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.c3p0.timeout")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.c3p0.max_statements")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.c3p0.idle_test_period")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.datasource")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.transaction.factory_class")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.transaction.manager_lookup_class")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.jndi.class")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.jndi.url")) {
			countOfDbConnection++;
		}
		if (line.contains("transaction.manager_lookup_class")) {
			countOfDbConnection++;
		}
		if (line.contains("transaction.factory_class")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.datasource")) {
			countOfDbConnection++;
		}
		if (line.contains("c3p0.idle_test_period")) {
			countOfDbConnection++;
		}
		if (line.contains("c3p0.max_statements")) {
			countOfDbConnection++;
		}
		if (line.contains("c3p0.timeout")) {
			countOfDbConnection++;
		}
		if (line.contains("c3p0.max_size")) {
			countOfDbConnection++;
		}
		if (line.contains("c3p0.min_size")) {
			countOfDbConnection++;
		}
		if (line.contains("dialect")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.password")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.username")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.url")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.driver_class")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.pool_size")) {
			countOfDbConnection++;
		}
		if (line.contains("TransactionManagerLookupStrategy")) {
			countOfDbConnection++;
		}
		if (line.contains("JndiName")) {
			countOfDbConnection++;
		}
		if (line.contains("Datasource")) {
			countOfDbConnection++;
		}
		if (line.contains("TransactionStrategy")) {
			countOfDbConnection++;
		}
		return countOfDbConnection;

	}

	private int isSchemaJava(String line) {
		int countOfSchema = 0;

		if (line.contains("<class")) {
			countOfSchema++;
		}
		if (line.contains("<many-to-one")) {
			countOfSchema++;
		}
		if (line.contains("<one-to-one")) {
			countOfSchema++;
		}
		if (line.contains("<one-to-many")) {
			countOfSchema++;
		}
		if (line.contains("<many-to-many")) {
			countOfSchema++;
		}
		if (line.contains("hibernate.default_schema")) {
			countOfSchema++;
		}
		if (line.contains("hibernate.default_catalog")) {
			countOfSchema++;
		}
		if (line.contains("MapResources")) {
			countOfSchema++;
		}
		if (line.contains("not-null")) {
			countOfSchema++;
		}
		if (line.contains("formula")) {
			countOfSchema++;
		}
		if (line.contains("NamingStrategy")) {
			countOfSchema++;
		}
		if (line.contains("default-schema")) {
			countOfSchema++;
		}
		if (line.contains("@hibernate.id")) {
			countOfSchema++;
		}
		if (line.contains("@hibernate.property")) {
			countOfSchema++;
		}
		if (line.contains("new Column(")) {
			countOfSchema++;
		}
		if (line.contains("new SimpleValue(")) {
			countOfSchema++;
		}
		if (line.contains("new Property(")) {
			countOfSchema++;
		}
		if (line.contains("<generator class")) {
			countOfSchema++;
		}
		if (line.contains("<discriminator")) {
			countOfSchema++;
		}
		if (line.contains("<subclass")) {
			countOfSchema++;
		}
		if (line.contains("<joined-subclass")) {
			countOfSchema++;
		}
		if (line.contains("cascade")) {
			countOfSchema++;
		}
		if (line.contains("inverse")) {
			countOfSchema++;
		}
		if (line.contains("LockMode.")) {
			countOfSchema++;
		}
		if (line.contains("UserType")) {
			countOfSchema++;
		}
		if (line.contains("CompositeUserType")) {
			countOfSchema++;
		}
		if (line.contains("<composite-element")) {
			countOfSchema++;
		}
		if (line.contains("<set")) {
			countOfSchema++;
		}
		if (line.contains("<idbag")) {
			countOfSchema++;
		}
		if (line.contains("<list")) {
			countOfSchema++;
		}
		if (line.contains("<map")) {
			countOfSchema++;
		}
		if (line.contains("<mapping package")) {
			countOfSchema++;
		}
		if (line.contains("<mapping class")) {
			countOfSchema++;
		}
		if (line.contains("@Table")) {
			countOfSchema++;
		}
		if (line.contains("@GeneratedValue")) {
			countOfSchema++;
		}
		if (line.contains("@GenericGenerator")) {
			countOfSchema++;
		}
		if (line.contains("@Temporal")) {
			countOfSchema++;
		}
		if (line.contains("@Column")) {
			countOfSchema++;
		}
		if (line.contains("@Lob")) {
			countOfSchema++;
		}
		if (line.contains("@Embedded")) {
			countOfSchema++;
		}
		if (line.contains("@Embeddable")) {
			countOfSchema++;
		}
		if (line.contains("@Access")) {
			countOfSchema++;
		}
		if (line.contains("@javax.persistence.Access")) {
			countOfSchema++;
		}
		if (line.contains("@Column")) {
			countOfSchema++;
		}
		if (line.contains("@javax.persistence.Column")) {
			countOfSchema++;
		}
		if (line.contains("@javax.persistence.TableGenerator")) {
			countOfSchema++;
		}
		if (line.contains("@TableGenerator")) {
			countOfSchema++;
		}
		if (line.contains("<table-generator")) {
			countOfSchema++;
		}
		if (line.contains("@javax.persistence.SequenceGenerator")) {
			countOfSchema++;
		}
		if (line.contains("<sequence-generator")) {
			countOfSchema++;
		}
		if (line.contains("@AttributeOverride")) {
			countOfSchema++;
		}
		if (line.contains("@JoinColumns")) {
			countOfSchema++;
		}
		if (line.contains("@OneToOne")) {
			countOfSchema++;
		}
		if (line.contains("@OneToMany")) {
			countOfSchema++;
		}
		if (line.contains("@ManyToOne")) {
			countOfSchema++;
		}
		if (line.contains("@ManyToMany")) {
			countOfSchema++;
		}
		if (line.contains("@Inheritance")) {
			countOfSchema++;
		}
		if (line.contains("@DiscriminatorColumn")) {
			countOfSchema++;
		}
		if (line.contains("@PrimaryKeyJoinColumn")) {
			countOfSchema++;
		}
		if (line.contains("@MappedSuperclass")) {
			countOfSchema++;
		}
		if (line.contains("@JoinTable")) {
			countOfSchema++;
		}
		if (line.contains("@CollectionTable")) {
			countOfSchema++;
		}
		if (line.contains("@SecondaryTables")) {
			countOfSchema++;
		}
		if (line.contains("@OnDelete")) {
			countOfSchema++;
		}
		if (line.contains("@ManyToAny")) {
			countOfSchema++;
		}
		if (line.contains("hibernate.default_entity_mode")) {
			countOfSchema++;
		}

		return countOfSchema;
	}

	private int isPerformanceJava(String line) {
		int countOfPerformance = 0;
		if (line.contains("dynamic-insert")) {
			countOfPerformance++;
		}
		if (line.contains("dynamic-update")) {
			countOfPerformance++;
		}
		if (line.contains("lazy=")) {
			countOfPerformance++;
		}
		if (line.contains("outer-join=")) {
			countOfPerformance++;
		}
		if (line.contains("batch-size=")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.jdbc.batch_size")) {
			countOfPerformance++;
		}
		if (line.contains("@BatchSize")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.max_fetch_depth")) {
			countOfPerformance++;
		}
		if (line.contains("max_fetch_depth")) {
			countOfPerformance++;
		}
		if (line.contains("optimistic-lock")) {
			countOfPerformance++;
		}
		if (line.contains("FlushMode.")) {
			countOfPerformance++;
		}
		if (line.contains("<version")) {
			countOfPerformance++;
		}
		if (line.contains("@Version")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.connection.isolation")) {
			countOfPerformance++;
		}
		if (line.contains("connection.isolation")) {
			countOfPerformance++;
		}
		if (line.contains("<cache")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.region_prefix")) {
			countOfPerformance++;
		}
		if (line.contains("cache.region_prefix")) {
			countOfPerformance++;
		}
		if (line.contains("cache.provider_class")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.provider_class")) {
			countOfPerformance++;
		}
		if (line.contains("<class-cache")) {
			countOfPerformance++;
		}
		if (line.contains("<collection-cache")) {
			countOfPerformance++;
		}
		if (line.contains("CacheMode")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.use_query_cache")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.default_cache_concurrency_strategy")) {
			countOfPerformance++;
		}
		if (line.contains("cache.default_cache_concurrency_strategy")) {
			countOfPerformance++;
		}
		if (line.contains("cache.use_query_cache")) {
			countOfPerformance++;
		}
		if (line.contains("@Cacheable")) {
			countOfPerformance++;
		}
		if (line.contains("@Cache")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.use_minimal_puts")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.use_second_level_cache")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.query_cache_factory")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.use_structured_entries")) {
			countOfPerformance++;
		}
		if (line.contains("<sql-query")) {
			countOfPerformance++;
		}
		if (line.contains("FetchType.")) {
			countOfPerformance++;
		}
		if (line.contains("@NamedQuery")) {
			countOfPerformance++;
		}
		if (line.contains("@SqlResultSetMapping")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.default_batch_fetch_size")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.order_updates")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.jdbc.fetch_size")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.jdbc.use_scrollable_resultset")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.connection.release_mode")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.transaction.manager_lookup_class")) {
			countOfPerformance++;
		}
		if (line.contains("jta.UserTransaction")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.transaction.factory_class")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.transaction.flush_before_completion")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.transaction.auto_close_session")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cglib.use_reflection_optimizer")) {
			countOfPerformance++;
		}

		return countOfPerformance;
	}

	private int isLoggingJava(String line) {
		int countOfLogging = 0;
		if (line.contains("hibernate. show_sql")) {
			countOfLogging++;
		}
		if (line.contains("show_sql")) {
			countOfLogging++;
		}
		if (line.contains("generate_statistics")) {
			countOfLogging++;
		}
		if (line.contains("hibernate.format_sql")) {
			countOfLogging++;
		}
		if (line.contains("hibernate.use_sql_comments")) {
			countOfLogging++;
		}
		return countOfLogging;
	}

	private int isDbConnectionCsharp(String line) {

		int countOfDbConnection = 0;

		if (line.contains("hibernate.connection.driver_class")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.dialect")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.provider")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.connection_string")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.isolation")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.default_schema")) {
			countOfDbConnection++;
		}
		if (line.contains("hibernate.connection.connection_string_name")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.provider")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.driver_class")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.connection_string")) {
			countOfDbConnection++;
		}
		if (line.contains("connection.connection_string_name")) {
			countOfDbConnection++;
		}
		if (line.contains("dialect")) {
			countOfDbConnection++;
		}
		return countOfDbConnection;

	}

	private int isSchemaCsharp(String line) {
		int countOfSchema = 0;
		if (line.contains("<generator class")) {
			countOfSchema++;
		}
		if (line.contains("<many-to-one")) {
			countOfSchema++;
		}
		if (line.contains("<one-to-one")) {
			countOfSchema++;
		}
		if (line.contains("<property")) {
			countOfSchema++;
		}
		if (line.contains("[Id(")) {
			countOfSchema++;
		}
		if (line.contains("[Generator(")) {
			countOfSchema++;
		}
		if (line.contains("[Class(")) {
			countOfSchema++;
		}
		if (line.contains("[Property(")) {
			countOfSchema++;
		}
		if (line.contains("[Component(")) {
			countOfSchema++;
		}
		if (line.contains("new Property(")) {
			countOfSchema++;
		}
		if (line.contains("new SimpleValue(")) {
			countOfSchema++;
		}
		if (line.contains("[ManyToOne(")) {
			countOfSchema++;
		}
		if (line.contains("[OneToMany(")) {
			countOfSchema++;
		}
		if (line.contains("[OneToOne(")) {
			countOfSchema++;
		}
		if (line.contains("<one-to-many")) {
			countOfSchema++;
		}
		if (line.contains("<discriminator")) {
			countOfSchema++;
		}
		if (line.contains("<subclass")) {
			countOfSchema++;
		}
		if (line.contains("[Discriminator(")) {
			countOfSchema++;
		}
		if (line.contains("<joined-subclass")) {
			countOfSchema++;
		}
		if (line.contains("[Subclass(")) {
			countOfSchema++;
		}
		if (line.contains("hibernate.use_proxy_validator")) {
			countOfSchema++;
		}
		if (line.contains("use_proxy_validator")) {
			countOfSchema++;
		}
		if (line.contains("NHibernate.Cfg.Environment.UseProxyValidator")) {
			countOfSchema++;
		}
		if (line.contains("NHibernate.UserTypes")) {
			countOfSchema++;
		}
		if (line.contains("IUserType")) {
			countOfSchema++;
		}
		if (line.contains("ICompositeUserType")) {
			countOfSchema++;
		}
		if (line.contains("<composite-element")) {
			countOfSchema++;
		}
		if (line.contains("<database-object")) {
			countOfSchema++;
		}
		if (line.contains("[CompositeId")) {
			countOfSchema++;
		}
		if (line.contains("[Serializable")) {
			countOfSchema++;
		}
		if (line.contains("hbm2ddl.auto")) {
			countOfSchema++;
		}
		if (line.contains("transaction.factory_class")) {
			countOfSchema++;
		}
		return countOfSchema;
	}

	private int isPerformanceCsharp(String line) {
		int countOfPerformance = 0;
		if (line.contains("hibernate.use_reflection_optimizer")) {
			countOfPerformance++;
		}
		if (line.contains("use_reflection_optimizer")) {
			countOfPerformance++;
		}
		if (line.contains("Environment.UseReflectionOptimizer")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.bytecode.provider")) {
			countOfPerformance++;
		}
		if (line.contains("bytecode.provider")) {
			countOfPerformance++;
		}
		if (line.contains("Environment.BytecodeProvider")) {
			countOfPerformance++;
		}
		if (line.contains("dynamic-insert")) {
			countOfPerformance++;
		}
		if (line.contains("dynamic-update")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.adonet.batch_size")) {
			countOfPerformance++;
		}
		if (line.contains("adonet.batch_size")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.max_fetch_depth")) {
			countOfPerformance++;
		}
		if (line.contains("max_fetch_depth")) {
			countOfPerformance++;
		}
		if (line.contains("NHibernate.ConnectionReleaseMode")) {
			countOfPerformance++;
		}
		if (line.contains("connection.release_mode")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.connection.release_mode")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.connection.isolation")) {
			countOfPerformance++;
		}
		if (line.contains("connection.isolation")) {
			countOfPerformance++;
		}
		if (line.contains("[Version(")) {
			countOfPerformance++;
		}
		if (line.contains("<version")) {
			countOfPerformance++;
		}
		if (line.contains("optimistic-lock")) {
			countOfPerformance++;
		}
		if (line.contains("[Cache(")) {
			countOfPerformance++;
		}
		if (line.contains("<cache")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.region_prefix")) {
			countOfPerformance++;
		}
		if (line.contains("cache.region_prefix")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.provider_class")) {
			countOfPerformance++;
		}
		if (line.contains("cache.provider_class")) {
			countOfPerformance++;
		}
		if (line.contains("<syscache")) {
			countOfPerformance++;
		}
		if (line.contains("<class-cache")) {
			countOfPerformance++;
		}
		if (line.contains("<collection-cache")) {
			countOfPerformance++;
		}
		if (line.contains(".SetFetchMode")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.use_query_cache")) {
			countOfPerformance++;
		}
		if (line.contains("cache.use_query_cache")) {
			countOfPerformance++;
		}
		if (line.contains(".SetCacheable")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.use_minimal_puts")) {
			countOfPerformance++;
		}
		if (line.contains("cache.use_minimal_puts")) {
			countOfPerformance++;
		}
		if (line.contains("hibernate.cache.query_cache_factory")) {
			countOfPerformance++;
		}
		if (line.contains("cache.query_cache_factory")) {
			countOfPerformance++;
		}

		return countOfPerformance;
	}

	private int isLoggingCsharp(String line) {
		int countOfLogging = 0;
		if (line.contains("hibernate.show_sql")) {
			countOfLogging++;
		}
		if (line.contains("show_sql")) {
			countOfLogging++;
		}
		if (line.contains("generate_statistics")) {
			countOfLogging++;
		}
		if (line.contains("Session.SessionFactory.Statistics")) {
			countOfLogging++;
		}
		return countOfLogging;
	}

}