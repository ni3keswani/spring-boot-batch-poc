package com.demo.springbootbatch.step;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * ItemProcessor
 */
@Component
public class MessageProcessor implements ItemProcessor<String, String> {

    /**
     * Read input data from itemReader, and then ItemProcessor applies the business logic here
     *
     * @param content
     * @return String
     * @throws Exception
     */
    @Override
    public String process(String content) throws Exception {
    	Thread.sleep(20000);
        System.out.println(Thread.currentThread().getName() + " - processor");
        return "TEST - [" + content.toUpperCase() + "]";
    }

}