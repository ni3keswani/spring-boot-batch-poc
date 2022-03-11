package com.demo.springbootbatch.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ItemReader
 */
@Component
public class MessageReader implements ItemReader<String> {

    @Value(("${spring.application.name}"))
    private String appName;

    private String[] welcomeMessage = {"Hello World!", "Welcome to Spring Batch Example!"};

    private int msgIndex = 0;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * It read the data from the given source
     *
     * @return String
     * @throws Exception
     */
    @Override
    public String read() throws Exception {

        //read and pass message to processor to process the message
        if (msgIndex < welcomeMessage.length) {
            //welcomeMessage[0],welcomeMessage[1]
            System.out.println(Thread.currentThread().getName() + " - reader");
            return appName+" - "+welcomeMessage[msgIndex++];
        } else {
            msgIndex = 0;
        }
        return null;
    }

}