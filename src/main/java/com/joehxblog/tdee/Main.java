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

        // Create a CsvToBeanBuilder object
        var csvToBeanBuilder = new CsvToBeanBuilder<DiaryEntry>(csvReader)
                .withType(DiaryEntry.class)
                .withMappingStrategy(new RecordMappingStrategy<>(DiaryEntry.class));

        // Parse the CSV file and create a list of records
        var records = csvToBeanBuilder.build().parse();

        // Close the CSV reader
        csvReader.close();
    }
}
