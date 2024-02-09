package com.joehxblog.tdee;

import java.time.LocalDate;

public record EnergyExpenditure(LocalDate date, int numberOfDays, int net, int base, double conversion, double changeInWeight) {

    int dailyEnergyExpenditure() {
        return base / numberOfDays;
    }
}
