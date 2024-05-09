package src.main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapFieldClassDataType {

    public void parseFiletoMap(String folderPath) {
        try {
            Path directoryPath = Path.of(folderPath);

            Files.list(directoryPath)
                    .filter(Files::isRegularFile)
                    //filePath được sử dụng làm biến điều khiển trong phương thức
                    //đại diện cho từng đối tượng Path
                    .forEach(filePath -> {
                        try {
                            String input = readFile(filePath);
                            System.out.println("File: " + filePath.getFileName());
                            String className = extractClassName(input);
                            className = removeEntityFromClassName(className);

                            List<String> fields = extractFields(input);

                            Map<String, String> fieldToClassNameMap = new HashMap<>();
                            Map<String, String> fieldToDataTypeMap = new HashMap<>();
                            Pattern fieldDeclarationPattern = Pattern.compile("(private|protected|public)\\s+(\\S+)\\s+(\\S+);");
                            for (String field : fields) {
                                Matcher matcher = fieldDeclarationPattern.matcher(field);
                                if (matcher.matches()) {
                                    String modifier = matcher.group(1);
                                    String dataType = matcher.group(2);
                                    String fieldName = matcher.group(3);

                                    fieldToClassNameMap.put(fieldName, className);
                                    fieldToDataTypeMap.put(fieldName, dataType);
                                }
                            }

                            for (Map.Entry<String, String> entry : fieldToClassNameMap.entrySet()) {
                                System.out.println(entry.getKey() + " ; " + entry.getValue());
                            }

                            for (Map.Entry<String, String> entry : fieldToDataTypeMap.entrySet()) {
                                System.out.println(entry.getKey() + " ; " + entry.getValue());
                            }

                        } catch (IOException e) {
                            System.err.println("Error reading file: " + filePath + ", " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            System.err.println("Error listing files in directory: " + folderPath + ", " + e.getMessage());
        }
    }

    private static String readFile(Path filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
    private static List<String> extractFields(String input) {
        List<String> fields = new ArrayList<>();

        // tách chuỗi thành các dòng
        String[] lines = input.split("\\r?\\n");

        boolean insideClass = false;

        // duyệt qua từng dòng
        for (String line : lines) {
            line = line.trim(); // xóa khoảng trắng

            if (line.startsWith("private") || line.startsWith("protected")) {
                if (insideClass) {
                    fields.add(line);
                }
            } else if (line.startsWith("public class")) {
                insideClass = true;
            }
        }

        return fields;
    }

    private static String extractClassName(String input) {

        int classKeywordIndex = input.indexOf("class");

        if (classKeywordIndex != -1) {
            //vị trí kết thúc của "class"
            int classNameStartIndex = classKeywordIndex + "class".length();

            //miễn ký tự tại vị trí classNameStartIndex là một ký tự khoảng trắng thì tăng vị trí của start để lấy nó
            //chuy3n63 kí tự từ class -> khoảng trống
            while (classNameStartIndex < input.length() && Character.isWhitespace(input.charAt(classNameStartIndex))) {
                classNameStartIndex++;
            }

            // vị trí kết thúc của tên class
            int classNameEndIndex = classNameStartIndex;
            while (classNameEndIndex < input.length() && !Character.isWhitespace(input.charAt(classNameEndIndex)) && input.charAt(classNameEndIndex) != '{') {
                classNameEndIndex++;
            }
            return input.substring(classNameStartIndex, classNameEndIndex);
        }

        return null;

    }
    private static String removeEntityFromClassName(String className) {
        if (className != null && className.endsWith("Entity")) {
            className = className.substring(0, className.length() - "Entity".length());
        }
        return className;
    }
}
