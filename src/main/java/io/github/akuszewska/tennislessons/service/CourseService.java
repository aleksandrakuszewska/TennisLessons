package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.dto.CourseDTO;

import java.util.List;

public interface CourseService {

    List<CourseDTO> getCoursesByCity(String city);
    CourseDTO createCourse(CourseDTO courseData);
    CourseDTO deleteCourse(Long id);
    CourseDTO updateCourse(CourseDTO courseData);
}
