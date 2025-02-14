package org.polymath.noteapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotNull @NotBlank(message = "Email cannot be blanks") @Email(message = "Input a correct email") String email,@NotNull @NotBlank(message = "Password cannot be blank") @Size(min = 6,message = "Password should have at least 6 characters") String password) {
}
