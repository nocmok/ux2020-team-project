package com.nocmok.uxprototype;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class UtilsTest {

    @Test
    public void testReadFileAsString() {
        File file = new File("./src/main/resources/keyboard_layouts/layout1.json");
        System.out.println(file.getAbsolutePath());
        String str = Utils.readFileAsString(file, Charset.forName("UTF8"));
        System.out.println(str);
    }

    @Test
    public void testReadLayoutJson() {
        File file = new File("./src/main/resources/keyboard_layouts/layout1.json");
        Map<String, List<String>> layout = Utils.parseLayoutJson(file, Charset.forName("UTF8"));
        for(var entry : layout.entrySet()){
            System.out.print(entry.getKey() + ": ");
            System.out.println(entry.getValue());            
        }
    }

    @Test
    public void testSplit(){
        String str = " asd    dasd dass das d   ";
        String[] words = str.trim().split("\\s+");
        for(String word : words){
            System.out.println(word);
        }
        System.out.println(words.length);
    }

    @Test
    public void testMethodReference(){
        Runnable lambda1 = this::method;
        Runnable lambda2 = this::method;
        System.out.println(lambda1);
        System.out.println(lambda2);

    }

    public void method(){

    }
}
