package net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.models;

public enum ReloadResult {
    UNKNOWN,
    NOT_INITIALISED,
    NO_UPDATE,
    READ_NEW_FILE,
        ERROR_READING_FILE,
    FOLDER_RELOADED,
        NOT_ALL_FOLDER_FILES_RELOADED,
    REVERTED_TO_BACKUP,
    CANNOT_MAKE_BACKUP
}
