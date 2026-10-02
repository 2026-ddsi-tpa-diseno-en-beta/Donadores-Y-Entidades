package ar.edu.utn.dds.k3003.model;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
public enum PeriodoNecesidad {
  SEMANAL, MENSUAL;
  public LocalDate inicio(LocalDate fecha) {
    return this == MENSUAL ? fecha.withDayOfMonth(1) : fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
  }
}
