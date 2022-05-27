package de.zonlykroks.mappingsconverter.impl;

import de.zonlykroks.mappingsconverter.AbstractMapping;
import net.fabricmc.lorenztiny.TinyMappingFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class HashedMojMap extends AbstractMapping {

    public HashedMojMap() {
        super("HashedMojMap", "https://maven.quiltmc.org/repository/release/org/quiltmc/hashed/&release&/hashed-&release&.tiny", false);
    }

    @Override
    public File download(String version) throws IOException {
        String file = this.getAdress().replace("&release&",version);
        File destinationFile = new File(StringUtils.substringAfterLast(file,"/"));
        FileUtils.copyURLToFile(new URL(file), destinationFile);
        return destinationFile;
    }

    //Decompression not needed here!
    @Override
    public File decompressIfNeeded(File file) throws IOException {
        return null;
    }

    @Override
    public String getNamedClassFromObf(String obf) throws IOException {
        MappingSet mappingSet = getOfficialToNamed().read();
        return mappingSet.getClassMapping(obf).orElseThrow().getFullDeobfuscatedName();
    }

    @Override
    public String getNamedFieldFromObf(String clazz, String obf) throws IOException {
        MappingSet mappingSet = getOfficialToNamed().read();
        ClassMapping classMapping = mappingSet.getClassMapping(clazz).orElseThrow();
        Optional<FieldMapping> fieldMapping = classMapping.getFieldMapping(obf);
        return fieldMapping.orElseThrow().getFullDeobfuscatedName();
    }

    @Override
    public String convert(AbstractMapping toConvertTo,String in, String out) throws IOException {
        return null;
    }

    @Override
    public void setMappingsFile(File file) {
        this.mappingsFile = file;
        try{
            this.officialToNamed = TinyMappingFormat.DETECT.createReader(this.mappingsFile.toPath(),"official","hashed");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
