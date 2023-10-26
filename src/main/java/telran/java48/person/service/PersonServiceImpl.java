package telran.java48.person.service;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.java48.person.dao.PersonRepository;
import telran.java48.person.dto.*;
import telran.java48.person.dto.exceptions.PersonNotFoundException;
import telran.java48.person.model.Address;
import telran.java48.person.model.Child;
import telran.java48.person.model.Employee;
import telran.java48.person.model.Person;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService, CommandLineRunner {

    final PersonRepository personRepository;
    final ModelMapper modelMapper;

    @Override
    @Transactional
    public Boolean addPerson(PersonDto personDto) {
        if (personRepository.existsById(personDto.getId())) {
            return false;
        }
        if (personDto instanceof ChildDto) {
            personRepository.save(modelMapper.map(personDto, Child.class));
        } else if (personDto instanceof EmployeeDto) {
            personRepository.save(modelMapper.map(personDto, Employee.class));
        } else if (personDto instanceof PersonDto) {
            personRepository.save(modelMapper.map(personDto, Person.class));
        }
        return true;
    }

    @Override
    public PersonDto findPersonById(Integer id) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        return modelMapper.map(person, getDto(person));
    }


    @Override
    @Transactional
    public PersonDto removePerson(Integer id) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        personRepository.delete(person);
        return modelMapper.map(person, getDto(person));
    }

    @Override
    @Transactional
    public PersonDto updatePersonName(Integer id, String name) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        person.setName(name);
//		personRepository.save(person);
        return modelMapper.map(person, getDto(person));
    }

    @Override
    @Transactional
    public PersonDto updatePersonAddress(Integer id, AddressDto addressDto) {
        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        person.setAddress(modelMapper.map(addressDto, Address.class));
//		personRepository.save(person);
        return modelMapper.map(person, getDto(person));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PersonDto> findPersonsByCity(String city) {
        return personRepository.findByAddressCity(city)
                .map(person -> modelMapper.map(person, getDto(person)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PersonDto> findPersonsByName(String name) {
        return personRepository.findByName(name)
                .map(person -> modelMapper.map(person, getDto(person)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PersonDto> findPersonsBetweenAge(Integer minAge, Integer maxAge) {
        LocalDate minBirthdate = LocalDate.now().minusYears(maxAge);
        LocalDate maxBirthdate = LocalDate.now().minusYears(minAge);
        return personRepository.findByBirthDateBetween(minBirthdate, maxBirthdate)
                .map(person -> modelMapper.map(person, getDto(person)))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<CityPopulationDto> getCitiesPopulation() {
        return personRepository.getCitiesPopulation();
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (personRepository.count() == 0) {
            Person person = new Person(1000, "John", LocalDate.of(1993, 10, 20), new Address("Tel aviv", "Ben gvirol", 87));
            Child child = new Child(2000, "Moshe", LocalDate.of(2018, 7, 5), new Address("Ashkelon", "Bar kohva", 21), "Shalom");
            Employee employee = new Employee(3000, "Sarah", LocalDate.of(1995, 11, 23), new Address("Rehovot", "herzl", 7), "Company", 5000);
            personRepository.save(person);
            personRepository.save(child);
            personRepository.save(employee);
        }
    }

    private Class<? extends PersonDto> getDto(Person person) {
        String className = person.getClass().getSimpleName() + "Dto";
        System.out.println(className + "Class Name!");
        try {
            return (Class<? extends PersonDto>) Class.forName("telran.java48.person.dto." + className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Wrong type Person: " + className, e);
        }
    }
}
