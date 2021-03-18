package com.nocmok.uxprototype.layouts;

import java.net.URL;

public class Layouts {
    
    public static URL get(String name){
        return Layouts.class.getClassLoader().getResource("layouts/" + name);
    }
}
