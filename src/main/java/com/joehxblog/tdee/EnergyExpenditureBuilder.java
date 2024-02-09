package com.joehxblog.tdee;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EnergyExpenditureBuilder {

    public List<EnergyExpenditure> with(List<WeightChangeEntry> weightChangeEntries) {
        List<EnergyExpenditure> energyExpenditures = new ArrayList<>();

        var previous = new AtomicReference<WeightChangeEntry>();

        weightChangeEntries.stream()
                .sorted()
                .forEachOrdered(current -> {
            if (previous.get() != null) {
                var energyExpenditure = create(previous.get(), current);
                energyExpenditures.add(energyExpenditure);
            }

            previous.set(current);
        });

        return energyExpenditures;
    }

    private EnergyExpenditure create(WeightChangeEntry previous, WeightChangeEntry current) {
        var date = current.date();
        var numberOfDays = current.numberOfDays();
        var net = current.net();
        var base = current.base(previous);
        var conversion = current.conversion(base);
        var changeInWeight = current.changeInWeight();

        return new EnergyExpenditure(date, numberOfDays, net, (int) base, conversion, changeInWeight);
    }
}
