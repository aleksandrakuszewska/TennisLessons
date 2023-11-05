package io.github.akuszewska.tennislessons.builder;

import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.dto.CourseDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class CourseBuilder {

    public static final String COURSE_TITLE = "Default course title";
    public static final String COURSE_DESCRIPTION = "Default course description";
    public static final String COURSE_CITY = "City";
    public static final Double COURSE_PRICE = 1500.0;
    public static final Date COURSE_START_DATE = new Date(2023, Calendar.OCTOBER, 1);
    public static final Date COURSE_STOP_DATE = new Date(2023, Calendar.OCTOBER, 15);
    public static final Integer COURSE_MAX_PARTICIPANTS = 15;
    public static final Integer COURSE_PURCHASED = 0;
    public static final Boolean COURSE_ACTIVE = Boolean.TRUE;
    public static final  String COURSE_UPDATED_TITLE = "Title updated";

    public static Course createDefaultCourse() {
        return Course.builder()
                .title(COURSE_TITLE)
                .description(COURSE_DESCRIPTION)
                .city(COURSE_CITY)
                .price(COURSE_PRICE)
                .start(COURSE_START_DATE)
                .stop(COURSE_STOP_DATE)
                .maxParticipants(COURSE_MAX_PARTICIPANTS)
                .purchased(COURSE_PURCHASED)
                .active(COURSE_ACTIVE)
                .build();
    }

    public static CourseDTO createDefaultCourseCreationBody() {

        Date startDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date stopDate = Date.from(LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault()).plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());

        return CourseDTO.builder()
                .title(COURSE_TITLE)
                .description(COURSE_DESCRIPTION)
                .city(COURSE_CITY)
                .start(startDate)
                .stop(stopDate)
                .maxParticipants(COURSE_MAX_PARTICIPANTS)
                .price(COURSE_PRICE)
                .build();
    }

    public static CourseDTO createCourseCreationBodyWithDatesWithInvalidDates() {

        CourseDTO courseWithInvalidDates = createDefaultCourseCreationBody();

        Date startDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date stopDate = Date.from(LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault()).plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());

        courseWithInvalidDates.setStart(stopDate);
        courseWithInvalidDates.setStop(startDate);

        return courseWithInvalidDates;
    }
}
