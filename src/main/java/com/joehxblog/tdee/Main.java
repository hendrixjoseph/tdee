package com.joehxblog.tdee;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

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

        weightChangeEntries.forEach(System.out::println);
    }
}
