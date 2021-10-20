package no.kristiania;

import no.kristiania.person.Person;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloDatabase {

    private Person person;
    private DataSource dataSource;

    public HelloDatabase(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/person_db");
        dataSource.setUser("person_dbuser");
        dataSource.setPassword("SrAwsd?s5#");

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from people")) {

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(rs.getString("last_name"));
                    }
                }
            }
        }
    }

    public void save(Person person) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into people (first_name) values (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, person.getFirstName());
                statement.executeUpdate();

                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                person.setId(rs.getLong("id"));
            }
        }
        this.person = person;
    }

    public Person retrieve(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from people where id = ?")) {
                statement.setLong(1, id);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    Person person = new Person();
                    person.setFirstName(rs.getString("first_name"));
                    return person;
                }
            }
        }
        return person;
    }
}
