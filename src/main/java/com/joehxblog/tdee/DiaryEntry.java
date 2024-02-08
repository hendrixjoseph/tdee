package com.joehxblog.tdee;

import java.time.LocalDate;

public record DiaryEntry(LocalDate date, int food, int exercise, Double weight) {

    int net() {
        return food - exercise;
    }
}
