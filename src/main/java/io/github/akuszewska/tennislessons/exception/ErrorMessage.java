package io.github.akuszewska.tennislessons.exception;

public interface ErrorMessage {

    String COURSE_NOT_FOUND = "Course with given ID does not exists";
    String RESERVATION_NOT_FOUND = "Reservation with given ID does not exists";
    String RESERVATION_ACCEPTED = "You can't delete reservation that was accepted";
    String COURSE_SOLD_OUT = "Course sold out!";
    String DATES_ORDER_INVALID = "Dates order is invalid";
    String PURCHASED_COURSE_DELETION = "It is impossible to delete course that was purchased by anybody";
    String USER_NOT_FOUND = "User with given ID does not exists";
}
