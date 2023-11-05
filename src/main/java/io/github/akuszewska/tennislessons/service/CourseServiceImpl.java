package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.dto.CourseDTO;
import io.github.akuszewska.tennislessons.mapper.CourseDTOMapper;
import io.github.akuszewska.tennislessons.repository.CourseRepository;
import io.github.akuszewska.tennislessons.validator.DateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.akuszewska.tennislessons.exception.ErrorMessage.COURSE_NOT_FOUND;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.DATES_ORDER_INVALID;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.PURCHASED_COURSE_DELETION;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseDTOMapper courseMapper;
    private final DateValidator dateValidator;
    private final AuthService authService;

    @Override
    public List<CourseDTO> getCoursesByCity(String city) {
        return courseRepository.findAllByCityAndActive(city, Boolean.TRUE)
                .stream()
                .map(courseMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseData) {

        Date startDate = courseData.getStart();
        Date stopDate = courseData.getStop();

        if (dateValidator.validateDates(startDate, stopDate)) {
            throw new RuntimeException(DATES_ORDER_INVALID);
        }

        Course newCourse = Course.builder()
                .title(courseData.getTitle())
                .description(courseData.getDescription())
                .maxParticipants(courseData.getMaxParticipants())
                .city(courseData.getCity())
                .price(courseData.getPrice())
                .start(startDate)
                .stop(stopDate)
                .purchased(0)
                .instructor(authService.getCurrentLoggedInUser())
                .active(Boolean.TRUE)
                .build();
        courseRepository.save(newCourse);
        return courseMapper.map(newCourse);
    }

    @Override
    public CourseDTO deleteCourse(Long id) {

        Course deleted = courseRepository.findById(id).orElseThrow(() -> new RuntimeException(COURSE_NOT_FOUND));

        if (deleted.getActive() && !deleted.getPurchased().equals(0)) {
            throw new RuntimeException(PURCHASED_COURSE_DELETION);
        }

        courseRepository.deleteById(id);
        return courseMapper.map(deleted);
    }

    @Override
    public CourseDTO updateCourse(CourseDTO courseData) {

        Course updated = courseRepository.findById(courseData.getCourseId())
                .orElseThrow(() -> new RuntimeException(COURSE_NOT_FOUND));

        Date startDate = courseData.getStart();
        Date stopDate = courseData.getStop();

        if (dateValidator.validateDates(startDate, stopDate)) {
            throw new RuntimeException(DATES_ORDER_INVALID);
        }

        updated.setTitle(courseData.getTitle());
        updated.setDescription(courseData.getDescription());
        updated.setMaxParticipants(courseData.getMaxParticipants());
        updated.setStart(courseData.getStart());
        updated.setStop(courseData.getStop());
        updated.setPrice(courseData.getPrice());
        updated.setActive(courseData.getActive());

        return courseMapper.map(updated);
    }
}
