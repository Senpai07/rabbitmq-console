package ru.geekbarins.rabbitmq.console;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Subscriber {
    public static final String QUEUE_NAME = "processingBlogQueue";
    public static final String EXCHANGE_NAME = "processingBlogExchanger";

    public static void main(String[] args) throws Exception {

        if (args.length == 2) {
            String arg = args[0];
            String theme = "";

            if (arg.equals("/t")) theme = args[1];

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            try {
                channel.queueDeclarePassive(QUEUE_NAME);
            } catch (IOException ex) {
                channel = connection.createChannel();
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            }

            System.out.println(" [*] Waiting for publication by theme: " + theme);

            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, theme);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received publication '" + message + "'");

//            finalChannel.basicPublish(EXCHANGE_NAME, THEME, null, "Publication ready".getBytes());
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        } else {
            System.out.println("Add command parameter /t + ' ' + name theme");
        }
    }
}
