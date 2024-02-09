package com.joehxblog.tdee;

import java.util.List;

public class TdeeEntryBuilder {

    public void with(List<DiaryEntry> diaryEntries) {
        diaryEntries.stream()
                .sorted()
                .dropWhile(diaryEntry -> diaryEntry.weight() == null)
                .forEach(diaryEntry -> {
            System.out.println(diaryEntry);
        });
    }
}
