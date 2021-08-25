package com.example.demo.apps.movie.controller.detail;

import com.example.demo.apps.common.UriConstant;
import com.example.demo.apps.response.MovieDetailRESTResponse;
import com.example.demo.core.movie.usecase.detail.MovieDetailInputBoundary;
import com.example.demo.core.movie.usecase.detail.MovieDetailRequestModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fathyaff
 * @date 15/08/21 01.39
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = UriConstant.DEFAULT)
public class MovieDetailController {
    private final MovieDetailInputBoundary movieDetailInputBoundary;

    @GetMapping(path = UriConstant.DETAIL_MOVIE, produces = { "application/json" })
    public MovieDetailRESTResponse detail(MovieDetailRequestModel requestModel) {
        MovieRESTPresenterDetail presenter = new MovieRESTPresenterDetail();
        movieDetailInputBoundary.detail(requestModel, presenter);
        return presenter.getResponse();
    }
}
