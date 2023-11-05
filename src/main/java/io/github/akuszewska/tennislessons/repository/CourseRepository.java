package io.github.akuszewska.tennislessons.repository;

import io.github.akuszewska.tennislessons.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByCityAndActive(String city, Boolean ACTIVE);
}
