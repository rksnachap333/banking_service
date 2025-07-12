//package org.example.kafka.consumer;
//
//import org.example.kafka.service.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaConsumerService {
//
//    @Autowired
//    private EmailService emailService;
//
//    @KafkaListener(topics = "${kafka.topic.name:email-topic}", groupId = "${spring.kafka.consumer.group-id}")
//    public void listen(String message) {
//        System.out.println("Message received: " + message);
//        emailService.sendEmail("rksnachap333@gmail.com", "Bank Transaction", "Deposit Done !!");
//    }
//}
