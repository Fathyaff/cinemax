package com.example.demo.apps.movie.controller.detail;

import com.example.demo.apps.response.MovieDetailRESTResponse;
import com.example.demo.core.movie.usecase.detail.MovieDetailOutputBoundary;
import com.example.demo.core.movie.usecase.detail.MovieDetailResponseModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fathyaff
 * @date 15/08/21 01.44
 */
@Getter
public class MovieRESTPresenterDetail implements MovieDetailOutputBoundary {
    private MovieDetailRESTResponse response;

    @Override
    public void present(MovieDetailResponseModel responseModel) {
        response = MovieDetailRESTResponse.valueOf(responseModel);
    }
}
