package com.elearning.progress.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVideoProgressRequest {

    @NotBlank(message = "Enrollment ID is a must")
    private String enrollmentId;

    @NotNull(message = "Module index is required")
    @Min(value = 0, message = "Module index must be >= 0")
    private Integer moduleIndex;

    @NotNull(message = "Lesson index is required")
    @Min(value = 0, message = "Lesson index must be >= 0")
    private Integer lessonIndex;

    // TODO find a way to fetch auto seconds from videos

    @NotNull(message = "Watched seconds is required")
    @Min(value = 0, message = "Watched seconds must be >= 0")
    private Integer watchedSeconds;

}
