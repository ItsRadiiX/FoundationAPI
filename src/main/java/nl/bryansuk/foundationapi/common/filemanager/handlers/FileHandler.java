package nl.bryansuk.foundationapi.common.filemanager.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.bryansuk.foundationapi.common.filemanager.FileManager;
import nl.bryansuk.foundationapi.common.filemanager.converter.Converter;
import org.apache.logging.log4j.Level;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unused")
public class FileHandler<T> extends Handler {

    private final Converter<T> converter;
    private final TypeReference<T> typeReference = new TypeReference<>() {};
    private volatile T object;

    private final boolean defaultResource;
    /**
     * Constructs a new FileHandler object with the specified file path and default resource flag.
     * Handler is initialized when constructor is called.
     *
     * @param path            the path to the file
     * @param defaultResource a flag indicating whether the file is a default resource
     */
    public FileHandler(String path, Converter<T> converter, boolean defaultResource, boolean isAutoReloading) {
        super(path, isAutoReloading);
        this.converter = converter;
        this.defaultResource = defaultResource;
        initialize();
    }

    @Override
    public void initialize(){
        FileManager.addHandler(this);
    }

    @Override
    public void destroy(){
        FileManager.removeHandler(this);
        this.object = null;
    }

    /**
     * Gets the object.
     *
     * @return the object
     */
    public T getObject() {
        if (object == null){
            object = read();
        }
        return object;
    }

    @Override
    public boolean onReload() {
        if (isNewVersionAvailable()){
            FileManager.getLogger().debug("Attempting to reload File: {}", getFile().getName());
            read();
            FileManager.getInstance().callFileReloadEvent(getFile().getName());
            return true;
        }
        return false;
    }

    public void write(Object data){
        try {
            converter.writeToFile(data, getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            updateLastModified();
        }
    }

    public T read(){
        try {
            // Check if the file exists
            if (!doesFileExist()) return checkIfObjectExists(false);

            // Read file content
            if (!isFileEmpty()) {
                T resource = converter.readFromFile(new FileInputStream(getFilePath()), typeReference);
                object = resource;
                if (defaultResource) {
                    T defaultResourceObject = converter.readFromFile(FileManager.getInstance().getDefaultResource(getPath()), typeReference);
                    return addDefaults(defaultResourceObject, resource);
                }
                return resource;
            }

            return checkIfObjectExists(true);
        } catch (IOException e) {
            // Log error
            FileManager.getLogger().error(e,"<red>Cannot load file {0}", getPath());
            // Check if there's a previously loaded valid file
            return checkIfObjectExists(true);
        }
    }

    private T checkIfObjectExists(boolean fileExists) {
        if (object != null) {
            FileManager.getLogger().error("<red>A previously loaded file was valid, using it instead.");
            return object;
        }

        if (defaultResource) {
            try {
                T defaultContent = converter.readFromFile(FileManager.getInstance().getDefaultResource(getPath()), typeReference);
                object = defaultContent;
                write(defaultContent); // Write default content to file
                return defaultContent;
            } catch (IOException e) {
                FileManager.getLogger().error(e,"<red>Cannot load file {0}", getPath());
            }
        }

        if (fileExists) throw new NullPointerException("Could not read file and no previous or default version could be loaded. File: " + getPath());
        return null;
    }

    private T addDefaults(T defaultFile, T file) throws IOException {
        ObjectMapper mapper = converter.getObjectMapper();

        if (defaultResource && doesFileExist()){

            JsonNode defaultNode = mapper.readTree(mapper.writeValueAsString(defaultFile));

            JsonNode fileNode = mapper.readTree(mapper.writeValueAsString(file));

            if (fileNode instanceof NullNode) {
                return object;
            }

            if (addMissingFields(defaultNode, (ObjectNode) fileNode)){
                object = mapper.treeToValue(fileNode, typeReference);
            }
            write(object);
        }
        return object;
    }

    private boolean addMissingFields(JsonNode source, ObjectNode target) {
        boolean updated = false;

        for (Iterator<Map.Entry<String, JsonNode>> it = source.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            String fieldName = entry.getKey();
            JsonNode value = entry.getValue();

            if (!target.has(fieldName)) {
                updated = true;
                target.set(fieldName, value);
            }
        }

        return updated;
    }
}
