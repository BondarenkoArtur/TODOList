package com.uabart.todolist.gui.components;

import com.uabart.todolist.entity.Task;
import com.uabart.todolist.gui.GuiListener;
import com.uabart.todolist.gui.GuiMessage;

public class DeleteGuiButton extends GuiButton {

    private final GuiListener listener;
    private final Task task;
    private final Type type;
    private boolean isDeleting;

    public DeleteGuiButton(final GuiListener listener, final Task task, final Type type) {
        super("x");
        this.listener = listener;
        this.task = task;
        this.type = type;
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
        return isDeleting ? "Click again if you want to delete" : getMessageByType();
    }

    private String getMessageByType() {
        final String msg;
        switch (type) {
            case TASK:
                msg = "Delete task";
                break;
            case SUBTASK:
                msg = "Delete sub-task";
                break;
            case CATEGORY:
                msg = "Delete category";
                break;
            default:
                msg = "Delete";
                break;
        }
        return msg;
    }

    public enum Type {
        CATEGORY,
        TASK,
        SUBTASK
    }
}
