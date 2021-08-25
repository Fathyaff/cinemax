package com.example.demo.persistence.movie;

import com.example.demo.core.movie.readmodel.MovieReadModel;

import java.util.List;

/**
 * @author fathyaff
 * @date 15/08/21 00.13
 */
public interface MovieGateway {
    void save(MovieReadModel movie);

    MovieReadModel findById(String movieId);

    List<MovieReadModel> findAll();
}
