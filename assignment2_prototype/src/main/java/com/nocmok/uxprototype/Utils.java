package com.nocmok.uxprototype;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class Utils {

    private Utils() {

    }

    public static String readFileAsString(File file, Charset charset) {
        try (FileReader in = new FileReader(file, charset); BufferedReader reader = new BufferedReader(in)) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException("failed to read file due to i/o error", e);
        }
    }

    public static Map<String, List<String>> parseLayoutJson(File file, Charset charset) {
        String json = readFileAsString(file, charset);
        return parseLayoutJson(json);
    }

    public static Map<String, List<String>> parseLayoutJson(String json) {
        JSONObject jobj = new JSONObject(json);
        Iterator<String> keys = jobj.keys();
        Map<String, List<String>> layout = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            List<Object> charsObj = jobj.getJSONArray(key).toList();
            List<String> chars = new ArrayList<>();
            for (Object charObj : charsObj) {
                chars.add(charObj.toString());
            }
            layout.put(key, chars);
        }
        return layout;
    }

    public static List<Word> readDictionaryCSV(File file, Charset charset) {
        List<Word> wordsList = new ArrayList<Word>();
        try (FileReader fr = new FileReader(file, charset); BufferedReader reader = new BufferedReader(fr)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(",");
                wordsList.add(new Word(splitLine[0].trim(), Integer.parseInt(splitLine[2].trim())));
            }
            return wordsList;
        } catch (IOException e) {
            throw new RuntimeException("failed to read dictionary due to i/o error", e);
        }
    }

    public static List<String> readLines(File file, Charset charset) {
        try (FileReader fr = new FileReader(file, charset); BufferedReader reader = new BufferedReader(fr)) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeStringToFile(File dest, Charset charset, String content) {
        try (FileWriter out = new FileWriter(dest, charset); BufferedWriter writer = new BufferedWriter(out)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
