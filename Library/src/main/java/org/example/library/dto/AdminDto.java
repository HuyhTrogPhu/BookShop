package org.example.library.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminDto {

    @Size(min = 3, max = 10, message = "Invalid first name! (3-10 character)")
    private String firstName;

    @Size(min = 3, max = 10, message = "Invalid first name! (3-10 character")
    private String lastName;

    private String userName;

    @Size(min = 5, max = 15, message = "Invalid password! (5-15 character)")
    private String password;

    private String repeatPassword;

}
