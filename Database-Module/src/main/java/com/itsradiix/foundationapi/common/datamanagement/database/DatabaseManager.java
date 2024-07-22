package com.itsradiix.foundationapi.common.datamanagement.database;

import com.itsradiix.foundationapi.common.datamanagement.files.converter.YAMLConverter;
import com.itsradiix.foundationapi.common.datamanagement.files.handlers.ConfigurationHandler;
import com.itsradiix.foundationapi.common.manager.CommonManager;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.UUID;

public class DatabaseManager implements CommonManager {
    private static SessionFactory sessionFactory;

    private EntityManager entityManager;
    private ConfigurationHandler configurationHandler;

    public DatabaseManager() {

    }

    @Override
    public void onLoad() {
        configurationHandler = new ConfigurationHandler("configuration/database.yml", new YAMLConverter<>(), true, false);
    }

    @Override
    public void onEnable(){
        Configuration configuration = new Configuration();

        String username = configurationHandler.getString("username");
        String password = configurationHandler.getString("password");
        String host = configurationHandler.getString("host");
        String port = configurationHandler.getString("port");
        String database = configurationHandler.getString("database");

        String driver = configurationHandler.getString("driver");

        configuration.setProperty("hibernate.connection.driver_class", driver);

        configuration.setProperty("hibernate.connection.url", configurationHandler.getString("url"));
        configuration.setProperty("hibernate.connection.username", configurationHandler.getString("username"));
        configuration.setProperty("hibernate.connection.password", configurationHandler.getString("password"));
        configuration.setProperty("hibernate.connection.dialect", configurationHandler.getString("dialect"));
        configuration.setProperty("hibernate.connection.show_sql", configurationHandler.getString("show_sql"));

        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        entityManager = sessionFactory.openSession();
    }

    @Override
    public void onDisable(){
        if (sessionFactory != null){
            sessionFactory.close();
        }
    }

    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    public <T> T findById(String uuid, Class<T> entityClass) {
        UUID id = UUID.fromString(uuid);
        return findById(id, entityClass);
    }

    public <T> T findById(UUID uuid, Class<T> entityClass) {
        return entityManager.find(entityClass, uuid);
    }

    public <T> void save(T object) {
        entityManager.persist(object);
    }

    public <T> void update(T object) {
        entityManager.merge(object);
    }

    public <T> void delete(T object) {
        entityManager.remove(object);
    }
}
