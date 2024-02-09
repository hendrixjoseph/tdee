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

        var energyExpenditures = new EnergyExpenditureBuilder().with(weightChangeEntries);

        var list = energyExpenditures.stream()
                .map(e -> {
                    var date = Converters.get(LocalDate.class).toString(e.date());
                    var tdee = Integer.toString(e.dailyEnergyExpenditure());

                    return new String[] {date, tdee};
                })
                .toList();

        var fileWriter = new FileWriter("tdee.csv");
        var csvWriter = new CSVWriterBuilder(fileWriter).build();
        csvWriter.writeAll(list);
        csvWriter.close();
    }
}
