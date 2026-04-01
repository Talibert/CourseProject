package com.example.api_docker.domain.course;

import com.example.api_docker.domain.course.exception.DuplicateLessonOrderException;

import java.util.ArrayList;
import java.util.List;

// domain/course/Module.java
public class Module {

    private final ModuleId id;
    private final String title;
    private final int order;
    private final List<Lesson> lessons;

    public Module(String title, int order) {
        this.id = ModuleId.generate();
        this.title = title;
        this.order = order;
        this.lessons = new ArrayList<>();
    }

    public void addLesson(Lesson lesson) {
        boolean orderExists = lessons.stream()
                .anyMatch(l -> l.order() == lesson.order());
        if (orderExists) {
            throw new DuplicateLessonOrderException(lesson.order());
        }
        lessons.add(lesson);
    }

    public ModuleId id()           { return id; }
    public String title()          { return title; }
    public int order()             { return order; }
    public List<Lesson> lessons()  { return List.copyOf(lessons); }
}
