package telran.java48.person.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.java48.person.dao.PersonRepository;
import telran.java48.person.dto.AddressDto;
import telran.java48.person.dto.CityPopulationDto;
import telran.java48.person.dto.PersonDto;
import telran.java48.person.dto.exceptions.PersonNotFoundException;
import telran.java48.person.model.Address;
import telran.java48.person.model.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    final PersonRepository personRepository;
    final ModelMapper modelMapper;

    @Override
    @Transactional
    public Boolean addPerson(PersonDto personDto) {
        if (personRepository.existsById(personDto.getId())) {
            return false;
        }
        personRepository.save(modelMapper.map(personDto, Person.class));
        return true;
    }

    @Override
    public PersonDto findPersonById(Integer id) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    @Transactional
    public PersonDto removePerson(Integer id) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        personRepository.delete(person);
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    @Transactional
    public PersonDto updatePersonName(Integer id, String name) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        person.setName(name);
//		personRepository.save(person);
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    @Transactional
    public PersonDto updatePersonAddress(Integer id, AddressDto addressDto) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        person.setAddress(modelMapper.map(addressDto, Address.class));
//		personRepository.save(person);
        return modelMapper.map(person, PersonDto.class);
    }

    @Override
    public Iterable<PersonDto> findPersonsByCity(String city) {
        List<Person> persons = personRepository.findByAddressCity(city);
        return persons.stream()
                .map(person -> modelMapper.map(person, PersonDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<PersonDto> findPersonsByName(String name) {
        List<Person> persons = personRepository.findByName(name);
        return persons.stream()
                .map(person -> modelMapper.map(person, PersonDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Iterable<PersonDto> findPersonsBetweenAge(Integer minAge, Integer maxAge) {
        LocalDate minBirthdate = LocalDate.now().minusYears(maxAge);
        LocalDate maxBirthdate = LocalDate.now().minusYears(minAge);
        return personRepository.findByBirthDateBetween(minBirthdate,maxBirthdate)
                .map(person -> modelMapper.map(person, PersonDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<CityPopulationDto> getCitiesPopulation() {
        return null;
        //return personRepository.getCitiesPopulation();
    }

}
