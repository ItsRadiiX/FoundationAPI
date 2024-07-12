package com.itsradiix.foundationapi.common.datamanagement.database;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {
    private static SessionFactory sessionFactory;

    public void onEnable(String path){
        Configuration configuration = new Configuration();
        configuration.configure(path);
        sessionFactory = configuration.buildSessionFactory();
    }

    public void onDisable(){
        if (sessionFactory != null){
            sessionFactory.close();
        }
    }

    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }
}
