package confeitaria.app;

import model.entities.Address;
import model.entities.Area;
import model.entities.Person;
import model.entities.User;
import model.repositories.CreateTables;
import model.repositories.RepositoryAddress;
import model.repositories.RepositoryArea;
import model.repositories.RepositoryPerson;
import model.repositories.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste automatizado para o cadastro de usuário
 * Fluxo: Area -> Address -> Person -> User (Ordem das dependências).
 */
public class UserRegistrationTest {

    private RepositoryArea repositoryArea;
    private RepositoryAddress repositoryAddress;
    private RepositoryPerson repositoryPerson;
    private RepositoryUser repositoryUser;

    @BeforeEach
    void setUp() throws SQLException{
        CreateTables.createAllTables();
        CreateTables.truncateAllTables();
        repositoryArea = new RepositoryArea();
        repositoryAddress = new RepositoryAddress();
        repositoryPerson = new RepositoryPerson();
        repositoryUser = new RepositoryUser();
    }
    
    @Test
    void cadastrarUsuario() throws Exception{

        // 1. Cria area
        Area area = new Area("Centro",10.0);
        boolean areaCriada = repositoryArea.createArea(area);
        assertTrue(areaCriada,"Area deve ser criada");

        area = repositoryArea.findByNameArea("Centro");
        assertNotNull(area,"Area deve ser encontrada por nome");
        assertNotNull(area.getId(),"Area deve ter id após persistência");

        // 2. Criar Address (com a Area)
        Address address = new Address(area, "12345678", "Rua das Flores", 100, "Sala 1", "Próximo ao mercado");
        boolean addressCriado = repositoryAddress.createAddress(address);
        assertTrue(addressCriado,"Address deve ser criado!");

        List<Address> enderecos = repositoryAddress.findAllAddress();
        assertFalse(enderecos.isEmpty(), "Deve existir pelo menos um endereço");
        address = enderecos.get(enderecos.size() - 1);
        assertNotNull(address.getInteger(), "Address deve ter id após persistência");

        // 3. Criar Person (com o Address)
        Person person = new Person("Maria", "Silva", "maria.silva@email.com", address);
        boolean personCriada = repositoryPerson.createPerson(person);
        assertTrue(personCriada, "Person deve ser criada");

        person = repositoryPerson.findByEmailPerson("maria.silva@email.com");
        assertNotNull(person, "Person deve ser encontrada por email");
        assertNotNull(person.getId(), "Person deve ter id após persistência");

        // 4. Criar User (com o id da Person e senha)
        char[] senha = "senha123".toCharArray();
        User user = new User(person.getId(), "Maria", "Silva", "maria.silva@email.com", senha);
        boolean userCriado = repositoryUser.createUser(user);
        assertTrue(userCriado, "User deve ser criado");

        // 5. Validar: buscar usuário por email
        User encontrado = repositoryUser.findByEmailUser("maria.silva@email.com");
        assertNotNull(encontrado, "Usuário deve ser encontrado por email após cadastro");
        assertEquals("maria.silva@email.com", encontrado.getEmail(), "Email deve coincidir");
        assertEquals("Maria", encontrado.getFirstName(), "Nome deve coincidir");
        assertEquals("Silva", encontrado.getLastName(), "Sobrenome deve coincidir");
    }
    
}
