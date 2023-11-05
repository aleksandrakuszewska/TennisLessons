package io.github.akuszewska.tennislessons.repository;

import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.dictionary.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleEnum roleName);
}
