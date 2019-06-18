package com.uabart.todolist.gui.components;

import com.uabart.todolist.entity.Task;
import com.uabart.todolist.gui.GuiListener;
import com.uabart.todolist.gui.GuiMessage;

public class DeleteGuiButton extends GuiButton {

    private final GuiListener listener;
    private final Task task;
    private final boolean isSubTask;
    private boolean isDeleting;

    public DeleteGuiButton(final GuiListener listener, final Task task, final boolean isSubTask) {
        super("x");
        this.listener = listener;
        this.task = task;
        this.isSubTask = isSubTask;
    }

    @Override
    public boolean onButtonPress(final boolean click) {
        if (isDeleting) {
            listener.update(GuiMessage.DELETE, task);
        } else {
            this.label = "?";
            isDeleting = true;
        }
        return true;
    }

    @Override
    public String getButtonTip() {
        return isDeleting ? "Click again if you want to delete" :
            isSubTask ? "Delete sub-task" : "Delete task";
    }
}
