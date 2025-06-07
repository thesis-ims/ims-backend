package com.backend.ims.general.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImportCsvRequest {
  private ImportType importType; // Append or Replace
  private byte[] csvData;
}
