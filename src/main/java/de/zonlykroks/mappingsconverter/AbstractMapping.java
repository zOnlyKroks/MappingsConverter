package de.zonlykroks.mappingsconverter;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.fabricmc.lorenztiny.TinyMappingFormat;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Getter
public abstract class AbstractMapping {

    private final String name;
    private final String adress;
    private final boolean compression;

    public File mappingsFile;
    public MappingsReader officialToNamed;
    public MappingsReader namedToOfficial;

    public AbstractMapping(String name, String adress, boolean compression){
        this.name = name;
        this.adress = adress;
        this.compression = compression;
    }

    public abstract File download(String version) throws IOException;

    public abstract File decompressIfNeeded(File file) throws IOException;

    public abstract String getNamedClassFromObf(String obf) throws IOException;
    public abstract String getNamedFieldFromObf(String clazz,String obf) throws IOException;
    public abstract String getObfFieldFromNamed(String clazz, String named) throws IOException;
    public abstract String getObfClassFromNamed(String named) throws IOException;

    public abstract String convert(AbstractMapping toConvertTo,String in, String out) throws IOException;

    public void setMappingsFile(File file) {
        this.mappingsFile = file;
        try {
            this.officialToNamed = TinyMappingFormat.DETECT.createReader(this.mappingsFile.toPath(), "official", "named");
            this.namedToOfficial = TinyMappingFormat.DETECT.createReader(this.mappingsFile.toPath(), "named", "official");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
