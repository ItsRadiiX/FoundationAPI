package nl.bryansuk.foundationapi.common.playerinfo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.UUID;

public class PlayerInfoDAOImpl implements PlayerInfoDAO {

    private final EntityManager entityManager;

    public PlayerInfoDAOImpl(EntityManager em) {
        entityManager = em;
    }

    @Override
    public PlayerInfo findById(String uuid) {
        return findById(UUID.fromString(uuid));
    }

    @Override
    public PlayerInfo findById(UUID uuid) {
        return entityManager.find(PlayerInfo.class, uuid);
    }

    @Override
    public void save(PlayerInfo playerInfo) {
        entityManager.persist(playerInfo);
    }

    @Override
    public void update(PlayerInfo playerInfo) {
        entityManager.merge(playerInfo);
    }

    @Override
    public void delete(PlayerInfo playerInfo) {
        entityManager.remove(playerInfo);
    }
}
