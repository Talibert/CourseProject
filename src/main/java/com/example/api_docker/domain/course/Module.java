package com.example.api_docker.domain.course;

import com.example.api_docker.domain.course.exception.DuplicateLessonOrderException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Module {

    @Getter
    private final ModuleId id;
    @Getter
    private final String title;
    @Getter
    private final int order;

    private final List<Lesson> lessons;

    public Module(String title, int order) {
        this.id = ModuleId.generate();
        this.title = title;
        this.order = order;
        this.lessons = new ArrayList<>();
    }

    public void addLesson(Lesson lesson) {
        boolean orderExists = lessons.stream().anyMatch(l -> l.getOrder() == lesson.getOrder());

        if (orderExists)
            throw new DuplicateLessonOrderException(lesson.getOrder());

        lessons.add(lesson);
    }

    public ModuleStructure toStructure() {
        List<LessonId> lessonsIds = lessons.stream()
                .sorted(Comparator.comparingInt(Lesson::getOrder))
                .map(Lesson::getId)
                .toList();

        return ModuleStructure.of(this.id, this.title, lessonsIds);
    }

    public List<Lesson> getLessons(){
        return List.copyOf(lessons);
    }
}
