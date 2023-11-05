package io.github.akuszewska.tennislessons.controller;

import io.github.akuszewska.tennislessons.dto.CourseDTO;
import io.github.akuszewska.tennislessons.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.github.akuszewska.tennislessons.security.dictionary.AuthenticationDictionary.INSTRUCTOR_PERMISSION;
import static io.github.akuszewska.tennislessons.security.dictionary.AuthenticationDictionary.STUDENT_PERMISSION;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseRestController {

    private final CourseService courseService;

    @GetMapping("/{city}")
    @PreAuthorize(STUDENT_PERMISSION)
    public ResponseEntity<List<CourseDTO>> getCoursesByCity(@PathVariable String city) {
        return ResponseEntity.ok(courseService.getCoursesByCity(city));
    }

    @PostMapping
    @PreAuthorize(INSTRUCTOR_PERMISSION)
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO course) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseService.createCourse(course));
    }

    @PutMapping
    @PreAuthorize(INSTRUCTOR_PERMISSION)
    public ResponseEntity<CourseDTO> updateCourse(@RequestBody CourseDTO course) {
        return ResponseEntity.ok(courseService.updateCourse(course));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(INSTRUCTOR_PERMISSION)
    public ResponseEntity<CourseDTO> deleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.deleteCourse(id));
    }
}
