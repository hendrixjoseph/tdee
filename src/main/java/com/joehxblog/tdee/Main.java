package com.joehxblog.tdee;

import com.joehxblog.converter.Converters;
import com.joehxblog.converter.LocalDateConverter;
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



    }

    private static void compareGainVsLoss(List<WeightChangeEntry> weightChangeEntries) throws IOException {
        var groupings = weightChangeEntries.stream().collect(
                Collectors.partitioningBy(
                        e -> e.changeInWeight() > 0,
                        Collectors.mapping(WeightChangeEntry::dailyNet, Collectors.toList())
                )
        );

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

    private static void writeEnergyExpenditures(List<EnergyExpenditure> energyExpenditures) throws IOException {
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
