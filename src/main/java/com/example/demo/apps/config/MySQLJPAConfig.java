package com.example.demo.apps.config;

import com.example.demo.persistence.movie.JPAMovieGateway;
import com.example.demo.persistence.movie.MovieGateway;
import com.example.demo.persistence.movie.MovieRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author fathyaff
 * @date 15/08/21 03.22
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.demo.persistence")
@EntityScan(basePackages = "com.example.demo")
public class MySQLJPAConfig {
    @Bean
    public MovieGateway movieGateway(MovieRepository movieRepository) {
        return new JPAMovieGateway(movieRepository);
    }

}
