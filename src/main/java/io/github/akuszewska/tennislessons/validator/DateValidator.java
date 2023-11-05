package io.github.akuszewska.tennislessons.validator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DateValidator {

    public boolean validateDates(Date startDate, Date stopDate) {
        if (dateFromThePast(startDate) || dateFromThePast(stopDate)) {
            return false;
        }
        return startDate.after(stopDate);
    }

    private boolean dateFromThePast(Date date) {
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return date.before(currentDate);
    }
}
