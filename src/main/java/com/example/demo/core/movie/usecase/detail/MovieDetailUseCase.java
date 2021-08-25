package com.example.demo.core.movie.usecase.detail;

import com.example.demo.core.movie.readmodel.MovieReadModel;
import com.example.demo.core.exception.InvalidRequestException;
import com.example.demo.persistence.movie.MovieGateway;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author fathyaff
 * @date 15/08/21 00.37
 */
@RequiredArgsConstructor
public class MovieDetailUseCase implements MovieDetailInputBoundary {

    private final MovieGateway movieGateway;

    @Override
    public void detail(MovieDetailRequestModel requestModel, MovieDetailOutputBoundary presenter) {
        validate(requestModel, presenter);
        MovieReadModel savedMovie = movieGateway.findById(requestModel.getMovieId());
        MovieDetailResponseModel responseModel = MovieDetailResponseModel.valueOf(savedMovie);
        presenter.present(responseModel);
    }

    private void validate(MovieDetailRequestModel requestModel, MovieDetailOutputBoundary presenter) {
        if (Objects.isNull(requestModel)) {
            throw new InvalidRequestException("Request cannot be null");
        }

        if (Objects.isNull(presenter)) {
            throw new InvalidRequestException("Presenter cannot be null");
        }

        requestModel.validate();
    }

}
