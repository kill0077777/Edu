package com.edu.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component("dateFormatUtils")
public class DateFormatUtils {
    public String format(LocalDateTime date, String pattern) {
        if (date == null) {
			return "";
		}
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
}
