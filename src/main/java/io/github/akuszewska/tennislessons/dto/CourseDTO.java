package io.github.akuszewska.tennislessons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {

    private Long courseId;
    private String title;
    private String description;
    private Integer maxParticipants;
    private String city;
    private Double price;
    private Date start;
    private Date stop;
    private Boolean active;
    private Long instructorId;
    private Integer purchased;
}
