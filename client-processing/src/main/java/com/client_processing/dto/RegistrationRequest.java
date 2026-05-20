package com.client_processing.dto;

import com.client_processing.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    @NotBlank
    @Pattern(regexp = "\\d{12}")
    private String clientCode;
    @NotBlank
    private String login;
    @NotBlank
    @Size(min = 8, max = 255)
    private String password;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String firstName;
    private String middleName;
    @NotBlank
    private String lastName;
    @NotNull
    @Past
    private LocalDate dateOfBirth;
    @NotNull
    private DocumentType documentType;
    @NotBlank
    private String documentId;
    private String documentPrefix;
    private String documentSuffix;
}
