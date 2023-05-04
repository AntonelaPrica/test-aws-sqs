package com.example.testsqsproject.controllers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class BasicController {

    String queueUrl = "https://sqs.eu-north-1.amazonaws.com/164108796864/TestQueue";

    private ProfileCredentialsProvider getCredentialsProvider(){
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        return credentialsProvider;
    }

    @PostMapping("/sqs/send")
    @ResponseBody
    public void sendMessageToSQSQueue(@RequestBody String text) {
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(this.getCredentialsProvider())
                .withRegion(Regions.EU_NORTH_1)
                .build();

        sqs.sendMessage(new SendMessageRequest(this.queueUrl, text));

    }

    @GetMapping("/sqs/poll")
    @ResponseBody
    public String pollMessagesFromSQSQueue(){
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(this.getCredentialsProvider())
                .withRegion(Regions.EU_NORTH_1)
                .build();

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(this.queueUrl)
                .withWaitTimeSeconds(10)
                .withMaxNumberOfMessages(1);

        List<Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();

        String messageBody = sqsMessages.get(0).getBody();

        sqs.deleteMessage(new DeleteMessageRequest()
                .withQueueUrl(this.queueUrl)
                .withReceiptHandle(sqsMessages.get(0).getReceiptHandle()));

        return messageBody;
    }
}
