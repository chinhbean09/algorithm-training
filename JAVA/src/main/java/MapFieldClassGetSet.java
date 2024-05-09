package src.main.java;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MapFieldClassGetSet {
    public static void generateGettersAndSetters(String folderPath) {
        try {
            Path directoryPath = Path.of(folderPath);

            Files.list(directoryPath)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            processFile(filePath);
                        } catch (IOException e) {
                            System.err.println("Error processing file: " + filePath + ", " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            System.err.println("Error listing files in directory: " + folderPath + ", " + e.getMessage());
        }
    }

    private static void processFile(Path filePath) throws IOException {
        StringBuilder classContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                classContent.append(line).append("\n");
            }
        }

        Map<String, Object> resultMap = extractFieldsAndGenerateGettersSetters(classContent.toString());

        String updatedContent = (String) resultMap.get("updatedContent");
        Map<String, String> generatedMap = (Map<String, String>) resultMap.get("map");


        writeUpdatedContentToFile(filePath, updatedContent);
        for (Map.Entry<String, String> entry : generatedMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        System.out.println("Getter and setter methods added successfully to the file: " + filePath.getFileName());
    }

    private static Map<String, Object> extractFieldsAndGenerateGettersSetters(String content) {
        StringBuilder updatedContent = new StringBuilder();
        String[] lines = content.split("\\r?\\n");
        String currentClass = null;
        Map<String, String> map = new HashMap<>();

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("public class")) {
                currentClass = line.substring("public class ".length(), line.indexOf('{')).trim();

            } else if (line.trim().startsWith("String") || line.trim().startsWith("Integer")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String dataType = parts[0];
                    String fieldName = parts[1].replace(";", "");
                    String capitalizedName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    map.put(fieldName, currentClass + ".get" + fieldName + "()");
                    map.put(fieldName, currentClass + ".set" + fieldName+ "()");
                    updatedContent.append("public " + dataType + " get").append(capitalizedName).append("() {\n");
                    updatedContent.append("    return ").append(fieldName).append(";\n");
                    updatedContent.append("}\n\n");

                    updatedContent.append("public void set ").append(capitalizedName).append( "(" + dataType + " ").append(fieldName).append(") {\n");
                    updatedContent.append("    this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
                    updatedContent.append("}\n\n");
                }
            } else if (line.contains("public class")) {
                String innerClassName = line.substring(line.indexOf("class") + 5, line.indexOf('{')).trim();
                currentClass = currentClass + "." + innerClassName;
            }
            updatedContent.append(line).append("\n");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("updatedContent", updatedContent.toString());
        result.put("map", map);
        return result;
    }

    private static void writeUpdatedContentToFile(Path filePath, String updatedContent) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(updatedContent);
        }
    }
}
