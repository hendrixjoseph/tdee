package com.joehxblog.tdee;

import java.util.ArrayList;
import java.util.List;

public class TdeeEntryBuilder {

    public List<TdeeEntry> with(List<DiaryEntry> diaryEntries) {
        List<TdeeEntry> tdeeEntries = new ArrayList<>();
        List<DiaryEntry> currentDiaryEntries = new ArrayList<>();

        diaryEntries.stream()
                .sorted()
                .dropWhile(diaryEntry -> diaryEntry.weight() == null)
                .forEach(diaryEntry -> {
            if (currentDiaryEntries.isEmpty() || diaryEntry.weight() == null) {
                currentDiaryEntries.add(diaryEntry);
            } else {
                currentDiaryEntries.add(diaryEntry);
                var newTdeeEntry = create(currentDiaryEntries);
                tdeeEntries.add(newTdeeEntry);
                currentDiaryEntries.clear();
                currentDiaryEntries.add(diaryEntry);
            }
        });

        return tdeeEntries;
    }

    private TdeeEntry create(List<DiaryEntry> diaryEntries) {
        int net = diaryEntries.stream()
                .limit(diaryEntries.size() - 1)
                .mapToInt(DiaryEntry::net)
                .sum();
        double changeInWeight = diaryEntries.getFirst().weight() - diaryEntries.getLast().weight();

        return new TdeeEntry(diaryEntries.getLast().date(), net, changeInWeight);
    }
}
