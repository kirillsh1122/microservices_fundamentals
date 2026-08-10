package com.microservice.architecture.overview.e2e.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
@EnableJpaRepositories(
        basePackages = "com.microservice.architecture.overview.e2e.repository.song",
        entityManagerFactoryRef = "songEntityManagerFactory",
        transactionManagerRef = "songTransactionManager"
)
public class PersistenceSongConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.song")
    public DataSourceProperties songDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource songDataSource() {
        return songDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean songEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("songDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.microservice.architecture.overview.e2e.model.song")
                .persistenceUnit("song")
                .build();
    }

    @Bean
    PlatformTransactionManager songTransactionManager(
            @Qualifier("songEntityManagerFactory") EntityManagerFactory localEntityManagerFactory) {
        return new JpaTransactionManager(localEntityManagerFactory);
    }

}
