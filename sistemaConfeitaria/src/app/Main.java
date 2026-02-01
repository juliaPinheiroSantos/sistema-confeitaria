package app;

import model.repositories.CreateTables;
import model.entities.User;
import model.repositories.RepositoryUser;
import services.EncryptionService;

public class Main {

    public static void main(String[] args) {
        try {
            CreateTables.createAllTables();
            System.out.println("Tabelas criadas com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}