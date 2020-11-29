package ru.geekbarins.rabbitmq.console;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ItBlog {
    //    public static final String QUEUE_NAME = "processingBlogQueue";
    public static final String EXCHANGE_NAME = "processingBlogExchanger";


    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Scanner in = new Scanner(System.in);
        String command;
        String theme;

        System.out.print("Введите команду ('/q' - выход): ");

        while (in.hasNextLine()) {
            command = in.nextLine();
            if (command.equals("/q")) {
                break;
            } else {
                String[] parts = command.split(" ");
                switch (parts[0]) {
                    case "/h":
                        System.out.println("/h - список команд");
                        System.out.println("/t - тема");
                        System.out.println("/q - выход");
                        break;
                    case "/t":
                        System.out.println("theme: " + parts[1]);
                        theme = parts[1];
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        // channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                        try {
                            try {
                                channel.exchangeDeclarePassive(EXCHANGE_NAME);
                            } catch (IOException ex) {
                                channel = connection.createChannel();
                                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                            }

                            String message = "info: " + theme;
                            Channel finalChannel = channel;
                            finalChannel.basicPublish(EXCHANGE_NAME, theme, null, message.getBytes(StandardCharsets.UTF_8));
                            System.out.println(" [x] Sent '" + message + "'");
                        } finally {
                            channel.close();
                            connection.close();
                        }
                        break;
                    default:
                        System.out.println("Неверная команда, список команд /h");
                }
            }
            System.out.print("Введите команду ('/quit' - выход): ");
        }


    }
}
