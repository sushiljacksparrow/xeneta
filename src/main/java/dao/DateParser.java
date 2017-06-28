package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hello.Application;

public class DateParser {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
    
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    
    public static Date toDate(String dateString) {
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            log.error(String.format("Could not parse date %s", dateString));
            throw new RuntimeException();
        }
    }
}
