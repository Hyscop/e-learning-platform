package com.elearning.enrollment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequest {

    @NotBlank(message = "Course ID is required")
    private String courseId;

    @NotNull(message = "Progress is required")
    @Min(value = 0, message = "Progress must be at least 0")
    @Max(value = 100, message = "Progress must be at most 100")
    private Integer progress;
}
