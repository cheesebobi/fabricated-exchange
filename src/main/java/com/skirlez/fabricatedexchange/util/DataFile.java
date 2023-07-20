package com.skirlez.fabricatedexchange.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

import com.skirlez.fabricatedexchange.FabricatedExchange;

// This class represents a JSON file.
public class DataFile<T> {
    private final Type type;
    private final Path path;
    private final String name;
    private T value;
    public DataFile(Type type, String name) {
        this.type = type;
        this.name = name;
        this.path = ModConfig.CONFIG_DIR.resolve(name);
    }

    // Read the file from disk to update the instance
    public void fetch() {
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                value = ModConfig.GSON.fromJson(reader, type);
            } 
            catch (Exception e) {
                FabricatedExchange.LOGGER.error(name + " exists but could not be read!", e);
            }
        }
    }

    public T getValue() {
        return value;
    }

    public T fetchAndGetValue() {
        fetch();
        return value;
    }
    
    // Write the instance's current data to disk
    public void save() {
        if (!Files.exists(ModConfig.CONFIG_DIR)) {
            try {
                Files.createDirectory(ModConfig.CONFIG_DIR);
            }
            catch (Exception e) {
                FabricatedExchange.LOGGER.error("Could not create config directory!", e);
            }
        }
        if (value == null)
            return;
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            ModConfig.GSON.toJson(value, writer);
        } 
        catch (Exception e) {
            FabricatedExchange.LOGGER.error(name + " could not be saved!", e);
        }
    }

    public void setValue(T newValue) {
        value = newValue;
    }

    public void setValueAndSave(T newValue) {
        value = newValue;
        save();
    }

}
