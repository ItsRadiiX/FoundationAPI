package nl.bryansuk.foundationapi.common.filemanager.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JSONConverter<T> implements Converter<T> {

    private static final ObjectMapper mapper = initializeJSONMapper();

    private static ObjectMapper initializeJSONMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    @Override
    public void writeToFile(Object data, File file) throws IOException {
        // Make sure all directories have been made
        file.getParentFile().mkdirs();
        mapper.writeValue(file, data);
    }

    @Override
    public T readFromFile(InputStream inputStream, TypeReference<T> typeReference) throws IOException {
        if (inputStream == null) return null;
        return mapper.readValue(inputStream, typeReference);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return mapper;
    }
}
