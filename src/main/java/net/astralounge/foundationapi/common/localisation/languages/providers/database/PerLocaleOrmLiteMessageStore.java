package net.astralounge.foundationapi.common.localisation.languages.providers.database;

import net.astralounge.foundationapi.common.datamanagement.database.DatabaseManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * MessageStore implementation that uses a separate table per locale.
 * <p>
 * Table naming convention:
 *   foundation_messages_{localeTagLowerUnderscored}
 * Example:
 *   Locale "en_UK" -> table "foundation_messages_en_uk"
 * <p>
 * Known locales are tracked in the "foundation_message_locales" registry table.
 */
public class PerLocaleOrmLiteMessageStore implements MessageStore {

    private final ConnectionSource connectionSource;
    private final Dao<LocaleRegistryEntity, String> localeRegistryDao;
    private final Map<String, Dao<LocaleMessageEntity, String>> daosByTable = new HashMap<>();

    private static final String TABLE_PREFIX = "foundation_messages_";

    public PerLocaleOrmLiteMessageStore(DatabaseManager databaseManager) throws SQLException {
        this.connectionSource = databaseManager.getConnectionSource();

        // Ensure registry table exists
        TableUtils.createTableIfNotExists(connectionSource, LocaleRegistryEntity.class);
        this.localeRegistryDao = DaoManager.createDao(connectionSource, LocaleRegistryEntity.class);
    }

    @Override
    public Map<Locale, Map<String, String>> loadAll() throws Exception {
        Map<Locale, Map<String, String>> result = new HashMap<>();

        for (String localeTag : listRegisteredLocaleTags()) {
            Locale locale = Locale.of(localeTag);
            Dao<LocaleMessageEntity, String> dao = getDaoForLocale(locale);

            Map<String, String> messages = new HashMap<>();
            for (LocaleMessageEntity entity : dao.queryForAll()) {
                messages.put(entity.getMessageKey(), entity.getMessage());
            }

            result.put(locale, messages);
        }

        return result;
    }

    @Override
    public String get(Locale locale, String key) throws Exception {
        Dao<LocaleMessageEntity, String> dao = getDaoForLocale(locale);
        LocaleMessageEntity entity = dao.queryForId(key);
        return entity == null ? null : entity.getMessage();
    }

    @Override
    public void set(Locale locale, String key, String message) throws Exception {
        // Ensure locale is registered
        registerLocaleIfNeeded(locale);

        Dao<LocaleMessageEntity, String> dao = getDaoForLocale(locale);
        LocaleMessageEntity existing = dao.queryForId(key);
        if (existing == null) {
            dao.create(new LocaleMessageEntity(key, message));
        } else {
            existing.setMessage(message);
            dao.update(existing);
        }
    }

    @Override
    public void saveAll(Map<Locale, Map<String, String>> messagesByLocale) throws Exception {
        for (Map.Entry<Locale, Map<String, String>> localeEntry : messagesByLocale.entrySet()) {
            Locale locale = localeEntry.getKey();
            registerLocaleIfNeeded(locale);

            Dao<LocaleMessageEntity, String> dao = getDaoForLocale(locale);

            for (Map.Entry<String, String> msgEntry : localeEntry.getValue().entrySet()) {
                String key = msgEntry.getKey();
                String message = msgEntry.getValue();

                LocaleMessageEntity existing = dao.queryForId(key);
                if (existing == null) {
                    dao.create(new LocaleMessageEntity(key, message));
                } else {
                    existing.setMessage(message);
                    dao.update(existing);
                }
            }
        }
    }

    /* ---------------------------------------------------------------------
     * Internal helpers
     * --------------------------------------------------------------------- */

    private void registerLocaleIfNeeded(Locale locale) throws SQLException {
        String tag = locale.toLanguageTag();
        if (localeRegistryDao.queryForId(tag) == null) {
            localeRegistryDao.create(new LocaleRegistryEntity(tag));
        }
    }

    private Dao<LocaleMessageEntity, String> getDaoForLocale(Locale locale) throws Exception {
        String tableName = tableNameForLocale(locale);
        return getDaoForTable(tableName);
    }

    private synchronized Dao<LocaleMessageEntity, String> getDaoForTable(String tableName) throws Exception {
        Dao<LocaleMessageEntity, String> dao = daosByTable.get(tableName);
        if (dao != null) {
            return dao;
        }

        // Build dynamic table config for this table
        DatabaseTableConfig<LocaleMessageEntity> tableConfig =
                DatabaseTableConfig.fromClass(
                        connectionSource.getDatabaseType(),
                        LocaleMessageEntity.class
                );
        tableConfig.setTableName(tableName);

        Dao<LocaleMessageEntity, String> newDao =
                DaoManager.createDao(connectionSource, tableConfig);

        TableUtils.createTableIfNotExists(connectionSource, tableConfig);
        daosByTable.put(tableName, newDao);

        return newDao;
    }

    private static String tableNameForLocale(Locale locale) {
        // e.g. "en_UK" -> "foundation_messages_en_uk"
        String tag = locale.toLanguageTag()
                .toLowerCase(Locale.ROOT)
                .replace('-', '_');
        return TABLE_PREFIX + tag;
    }

    private List<String> listRegisteredLocaleTags() throws SQLException {
        List<String> tags = new ArrayList<>();
        for (LocaleRegistryEntity entity : localeRegistryDao.queryForAll()) {
            tags.add(entity.getLocaleTag());
        }
        return tags;
    }
}