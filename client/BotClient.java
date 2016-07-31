package com.javarush.test.level30.lesson15.big01.client;


import com.javarush.test.level30.lesson15.big01.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BotClient extends Client {

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSentTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + new Random().nextInt(100);
    }

    public class BotSocketThread extends Client.SocketThread {

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Hi chat. I'am bot. Taking command: date, day, month, year, time, hours, minutes, seconds.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (message.contains(": ")) {
                String[] userAndMessage = message.split(": ");
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat format = null;
                switch (userAndMessage[1]) {
                    case "date":
                        format = new SimpleDateFormat("d.MM.YYYY", Locale.ENGLISH);
                        break;
                    case "day":
                        format = new SimpleDateFormat("d", Locale.ENGLISH);
                        break;
                    case "month":
                        format = new SimpleDateFormat("MMMM", Locale.ENGLISH);
                        break;
                    case "year":
                        format = new SimpleDateFormat("YYYY", Locale.ENGLISH);
                        break;
                    case "time":
                        format = new SimpleDateFormat("H:mm:ss", Locale.ENGLISH);
                        break;
                    case "hours":
                        format = new SimpleDateFormat("H", Locale.ENGLISH);
                        break;
                    case "minutes":
                        format = new SimpleDateFormat("m", Locale.ENGLISH);
                        break;
                    case "seconds":
                        format = new SimpleDateFormat("s", Locale.ENGLISH);
                        break;
                    default:
                }
                if (format != null) {
                    sendTextMessage(String.format("Information to %s: %s", userAndMessage[0], format.format(calendar.getTime())));
                }
            }
        }
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
