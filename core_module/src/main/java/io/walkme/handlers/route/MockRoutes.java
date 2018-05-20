package io.walkme.handlers.route;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MockRoutes {
    private static final List<String> mockRoutes = new ArrayList<>();

    public static void load(String path) throws IOException {
        File file = new File(path);

        if (!file.exists()) {
            throw new IllegalStateException("File must be exists.");
        }

        if (!file.isDirectory()) {
            throw new IllegalStateException("Path must be directory.");
        }

        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("Files not found.");
        }

        BufferedReader bufferedReader;
        String line;
        StringBuilder builder = new StringBuilder();
        for (File f : files) {
            bufferedReader = new BufferedReader(new FileReader(f));
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            mockRoutes.add(builder.toString());
            builder.setLength(0);
        }
        System.out.println("Successfully loaded!");
    }

    public static List<String> mockRoutes() {
        if (mockRoutes.size() == 0) {
            throw new IllegalStateException("Size of mock routes must be more than 0.");
        }
        return mockRoutes;
    }
}
