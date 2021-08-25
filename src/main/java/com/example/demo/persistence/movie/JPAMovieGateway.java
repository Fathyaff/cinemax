package com.example.demo.persistence.movie;

import com.example.demo.core.util.StringUtils;
import com.example.demo.core.movie.readmodel.MovieReadModel;
import com.example.demo.core.exception.InvalidRequestException;
import com.example.demo.core.exception.NoDataFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @author fathyaff
 * @date 15/08/21 00.13
 */
@RequiredArgsConstructor
public class JPAMovieGateway implements MovieGateway {
    private final MovieRepository movieRepository;

    @Override
    public void save(MovieReadModel movie) {
        if (Objects.isNull(movie)) {
            throw new InvalidRequestException("Movie cannot be null");
        }
        movieRepository.save(movie);
    }

    @Override
    public MovieReadModel findById(String movieId) {
        if (StringUtils.isNullOrEmpty(movieId)) {
            throw new InvalidRequestException("Movie ID cannot be null or empty");
        }
        return movieRepository.findById(movieId).orElseThrow(() -> new NoDataFoundException(
                String.format("No movie found for ID %s", movieId)));
    }

    @Override
    public List<MovieReadModel> findAll() {
        return movieRepository.findAll();
    }
}
