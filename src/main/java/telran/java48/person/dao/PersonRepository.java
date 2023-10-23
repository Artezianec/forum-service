package telran.java48.person.dao;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import telran.java48.person.dto.CityPopulationDto;
import telran.java48.person.model.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface PersonRepository extends CrudRepository<Person, Integer> {

    List<Person> findByAddressCity(@Param("city") String city);

    List<Person> findByName(@Param("name") String name);

    Stream<Person> findByBirthDateBetween(LocalDate from, LocalDate to);

    // TODO: 23.10.2023
   // @Query()
   // List<CityPopulationDto> getCitiesPopulation();

}
