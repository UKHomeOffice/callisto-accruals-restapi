package uk.gov.homeoffice.digital.sas.accruals.testUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CommonUtils {

  private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public static String objectAsJsonString(final Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }
}
