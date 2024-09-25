package org.example.library.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.library.model.Caterogy;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookDto {

    private Long id;

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Author is required")
    private String author;

    @Size(min = 10, message = "Description should be at least 10 character")
    @Size(max = 1000, message = "Description cannot exceed 1000 character")
    private String description;

    @NotNull
    private double costPrice;

    @NotNull
    private double salePrice;

    @NotNull
    private int currentQuantity;

    private Caterogy category;

    private String image;

    private boolean is_active;

    private boolean is_delete;

}
