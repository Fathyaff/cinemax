package com.example.demo.core.movie.usecase.create;

import com.example.demo.core.exception.InvalidRequestException;
import com.example.demo.core.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author fathyaff
 * @date 15/08/21 15.06
 */
@Setter
@Getter
@AllArgsConstructor
public class MovieCreationRequestModel {

    private String movieName;

    private String movieDescription;

    private int durations;

    private String topic;

    public void validate() {
        if (StringUtils.isNullOrEmpty(movieName)) {
            throw new InvalidRequestException("Movie Name cannot be null or empty");
        }

        if (StringUtils.isNullOrEmpty(movieDescription)) {
            throw new InvalidRequestException("Movie Description cannot be null or empty");
        }

        if (durations <= 0) {
            throw new InvalidRequestException("Durations is invalid");
        }

        if (StringUtils.isNullOrEmpty(topic)) {
            throw new InvalidRequestException("Topic cannot be null or empty");
        }
    }
}
