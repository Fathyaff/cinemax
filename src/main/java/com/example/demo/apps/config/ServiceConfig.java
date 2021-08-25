package com.example.demo.apps.config;

import advotics.shared.eventsourcing.wrapper.DefaultTransactionalWrapper;
import advotics.shared.eventsourcing.wrapper.TransactionalWrapper;
import com.example.demo.core.movie.usecase.detail.MovieDetailInputBoundary;
import com.example.demo.core.movie.usecase.detail.MovieDetailUseCase;
import com.example.demo.persistence.movie.MovieGateway;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author fathyaff
 * @date 15/08/21 02.02
 */
@SpringBootConfiguration
public class ServiceConfig {

    @Bean
    public MovieDetailInputBoundary detailMovieInputBoundary(MovieGateway movieGateway) {
        return new MovieDetailUseCase(movieGateway);
    }

    @Bean
    public TransactionalWrapper transactionalWrapper() {
        return new DefaultTransactionalWrapper();
    }
}
