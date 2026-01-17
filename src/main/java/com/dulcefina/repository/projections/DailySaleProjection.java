package com.dulcefina.repository.projections;

import java.time.LocalDate;

public interface DailySaleProjection {
    LocalDate getDate();
    Double getTotal();
}