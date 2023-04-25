import java.io.BufferedReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReader {
    public static List<Language> parse(String dir) {
        List<Language> data = new ArrayList<>();

        try (DirectoryStream<Path> directories = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path directory : directories) {
                if (Files.isDirectory(directory)) {
                    String language = directory.getFileName().toString();

                    try (Stream<Path> files = Files.walk(directory)) {
                        List<Path> fileList = files.filter(Files::isRegularFile).collect(Collectors.toList());
                        for (Path file : fileList) {
                            String text = readTextFromFile(file);
                            data.add(new Language(language, text));
                        }
                    }
                }
            }
            Collections.shuffle(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private static String readTextFromFile(Path file) {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line.toLowerCase().replaceAll("[^a-zA-Z]", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}