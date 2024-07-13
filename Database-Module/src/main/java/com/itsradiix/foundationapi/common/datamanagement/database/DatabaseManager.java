package com.itsradiix.foundationapi.common.datamanagement.database;

import com.itsradiix.foundationapi.common.manager.CommonManager;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseManager implements CommonManager {
    private static SessionFactory sessionFactory;

    public DatabaseManager() {}

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable(){
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
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
}
