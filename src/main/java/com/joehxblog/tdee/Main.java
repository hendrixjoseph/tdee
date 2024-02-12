package com.joehxblog.tdee;

import com.joehxblog.converter.Converters;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String... args) throws URISyntaxException, IOException {
        var resource = Main.class.getResource("/calories.csv").toURI();
        var path = Path.of(resource);

        var fileReader = new FileReader(path.toFile());
        var csvReader = new CSVReader(fileReader);

        var csvToBeanBuilder = new CsvToBeanBuilder<DiaryEntry>(csvReader)
                .withType(DiaryEntry.class)
                .withMappingStrategy(new RecordMappingStrategy<>(DiaryEntry.class));

        var diaryEntries = csvToBeanBuilder.build().parse();

        csvReader.close();

        var weightChangeEntries = new WeightChangeEntryBuilder().with(diaryEntries);

        writeWeightChangeEntries(weightChangeEntries);
        writeEnergyExpenditures(weightChangeEntries);
        compareGainVsLoss(weightChangeEntries);
        byDayOfWeek(weightChangeEntries);
    }

    private static void writeWeightChangeEntries(List<WeightChangeEntry> weightChangeEntries) throws IOException {
        var list = weightChangeEntries.stream().map(w -> {
            var date = Converters.get(LocalDate.class).toString(w.date());
            var dailyNet = Integer.toString(w.dailyNet());
            var changeInWeight = Double.toString(w.changeInWeight());

            return new String[] {date, dailyNet, changeInWeight};
        })
        .toList();

        write(list, "changeInWeight.csv");
    }

    private static void byDayOfWeek(List<WeightChangeEntry> weightChangeEntries) throws IOException {
        var dayOfWeekMap = weightChangeEntries.stream().collect(
                Collectors.groupingBy(
                    w -> w.date().getDayOfWeek(),
                    Collectors.partitioningBy(w -> w.changeInWeight() > 0)
                )
        );

        var list = dayOfWeekMap.entrySet().stream().map(e -> {
            var dayOfWeek = e.getKey().toString();
            var gainCount = Integer.toString(e.getValue().get(true).size());
            var lossCount = Integer.toString(e.getValue().get(false).size());

            return new String[] {dayOfWeek, gainCount, lossCount};
        })
        .toList();

        write(list, "byDayOfWeek.csv");
    }

    private static Map<Boolean, List<Integer>> groupWeightChangeEntries(List<WeightChangeEntry> weightChangeEntries) {
        return weightChangeEntries.stream().collect(
                Collectors.partitioningBy(
                        e -> e.changeInWeight() > 0,
                        Collectors.mapping(WeightChangeEntry::dailyNet, Collectors.toList())
                )
        );
    }

    private static void compareGainVsLoss(List<WeightChangeEntry> weightChangeEntries) throws IOException {
        var groupings = groupWeightChangeEntries(weightChangeEntries);

        var gainedWeight = groupings.get(true);
        var lostWeight = groupings.get(false);
        var size = Math.max(gainedWeight.size(), lostWeight.size());

        var list = IntStream.range(0, size).mapToObj(i -> {
                    var gain = getValue(gainedWeight, i);
                    var lost = getValue(lostWeight, i);

                    return new String[] {gain, lost};
                })
                .toList();

        write(list, "gainVsLoss.csv");
    }

    private static String getValue(List<Integer> list, int index) {
        if (list.size() >  index) {
            return Integer.toString(list.get(index));
        } else {
            return "";
        }
    }

    private static void writeEnergyExpenditures(List<WeightChangeEntry> weightChangeEntries) throws IOException {
        var energyExpenditures = new EnergyExpenditureBuilder().with(weightChangeEntries);

        var list = energyExpenditures.stream()
                .map(e -> {
                    var date = Converters.get(LocalDate.class).toString(e.date());
                    var tdee = Integer.toString(e.dailyEnergyExpenditure());

                    return new String[] {date, tdee};
                })
                .toList();

        write(list, "tdee.csv");
    }

    private static void write(List<String[]> list, String filename) throws IOException {
        var fileWriter = new FileWriter(filename);
        var csvWriter = new CSVWriterBuilder(fileWriter).build();
        csvWriter.writeAll(list);
        csvWriter.close();
    }
}
