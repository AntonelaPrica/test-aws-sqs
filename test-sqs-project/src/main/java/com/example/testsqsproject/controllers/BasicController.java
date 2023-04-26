package com.example.testsqsproject.controllers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

@Controller
public class BasicController {

    @PostMapping("/sqs/send")
    @ResponseBody
    public void sendMessageToSQSQueue(@RequestBody String text) {

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

        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_NORTH_1)
                .build();

        String queueUrl = "https://sqs.eu-north-1.amazonaws.com/164108796864/TestQueue";

        sqs.sendMessage(new SendMessageRequest(queueUrl, text));

    }
}
