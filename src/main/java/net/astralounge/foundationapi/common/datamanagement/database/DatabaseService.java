package net.astralounge.foundationapi.common.datamanagement.database;

import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Global access point for the shared ConnectionSource managed by
 * the core FoundationAPI plugin (PaperFoundationAPI).
 *
 * - Only the core plugin should call {@link #setConnectionSource(ConnectionSource)}.
 * - Other plugins call {@link #getConnectionSource()} to obtain the shared connection pool.
 */
public final class DatabaseService {

    private static final DatabaseService INSTANCE = new DatabaseService();

    public static DatabaseService getInstance() {
        return INSTANCE;
    }

    private final AtomicReference<ConnectionSource> connectionRef = new AtomicReference<>();

    private DatabaseService() {
    }

    /**
     * Set the global ConnectionSource. Intended to be called once from the
     * core plugin after DatabaseManager has been enabled.
     */
    public void setConnectionSource(ConnectionSource connectionSource) {
        if (connectionRef.get() != null) {
            FoundationLoggerService.getGlobalLogger().warn("DatabaseService already initialised");
            return;
        }
        connectionRef.set(connectionSource);
    }

    /**
     * Clear the global ConnectionSource reference. Intended to be called
     * from DatabaseManager during plugin shutdown after closing the pool.
     */
    public void clear() {
        connectionRef.set(null);
    }

    /**
     * Get the global ConnectionSource, or null if not yet initialised.
     * Callers should handle the null case (e.g. log and skip DB work).
     */
    public @Nullable ConnectionSource getConnectionSource() {
        ConnectionSource cs = connectionRef.get();
        if (cs == null) {
            FoundationLoggerService.getGlobalLogger()
                    .warn("DatabaseService.getConnectionSource() called before it was initialised");
        }
        return cs;
    }
}