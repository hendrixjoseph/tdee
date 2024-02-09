package com.joehxblog.tdee;

import java.util.ArrayList;
import java.util.List;

public class WeightChangeEntryBuilder {

    public List<WeightChangeEntry> with(List<DiaryEntry> diaryEntries) {
        List<WeightChangeEntry> tdeeEntries = new ArrayList<>();
        List<DiaryEntry> currentDiaryEntries = new ArrayList<>();

        diaryEntries.stream()
                .sorted()
                .dropWhile(diaryEntry -> diaryEntry.weight() == null)
                .forEachOrdered(diaryEntry -> {
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

    private WeightChangeEntry create(List<DiaryEntry> diaryEntries) {
        var date = diaryEntries.getLast().date();
        int numberOfDays = diaryEntries.size() - 1;
        int net = diaryEntries.stream()
                .limit(numberOfDays)
                .mapToInt(DiaryEntry::net)
                .sum();
        double changeInWeight = diaryEntries.getFirst().weight() - diaryEntries.getLast().weight();

        return new WeightChangeEntry(date, numberOfDays, net, changeInWeight);
    }
}
