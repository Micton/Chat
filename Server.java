package com.javarush.test.level30.lesson15.big01;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    private static final int SERVER_STATIC_PORT = 1050;

    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> map : connectionMap.entrySet()) {
            try {
                map.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Error while sending message");
            }
        }
    }

    public static void main(String[] args) {
//        ConsoleHelper.writeMessage("Enter server port");
//        int port = ConsoleHelper.readInt();
        try (ServerSocket serverSocket = new ServerSocket(SERVER_STATIC_PORT)) {
            ConsoleHelper.writeMessage("Server started");
            while (true) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Socket error");
        }
    }

    private static class Handler extends Thread {

        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            SocketAddress address = socket.getRemoteSocketAddress();
            ConsoleHelper.writeMessage("New connection established with remote address: " + address);
            String newClientName = null;
            try (Connection connection = new Connection(socket)) {
                ConsoleHelper.writeMessage("Connection to port: " + connection.getRemoteSocketAddress());
                newClientName = serverHandshake(connection);
                connectionMap.put(newClientName, connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, newClientName));
                sendListOfUsers(connection, newClientName);
                serverMainLoop(connection, newClientName);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error while data transfer with remote address");
            }
            Message message = new Message(MessageType.USER_REMOVED, newClientName);
            sendBroadcastMessage(message);
            try {
                connectionMap.remove(newClientName);
            } catch (NullPointerException ignored) {
            }
            ConsoleHelper.writeMessage("Connection with remote address: " + address + " closed");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {

            while (true) {
                Message requestMessage = new Message(MessageType.NAME_REQUEST);
                connection.send(requestMessage);
                Message receivedMessage = connection.receive();
                if (receivedMessage.getType() == MessageType.USER_NAME) {
                    String userName = receivedMessage.getData();
                    if (userName != null && !userName.isEmpty() && connectionMap.get(receivedMessage.getData()) == null) {
                        connectionMap.put(userName, connection);
                        Message successfulValidation = new Message(MessageType.NAME_ACCEPTED);
                        connection.send(successfulValidation);
                        return userName;
                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {

            for (Map.Entry<String, Connection> map : connectionMap.entrySet()) {
                Message message = new Message(MessageType.USER_ADDED, map.getKey());
                if (!map.getKey().equals(userName)) {
                    connection.send(message);
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    Message forSendToAll = new Message(MessageType.TEXT, String.format("%s: %s", userName, message.getData()));
                    sendBroadcastMessage(forSendToAll);
                } else ConsoleHelper.writeMessage("Error message type");
            }
        }
    }
}
