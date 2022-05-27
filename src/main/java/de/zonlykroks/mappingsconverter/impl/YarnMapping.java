package de.zonlykroks.mappingsconverter.impl;

import de.zonlykroks.mappingsconverter.AbstractMapping;
import net.fabricmc.lorenztiny.TinyMappingFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.signature.MethodSignature;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class YarnMapping extends AbstractMapping {

    public YarnMapping() {
        super("YarnMapping", "https://maven.fabricmc.net/net/fabricmc/yarn/&release&/yarn-&release&-tiny.gz", true);
    }

    @Override
    public File download(String version) throws IOException {
        String file = this.getAdress().replace("&release&",version);
        File destinationFile = new File(StringUtils.substringAfterLast(file,"/"));
        FileUtils.copyURLToFile(new URL(file), destinationFile);
        return destinationFile;
    }

    @Override
    public File decompressIfNeeded(File compressedFile) throws IOException {
        String s = StringUtils.substringBeforeLast(compressedFile.getAbsolutePath(), "-");
        s = StringUtils.appendIfMissing(s,".tiny");

        try (GZIPInputStream gis = new GZIPInputStream(
                new FileInputStream(compressedFile));
             FileOutputStream fos = new FileOutputStream(Path.of(s).toFile())) {

            // copy GZIPInputStream to FileOutputStream
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

        }

        compressedFile.delete();

        return new File(s);
    }

    @Override
    public String convert(AbstractMapping toConvertTo,String in, String out) throws IOException {
        return null;
    }

    @Override
    public String getNamedClassFromObf(String obf) throws IOException {
        MappingSet mappingSet = getOfficialToNamed().read();
        return mappingSet.getClassMapping(obf).orElseThrow().getFullDeobfuscatedName();
    }

    @Override
    public String getNamedFieldFromObf(String clazz,String obf) throws IOException {
        MappingSet mappingSet = getOfficialToNamed().read();
        ClassMapping classMapping = mappingSet.getClassMapping(clazz).orElseThrow();
        Optional<FieldMapping> fieldMapping = classMapping.getFieldMapping(obf);
        return fieldMapping.orElseThrow().getFullDeobfuscatedName();
    }
}
