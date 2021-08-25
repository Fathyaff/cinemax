package com.example.demo.core.movie.usecase.detail;

/**
 * @author fathyaff
 * @date 15/08/21 00.36
 */
public interface MovieDetailInputBoundary {
    void detail(MovieDetailRequestModel requestModel, MovieDetailOutputBoundary presenter);
}
