package com.example.demo.apps.config;

import com.example.demo.core.movie.usecase.detail.MovieDetailInputBoundary;
import com.example.demo.core.movie.usecase.detail.MovieDetailUseCase;
import com.example.demo.persistence.movie.JPAMovieGateway;
import com.example.demo.persistence.movie.MovieGateway;
import com.example.demo.persistence.movie.MovieRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@DataJpaTest
@EnableJpaRepositories(basePackages = "com.example.demo.persistence")
@EntityScan(basePackages = "com.example.demo")
public class H2JPAConfig {

    @Bean
    public MovieGateway movieGateway(MovieRepository movieRepository) {
        return new JPAMovieGateway(movieRepository);
    }

    @Bean
    public MovieDetailInputBoundary detailMovieInputBoundary(MovieGateway movieGateway) {
        return new MovieDetailUseCase(movieGateway);
    }

}
