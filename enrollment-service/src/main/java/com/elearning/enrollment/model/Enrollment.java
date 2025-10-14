package com.elearning.enrollment.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "enrollments")
@CompoundIndex(name = "student_course_idx", def = "{'studentEmail': 1, 'courseId': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String studentEmail;
    private String studentFirstName;
    private String studentLastName;
    private String courseId;
    private String courseTitle;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
    private Integer progressPercentage;

    private LocalDateTime lastAccessDate;
    private LocalDateTime completionDate;

}
