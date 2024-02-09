package com.joehxblog.tdee;

import java.time.LocalDate;

public record TdeeEntry(LocalDate date, int net, double changeInWeight) {

    double base(double conversion) {
        return net - changeInWeight / conversion;
    }

    double conversion(double base) {
        return changeInWeight / (base - net);
    }

    double base(TdeeEntry other) {
        return (changeInWeight * other.net - other.changeInWeight - net)
                /
                (changeInWeight - other.changeInWeight);
    }

    double conversion(TdeeEntry other) {
        return (changeInWeight - other.changeInWeight) / (net - other.net);
    }
}
