package com.example.demo.core.movie.usecase.detail;

import com.example.demo.core.util.StringUtils;
import com.example.demo.core.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author fathyaff
 * @date 15/08/21 00.36
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailRequestModel {
    private String movieId;

    public static MovieDetailRequestModel valueOf(String movieId) {
        return MovieDetailRequestModel.builder().movieId(movieId).build();
    }

    public void validate() {
        if (StringUtils.isNullOrEmpty(movieId)) {
            throw new InvalidRequestException("Movie ID cannot be null or empty");
        }
    }
}
