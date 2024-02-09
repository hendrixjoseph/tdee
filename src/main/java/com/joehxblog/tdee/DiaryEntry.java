package com.joehxblog.tdee;

import java.time.LocalDate;

public record DiaryEntry(LocalDate date, int food, int exercise, Double weight) implements Comparable<DiaryEntry> {

    int net() {
        return food - exercise;
    }

    @Override
    public int compareTo(DiaryEntry o) {
        return date.compareTo(o.date);
    }
}
