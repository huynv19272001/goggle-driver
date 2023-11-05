package com.fm.api;

import com.fm.base.repository.sql.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

@Configuration
/*com.mysql.cj.jdbc.Driver*/
//@EnableJdbcRepositories(basePackages = "com.fm.base.repository.sql")
@EnableJpaRepositories(basePackages = "com.fm.base.repository.sql", repositoryBaseClass = BaseRepositoryImpl.class, repositoryImplementationPostfix = "Impl")
public class SqlConfig extends AbstractJdbcConfiguration {
    @Value("${postgresql.uri}")
    String dbUrl;

    @Value("${postgresql.user}")
    String user;

    @Value("${postgresql.password}")
    String password;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}