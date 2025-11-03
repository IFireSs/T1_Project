package com.client_processing.dto;

import com.client_processing.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto {
    private String clientId;
    private String firstName;
    private String middleName;
    private String lastName;
    private DocumentType documentType;
    private String documentId;
    private String documentPrefix;
    private String documentSuffix;
}
