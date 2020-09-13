package net.fosforito.partido.database.impl;

import net.fosforito.partido.database.UTCDateService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class UTCDateServiceImpl implements UTCDateService {
    @Override
    public Date getConvertedUTCDate() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

        try {
            return dateFormatLocal.parse(dateFormatGmt.format(new Date()));
        } catch (ParseException e) {
            throw new RuntimeException("Could not return Date in UTC format");
        }
    }
}
