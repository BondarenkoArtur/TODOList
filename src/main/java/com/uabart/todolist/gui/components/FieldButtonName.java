package com.uabart.todolist.gui.components;

import com.uabart.todolist.entity.Task;
import com.uabart.todolist.logic.TaskListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FieldButtonName extends GuiButton implements TaskListener {

    private Task task;
    private String realLabel;

    public FieldButtonName(Task task, int height, int width) {
        this.task = task;
        this.task.setListener(this);
        this.label = task.getName();
        this.realLabel = this.label;
        this.h = height;
        this.w = width;
        update(task);
    }

    @Override
    public void update(Task task) {
        this.realLabel = task.getName();
        this.label = realLabel;

        int textwidth = contentWidth();
        int maxLength = w / 6 + 1;
        if (label.length() > maxLength)
            label = realLabel.substring(0, maxLength).concat("..");
        if (task.isCompleted())
            state = 2;
        else
            state = 0;
    }

    @Override
    public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
        if (!contains(mx, my))
            return tooltip;

        Pattern regex = Pattern.compile("(.{1,20}(?:\\s|$))|(.{0,20})", Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(realLabel);
        while (regexMatcher.find()) {
            if (!regexMatcher.group().isEmpty())
                tooltip.add(regexMatcher.group());
        }

        return tooltip;
    }

    public Task getTask() {
        return task;
    }
}