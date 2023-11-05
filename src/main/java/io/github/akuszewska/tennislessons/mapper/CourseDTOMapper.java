package io.github.akuszewska.tennislessons.mapper;

import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.dto.CourseDTO;
import org.springframework.stereotype.Component;

@Component
public class CourseDTOMapper {

    public CourseDTO map(Course from) {
        return CourseDTO.builder()
                .courseId(from.getId())
                .title(from.getTitle())
                .description(from.getDescription())
                .maxParticipants(from.getMaxParticipants())
                .city(from.getCity())
                .price(from.getPrice())
                .start(from.getStart())
                .stop(from.getStop())
                .instructorId(from.getInstructor().getId())
                .active(from.getActive())
                .build();
    }
}
