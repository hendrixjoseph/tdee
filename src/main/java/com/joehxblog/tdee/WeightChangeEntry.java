package com.joehxblog.tdee;

import java.time.LocalDate;

public record WeightChangeEntry(LocalDate date, int numberOfDays, int net, double changeInWeight) {

    double base(double conversion) {
        return net - changeInWeight / conversion;
    }

    double conversion(double base) {
        return changeInWeight / (base - net);
    }

    double base(WeightChangeEntry other) {
        return (changeInWeight * other.net - other.changeInWeight - net)
                /
                (changeInWeight - other.changeInWeight);
    }

    double conversion(WeightChangeEntry other) {
        return (changeInWeight - other.changeInWeight) / (net - other.net);
    }
}
