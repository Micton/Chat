package com.javarush.test.level30.lesson15.big01;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message){
        System.out.println(message);
    }

    public static String readString(){
        String result = null;
        while (true) {
            try {
                result = reader.readLine();
            } catch (IOException e) {
                writeMessage("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
            if (result != null) break;
        }
        return result;
    }
    public static int readInt(){
        Integer result = null;
        while (true) {
            try {
                result = Integer.parseInt(readString());
            } catch (NumberFormatException e) {
                writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
            if (result != null) break;
        }
        return  result;
    }
}
