package com.mavi.projectmanager.service.utils;

import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.model.Project;

import java.time.LocalDate;

public class DateUtils {

    public static void validateDates(LocalDate start_date, LocalDate end_date) {

        LocalDate today = LocalDate.now();

        if (start_date.isAfter(end_date)) {
            throw new InvalidDateException("Start date cannot be after end date!", 1);
        }

        if (!end_date.isAfter(today)) {
            throw new InvalidDateException("End date cannot be in the past!", 2);
        }
    }
}
