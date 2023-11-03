package io.github.akuszewska.tennislessons.domain;

import io.github.akuszewska.tennislessons.domain.dictionary.RoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RoleEnum name;
}
