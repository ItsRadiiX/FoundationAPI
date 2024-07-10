package nl.bryansuk.foundationapi.common.datamanagement.database;

import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfoDAO;
import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfoDAOImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {
    private static SessionFactory sessionFactory;
    private static PlayerInfoDAO playerInfoDAO;

    public void onEnable(String path){
        Configuration configuration = new Configuration();
        configuration.configure(path);
        sessionFactory = configuration.buildSessionFactory();
        playerInfoDAO = new PlayerInfoDAOImpl(sessionFactory.createEntityManager());
    }

    public void onDisable(){
        if (sessionFactory != null){
            sessionFactory.close();
        }
    }

    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    public static PlayerInfoDAO getPlayerInfoDAO() {
        return playerInfoDAO;
    }
}
