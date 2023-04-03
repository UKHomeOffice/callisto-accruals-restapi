package uk.gov.homeoffice.digital.sas.accruals.enums;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccrualType {
  NIGHT_HOURS(UUID.fromString("5f06e6ce-1422-4a0c-89dd-f4952e735202")),
  DAY_HOURS(UUID.fromString("05bbd915-e907-4259-a2e2-080d7956afec"));

  private final UUID id;
}
