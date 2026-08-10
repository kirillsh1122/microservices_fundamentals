package com.microservice.architecture.overview.e2e.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.microservice.architecture.overview.e2e.repository.resource",
        entityManagerFactoryRef = "resourceEntityManagerFactory",
        transactionManagerRef = "resourceTransactionManager"
)
public class PersistenceResourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.resource")
    public DataSourceProperties resourceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource resourceDataSource() {
        return resourceDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean resourceEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("resourceDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.microservice.architecture.overview.e2e.model.resource")
                .persistenceUnit("resource")
                .build();
    }

    @Bean
    PlatformTransactionManager resourceTransactionManager(
            @Qualifier("resourceEntityManagerFactory") EntityManagerFactory localEntityManagerFactory) {
        return new JpaTransactionManager(localEntityManagerFactory);
    }

}
