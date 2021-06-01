package com.bank.cashcard.configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bank.cashcard.CashcardApplication;
import com.bank.cashcard.constant.Constants;
import com.bank.cashcard.util.AESAlgorithm;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * @author Akshay Parab
 * Database Configuration 
 *
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = {CashcardApplication.class})
public class JpaConfig {
	
	@Value("${dataSource.driverClassName}")
	private String driver;
	@Value("${dataSource.url}")
	private String url;
	@Value("${dataSource.username}")
	private String username;
	@Value("${dataSource.password}")
	private String password;
	
	@Value("${hibernate.dialect}")
	private String dialect;
	@Value("${hibernate.hbm2ddl.auto}")
	private String hbm2ddlAuto;
	@Value("${hibernate.show_sql}")
	private String showSql;
	@Value("${hibernate.format_sql}")
	private String formatSql;
	@Value("${hibernate.use_sql_comments}")
	private String useSqlComments;
		
	@Bean
	public DataSource dataSource() throws GeneralSecurityException, IOException {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driver);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(AESAlgorithm.dt(password));
		config.addDataSourceProperty(Constants.CACHE_PREP_STMTS,Constants.TRUE);
		config.addDataSourceProperty(Constants.PREPSTMT_CACHE_SIZE,Constants.PREPSTMT_CACHE_SIZE_NO);
		config.addDataSourceProperty(Constants.PREPSTMT_CACHE_SQL_LIMIT, Constants.PREPSTMT_CACHE_SQL_LIMIT_NO);
		config.addDataSourceProperty(Constants.USE_SERVER_PREPSTMT,Constants.TRUE);
		return new HikariDataSource(config);
	}

	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource);

		String entities = ClassUtils.getPackageName(CashcardApplication.class);
		String converters = ClassUtils.getPackageName(Jsr310JpaConverters.class);
		entityManagerFactoryBean.setPackagesToScan(new String[]{entities, converters});

		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		Properties jpaProperties = new Properties();
		jpaProperties.put(Constants.HIBERNATE_DIALECT, dialect);
		jpaProperties.put(Constants.HIBERNATE_HBM2DDL_AUTO, hbm2ddlAuto);
		jpaProperties.put(Constants.HIBERNATE_SHOW_SQL, showSql);
		jpaProperties.put(Constants.HIBERNATE_FORMAT_SQL, formatSql);
		jpaProperties.put(Constants.HIBERNATE_SQL_COMMENTS, useSqlComments);
		jpaProperties.put(Constants.HIBERNATE_LAZY_LOAD, "true");
		entityManagerFactoryBean.setJpaProperties(jpaProperties); 

		return entityManagerFactoryBean;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

}
