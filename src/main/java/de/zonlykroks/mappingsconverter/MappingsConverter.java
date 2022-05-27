package de.zonlykroks.mappingsconverter;

import de.zonlykroks.mappingsconverter.impl.HashedMojMap;
import de.zonlykroks.mappingsconverter.impl.QuiltMapping;
import de.zonlykroks.mappingsconverter.impl.YarnMapping;
import net.fabricmc.lorenztiny.TinyMappingFormat;
import org.apache.commons.io.FileUtils;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MappingsConverter {

    public static final File mappingsFolder = new File(Paths.get("").toAbsolutePath() + "/mappings");

    public static final List<AbstractMapping> supportedMappings = Arrays.asList(new QuiltMapping(),new HashedMojMap(),new YarnMapping());

    public static void main(String[] args) throws IOException {
        if(!mappingsFolder.exists())
            mappingsFolder.mkdirs();

        supportedMappings.forEach(abstractMapping -> {
            if(abstractMapping instanceof QuiltMapping) {
                try {
                    QuiltMapping quiltMapping = (QuiltMapping) abstractMapping;
                    File file = abstractMapping.download("22w19a+build.9");

                    if (abstractMapping.isCompression()) file = abstractMapping.decompressIfNeeded(file);

                    quiltMapping.setMappingsFile(createAndMoveFile(file));
                    System.out.println("Quilt");
                    System.out.println(quiltMapping.getNamedClassFromObf("a"));
                    System.out.println(quiltMapping.getNamedFieldFromObf("a","b"));

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(abstractMapping instanceof HashedMojMap) {
                try {
                    HashedMojMap hashedMojMap = (HashedMojMap) abstractMapping;
                    File file = abstractMapping.download("22w19a");

                    if (abstractMapping.isCompression()) file = abstractMapping.decompressIfNeeded(file);

                    hashedMojMap.setMappingsFile(createAndMoveFile(file));

                    System.out.println("Hashed MojMap");
                    System.out.println(hashedMojMap.getNamedClassFromObf("a"));
                    System.out.println(hashedMojMap.getNamedFieldFromObf("a","b"));

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(abstractMapping instanceof YarnMapping) {
                try {
                    YarnMapping yarnMapping = (YarnMapping) abstractMapping;
                    File file = abstractMapping.download("22w19a+build.6");

                    if (abstractMapping.isCompression()) file = abstractMapping.decompressIfNeeded(file);

                    yarnMapping.setMappingsFile(createAndMoveFile(file));

                    System.out.println("Yarn");
                    System.out.println(yarnMapping.getNamedClassFromObf("a"));
                    System.out.println(yarnMapping.getNamedFieldFromObf("a","b"));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        test();
    }

    public static File createAndMoveFile(File fileToMove) throws IOException {
        File newFile = new File(mappingsFolder + "/" + fileToMove.getName());
        if(newFile.exists()) {
            newFile.delete();
        }
        FileUtils.moveFile(fileToMove,newFile);
        fileToMove.delete();
        return newFile;
    }

    public static void test() {
        supportedMappings.forEach(abstractMapping -> {
            try {
                if(!(abstractMapping instanceof YarnMapping)) return;

                YarnMapping mapping = (YarnMapping) abstractMapping;

                MappingSet mappingSet = mapping.getOfficialToNamed().read();

                String map = mappingSet.getClassMapping("net/minecraft/util/math/MathConstants").orElseThrow().getFullObfuscatedName();
                System.out.println(map);
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
