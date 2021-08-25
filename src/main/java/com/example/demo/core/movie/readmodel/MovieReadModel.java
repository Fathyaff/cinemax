package com.example.demo.core.movie.readmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fathyaff
 * @date 14/08/21 23.34
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class MovieReadModel {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "movie_name")
    private String movieName;

    @Column(name = "movie_description")
    private String movieDescription;

    @Column(name = "durations")
    private int durations;

}
