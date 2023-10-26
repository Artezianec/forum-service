package telran.java48.person.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import telran.java48.person.dto.CityPopulationDto;
import telran.java48.person.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public interface PersonRepository extends CrudRepository<Person, Integer> {
    @Query("select p from Person p where p.address.city=:city")
    Stream<Person> findByAddressCity(@Param("city") String city);

    @Query("select p from Person p where p.name =?1")
    Stream<Person> findByName(@Param("name") String name);


    Stream<Person> findByBirthDateBetween(LocalDate from, LocalDate to);
    @Query("select new telran.java48.person.dto.CityPopulationDto(p.address.city,count(p)) from Person p group by p.address.city order by count(p) asc")
    List<CityPopulationDto> getCitiesPopulation();

}
