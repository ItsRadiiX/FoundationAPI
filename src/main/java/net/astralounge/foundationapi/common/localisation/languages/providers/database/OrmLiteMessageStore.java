package net.astralounge.foundationapi.common.localisation.languages.providers.database;

import net.astralounge.foundationapi.common.datamanagement.database.DatabaseManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ORMLite-backed MessageStore implementation using the shared DatabaseManager.
 */
public class OrmLiteMessageStore implements MessageStore {

    private final Dao<MessageEntity, String> dao;

    public OrmLiteMessageStore(DatabaseManager databaseManager) throws Exception {
        ConnectionSource cs = databaseManager.getConnectionSource();
        this.dao = DaoManager.createDao(cs, MessageEntity.class);
        TableUtils.createTableIfNotExists(cs, MessageEntity.class);

        // Ensure table exists
        dao.setObjectCache(true);
    }

    @Override
    public Map<Locale, Map<String, String>> loadAll() throws Exception {
        Map<Locale, Map<String, String>> result = new HashMap<>();

        List<MessageEntity> all = dao.queryForAll();
        for (MessageEntity entity : all) {
            Locale locale = Locale.of(entity.getLocaleTag());
            result
                    .computeIfAbsent(locale, l -> new HashMap<>())
                    .put(entity.getMessageKey(), entity.getMessage());
        }

        return result;
    }

    @Override
    public String get(Locale locale, String key) throws Exception {
        String compositeId = locale.toLanguageTag() + "|" + key;
        MessageEntity entity = dao.queryForId(compositeId);
        return entity == null ? null : entity.getMessage();
    }

    @Override
    public void set(Locale locale, String key, String message) throws Exception {
        String localeTag = locale.toLanguageTag();
        String compositeId = localeTag + "|" + key;

        MessageEntity existing = dao.queryForId(compositeId);
        if (existing == null) {
            dao.create(new MessageEntity(localeTag, key, message));
        } else {
            existing.setMessage(message);
            dao.update(existing);
        }
    }

    @Override
    public void saveAll(Map<Locale, Map<String, String>> messagesByLocale) throws Exception {
        for (Map.Entry<Locale, Map<String, String>> localeEntry : messagesByLocale.entrySet()) {
            Locale locale = localeEntry.getKey();
            for (Map.Entry<String, String> msgEntry : localeEntry.getValue().entrySet()) {
                set(locale, msgEntry.getKey(), msgEntry.getValue());
            }
        }
    }
}