package no.kristiania.person;

import no.kristiania.PersonDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonDaoTest {

    private final PersonDao dao = new PersonDao(PersonDao.createDataSource());

    @Test
    void shouldRetrieveSavedPerson() throws SQLException {
        Person person = randomPerson();
        dao.save(person);
        assertThat(dao.retrieve(person.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(person);
    }

    @Test
    void shouldListPeopleByLastName() throws SQLException {
        Person matchingPerson = randomPerson();
        matchingPerson.setLastName("Testperson");
        dao.save(matchingPerson);

        Person anotherMatchingPerson = randomPerson();
        anotherMatchingPerson.setLastName(matchingPerson.getLastName());
        dao.save(anotherMatchingPerson);

        Person nonMatchingPerson = randomPerson();
        dao.save(nonMatchingPerson);

        assertThat(dao.listByLastName(matchingPerson.getLastName()))
                .extracting(Person::getId)
                .contains(matchingPerson.getId(), anotherMatchingPerson.getId())
                .doesNotContain(nonMatchingPerson.getId());
    }

    private Person randomPerson() {
        Person person = new Person();
        person.setFirstName(pickOne("Vigleik", "John", "Johan", "Joseph", "Jamal"));
        person.setLastName(pickOne("Skogesal", "Almaas", "Golden", "Stephens", "Abram"));
        return person;
    }

    private String pickOne(String... alternatives) {
        return alternatives[new Random().nextInt(alternatives.length)];
    }
}
