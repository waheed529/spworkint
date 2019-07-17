package com.intellect.fna;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
/*@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory", 
        basePackages = { "com.intellect.fna.repositories" })*/
public class JPAConfiguration {
	
	@Autowired
	Environment env;
    @Primary
    @Bean(name = "fnaDataSource")
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSource() {
    	 DriverManagerDataSource dataSource = new DriverManagerDataSource();
    	    dataSource.setDriverClassName(env.getProperty("datasource.driver-class-name"));
    	    dataSource.setUrl(env.getProperty("datasource.jdbcUrl"));
    	    dataSource.setUsername(env.getProperty("datasource.username"));
    	    dataSource.setPassword(env.getProperty("datasource.password"));

    	    return dataSource;
    }

    @Bean(name = "jndiDataSource")
	public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName(env.getProperty("datasource.jndi-name"));
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(false);
		bean.afterPropertiesSet();
		return (DataSource)bean.getObject();
	}

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("jndiDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.intellect.fna.model")
                .persistenceUnit("fna")
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
