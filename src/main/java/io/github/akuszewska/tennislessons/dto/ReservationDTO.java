package io.github.akuszewska.tennislessons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {

    private Long reservationId;
    private String additionalRemarks;
    private Long clientId;
    private Long courseId;
    private Boolean accepted;
}
