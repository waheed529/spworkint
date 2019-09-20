package com.packtpub.springsecurity.configuration;

import java.sql.SQLException;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.packtpub.springsecurity.dataaccess", "com.packtpub.springsecurity.service" })
@EnableTransactionManagement
public class DataSourceConfig {
	private EmbeddedDatabase database = null;

	@Bean
	public DataSource dataSource() {
		final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		database = builder.setType(EmbeddedDatabaseType.H2).setName("dataSource").ignoreFailedDrops(true)
				.continueOnError(false).addScript("classpath:database/h2/calendar-schema.sql")
				.addScript("classpath:database/h2/calendar-data.sql").build();
		return database;
	}

	@PreDestroy()
	public void dataSourceDestroy() throws SQLException {
		if (database != null) {
			database.shutdown();
		}
	}

}
