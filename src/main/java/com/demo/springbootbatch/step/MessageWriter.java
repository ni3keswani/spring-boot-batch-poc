package com.demo.springbootbatch.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ItemWriter
 */
@Component
public class MessageWriter implements ItemWriter<String> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * ItemWriter writes received data to destination.
     *
     * @param inputMessage
     * @throws Exception
     */
    @Override
    public void write(List<? extends String> inputMessage) throws Exception {
        //write data to console
        for (String outputMsg : inputMessage) {
            System.out.println(Thread.currentThread().getName() + " ---- Received input data from Step:- " + outputMsg);
        }
    }

}