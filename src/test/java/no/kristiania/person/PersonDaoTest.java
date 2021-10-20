package no.kristiania.person;

import no.kristiania.PersonDao;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonDaoTest {

    @Test
    void shouldRetriveSavedPerson() throws SQLException {
        PersonDao dao = new PersonDao(createDataSource());

        Person person = randomPerson();
        dao.save(person);
        assertThat(dao.retrieve(person.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(person);
    }

    private DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/person_db");
        dataSource.setUser("person_dbuser");
        dataSource.setPassword("SrAwsd?s5#");
        return dataSource;
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
