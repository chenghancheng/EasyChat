package com.example.easychat.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.easychat.repository",
        entityManagerFactoryRef = "asteriskEntityManagerFactory",
        transactionManagerRef = "asteriskTransactionManager"
)
public class AsteriskDataSourceConfig {

    @Bean(name = "asteriskDataSource")
    @ConfigurationProperties(prefix = "spring.asterisk.datasource")
    public DataSource asteriskDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "asteriskEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean asteriskEntityManagerFactory(
            @Qualifier("asteriskDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.easychat.entity.po");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean(name = "asteriskTransactionManager")
    public JpaTransactionManager asteriskTransactionManager(
            @Qualifier("asteriskEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}