package com.itsradiix.foundationapi.common.playerinfo;

import java.util.UUID;

public interface PlayerInfoDAO {
    PlayerInfo findById(String uuid);
    PlayerInfo findById(UUID uuid);
    void save(PlayerInfo playerInfo);
    void update(PlayerInfo playerInfo);
    void delete(PlayerInfo playerInfo);
}
