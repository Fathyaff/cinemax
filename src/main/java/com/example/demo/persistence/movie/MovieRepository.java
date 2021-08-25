package com.example.demo.persistence.movie;

import com.example.demo.core.movie.readmodel.MovieReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author fathyaff
 * @date 14/08/21 23.31
 */
public interface MovieRepository extends JpaRepository<MovieReadModel, String> {
}
