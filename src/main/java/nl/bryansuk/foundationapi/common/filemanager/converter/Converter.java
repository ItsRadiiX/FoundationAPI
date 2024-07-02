package nl.bryansuk.foundationapi.common.filemanager.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Converter<T> {
    void writeToFile(Object data, File file) throws IOException;
    T readFromFile(InputStream inputStream, TypeReference<T> typeReference) throws IOException;

    default ObjectMapper getObjectMapper() {
        return null;
    }
}
