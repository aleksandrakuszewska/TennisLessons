package io.github.akuszewska.tennislessons;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableJpaRepositories({"io.github.akuszewska.tennislessons.repository"})
@ComponentScan(value = {"io.github.akuszewska.tennislessons"})
@AutoConfigureDataJpa
@EntityScan({"io.github.akuszewska.tennislessons.domain"})
public class TestConfiguration {
}
