package src.main.java;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        System.out.println("Generated Map:");
        for (Map.Entry<String, String> entry : generatedMap.entrySet()) {
            String fieldName = entry.getKey();
            String getterSetter = entry.getValue();

            System.out.println("Key: " + fieldName + ", Value: " + getterSetter);
        }

        writeUpdatedContentToFile(filePath, updatedContent);
        System.out.println("Getter and setter methods added successfully to the file: " + filePath.getFileName());
    }

    private static Map<String, Object> extractFieldsAndGenerateGettersSetters(String content) {
        StringBuilder updatedContent = new StringBuilder();
        String[] lines = content.split("\\r?\\n");
        String currentClass = null;
        Map<String, String> map = new HashMap<>();
        Pattern variablePattern = Pattern.compile("(\\w+)\\s+(\\w+);");

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("public class")) {
                currentClass = line.substring("public class ".length(), line.indexOf('{')).trim();
            } else {
                Matcher variableMatcher = variablePattern.matcher(line);
                if (variableMatcher.find()) {
                    String dataType = variableMatcher.group(1); // Kiểu dữ liệu của biến
                    String fieldName = variableMatcher.group(2); // Tên của biến
                    String capitalizedName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    if (generateGetterSetter(dataType, fieldName, capitalizedName, updatedContent)) {
                        // Nếu tạo thành công, thêm vào map
                        String getter = currentClass + ".get" + capitalizedName + "()";
                        String setter = currentClass + ".set" + capitalizedName + "(" + dataType + " " + fieldName + ")";
                        map.put(fieldName, getter + "\n" + setter);
                    }
                }
            }
            updatedContent.append(line).append("\n");
        }

        // Lưu kết quả vào map và trả về
        Map<String, Object> result = new HashMap<>();
        result.put("updatedContent", updatedContent.toString());
        result.put("map", map);
        return result;
    }

    private static boolean generateGetterSetter(String dataType, String fieldName, String capitalizedName, StringBuilder updatedContent) {
        updatedContent.append("public ").append(dataType).append(" get").append(capitalizedName).append("() {\n");
        updatedContent.append("    return ").append(fieldName).append(";\n");
        updatedContent.append("}\n\n");
        updatedContent.append("public void set").append(capitalizedName).append("(").append(dataType).append(" ").append(fieldName).append(") {\n");
        updatedContent.append("    this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
        updatedContent.append("}\n\n");

        return true;
    }

    private static void writeUpdatedContentToFile(Path filePath, String updatedContent) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(updatedContent);
        }
    }
}
