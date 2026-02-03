package controller;

import app.FlowHandler;
import model.entities.Address;
import model.entities.Area;
import model.entities.Person;
import model.entities.User;
import model.repositories.RepositoryAddress;
import model.repositories.RepositoryArea;
import model.repositories.RepositoryPerson;
import model.repositories.RepositoryUser;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Registration logic: validation, duplicate email check, and persistence.
 * Dependencies injected via constructor.
 */
public class ControllerCadastro {

    private final RepositoryPerson repoPerson;
    private final RepositoryUser repoUser;
    private final RepositoryAddress repoAddress;
    private final RepositoryArea repoArea;

    public ControllerCadastro(RepositoryPerson repoPerson, RepositoryUser repoUser,
                              RepositoryAddress repoAddress, RepositoryArea repoArea) {
        this.repoPerson = repoPerson;
        this.repoUser = repoUser;
        this.repoAddress = repoAddress;
        this.repoArea = repoArea;
    }

    /**
     * Returns true if a person with this email already exists.
     */
    public boolean isEmailAlreadyRegistered(String email) {
        try {
            return repoPerson.findByEmailPerson(email) != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Lists areas for the form (e.g. area/region combo).
     */
    public List<Area> listAreas() throws SQLException {
        return repoArea.findAllArea();
    }

    /**
     * Performs registration: creates address, person and user.
     *
     * @return null on success; error message on failure
     */
    public String register(String firstName, String lastName, String email,
                            char[] password, Integer idArea, String street, Integer number,
                            String cep, String complement, String reference) {
        FlowHandler.log("REGISTER_SUBMIT");
        Optional<String> validationError = validateRequiredFields(firstName, email, password, idArea, street);
        if (validationError.isPresent()) {
            clearPassword(password);
            FlowHandler.log("REGISTER_ERROR", validationError.get());
            return validationError.get();
        }

        try {
            if (repoPerson.findByEmailPerson(email.trim()) != null) {
                clearPassword(password);
                FlowHandler.log("REGISTER_ERROR", "email_already_registered");
                return "Email already registered. Use another email or log in.";
            }

            Area area = repoArea.findByIdArea(idArea);
            if (area == null) {
                clearPassword(password);
                FlowHandler.log("REGISTER_ERROR", "invalid_area");
                return "Invalid area.";
            }

            Optional<String> persistError = persistUser(
                    firstName, lastName, email, password, area,
                    street, number, cep, complement, reference
            );
            clearPassword(password);
            if (persistError.isPresent()) {
                FlowHandler.log("REGISTER_ERROR", persistError.get());
                return persistError.get();
            }
            FlowHandler.log("REGISTER_SUCCESS");
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            clearPassword(password);
            FlowHandler.log("REGISTER_ERROR", "db: " + e.getMessage());
            return "Database error: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            clearPassword(password);
            FlowHandler.log("REGISTER_ERROR", e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    private static Optional<String> validateRequiredFields(String firstName, String email,
                                                           char[] password, Integer idArea, String street) {
        if (isBlank(firstName)) return Optional.of("First name is required.");
        if (isBlank(email)) return Optional.of("Email is required.");
        if (password == null || password.length == 0) return Optional.of("Password is required.");
        if (idArea == null) return Optional.of("Please select an area.");
        if (isBlank(street)) return Optional.of("Street is required.");
        return Optional.empty();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String trimOrNull(String s) {
        return s == null ? null : s.trim();
    }

    private static void clearPassword(char[] password) {
        if (password != null) Arrays.fill(password, '0');
    }

    private Optional<String> persistUser(String firstName, String lastName, String email,
                                          char[] password, Area area, String street, Integer number,
                                          String cep, String complement, String reference) throws Exception {
        Address address = buildAddress(area, street, number, cep, complement, reference);
        Integer idAddress = repoAddress.createAddressAndReturnId(address);
        if (idAddress == null) return Optional.of("Error saving address.");

        address.setInteger(idAddress);
        Person person = new Person(firstName.trim(), trimOrNull(lastName), email.trim(), address);
        Integer idPerson = repoPerson.createPersonAndReturnId(person);
        if (idPerson == null) return Optional.of("Error saving personal data (email may already be in use).");

        person.setId(idPerson);
        User user = new User(idPerson, firstName.trim(), trimOrNull(lastName), email.trim(), password);
        if (!repoUser.createUser(user)) return Optional.of("Error creating user.");
        return Optional.empty();
    }

    private static Address buildAddress(Area area, String street, Integer number,
                                        String cep, String complement, String reference) {
        Address address = new Address();
        address.setArea(area);
        address.setCep(trimOrNull(cep));
        address.setStreet(street.trim());
        address.setNumber(number);
        address.setComplement(trimOrNull(complement));
        address.setReference(trimOrNull(reference));
        return address;
    }
}
