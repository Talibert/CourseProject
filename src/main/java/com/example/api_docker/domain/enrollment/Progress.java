package com.example.api_docker.domain.enrollment;

import com.example.api_docker.domain.course.AssessmentId;
import com.example.api_docker.domain.course.CourseStructure;
import com.example.api_docker.domain.course.LessonId;
import com.example.api_docker.domain.course.exception.LessonNotPartOfCourseException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record Progress(
        Set<LessonId> completedLessons,
        Map<AssessmentId, BigDecimal> grades,
        int totalLessons
) {
    public static Progress zero(int totalLessons) {
        return new Progress(Set.of(), Map.of(), totalLessons);
    }

    public double percentage(CourseStructure structure) {
        if (totalLessons == 0) return 0.0;
        return (completedLessons.size() * 100.0) / totalLessons;
    }

    public Progress withCompletedLesson(LessonId lessonId, CourseStructure structure) {
        if (!structure.contains(lessonId)) {
            throw new LessonNotPartOfCourseException(lessonId);
        }
        var updated = new HashSet<>(completedLessons);
        updated.add(lessonId);
        return new Progress(Set.copyOf(updated), grades, totalLessons);
    }

    public Progress withGrade(AssessmentId assessmentId, BigDecimal grade) {
        var updatedGrades = new HashMap<>(grades);
        updatedGrades.put(assessmentId, grade);
        return new Progress(completedLessons, Map.copyOf(updatedGrades), totalLessons);
    }

    public boolean hasPassingGrade(BigDecimal minimumGrade) {
        if (grades.isEmpty()) return false;
        var avg = grades.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(grades.size()), RoundingMode.HALF_UP);
        return avg.compareTo(minimumGrade) >= 0;
    }
}
