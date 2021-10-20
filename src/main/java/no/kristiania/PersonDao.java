package no.kristiania;

import no.kristiania.person.Person;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PersonDao {

    private DataSource dataSource;

    public PersonDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/person_db");
        dataSource.setUser("person_dbuser");
        dataSource.setPassword("SrAwsd?s5#");
        return dataSource;
    }

    public void save(Person person) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement("insert into people (first_name, last_name) values (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, person.getFirstName());
                statement.setString(2, person.getLastName());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    person.setId(rs.getLong("id"));
                }
            }
        }
    }

    public Person retrieve(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from people where id = ?")) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return mapFromResultSet(rs);

                }
            }
        }
    }

    private Person mapFromResultSet(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        return person;
    }

    public List<Person> listByLastName(String lastName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from people where last_name = ?")) {

                statement.setString(1, lastName);

                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Person> people = new ArrayList<>();

                        while (rs.next()) {
                            people.add(mapFromResultSet(rs));
                        }

                        return people;
                    }
                }
            }

    }

    public static void main(String[] args) throws SQLException {
        PersonDao dao = new PersonDao((createDataSource()));

        System.out.println("Please enter a last name: ");

        Scanner scanner = new Scanner(System.in);
        String lastName = scanner.nextLine().trim();

        System.out.println(dao.listByLastName(lastName));
    }
}
