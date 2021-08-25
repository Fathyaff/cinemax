package com.example.demo.core.movie.usecase.create;

/**
 * @author fathyaff
 * @date 15/08/21 15.06
 */
public interface MovieCreationInputBoundary {
    void create(MovieCreationRequestModel requestModel, MovieCreationOutputBoundary presenter);
}
