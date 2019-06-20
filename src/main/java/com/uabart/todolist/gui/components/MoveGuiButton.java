package com.uabart.todolist.gui.components;

import com.uabart.todolist.entity.Task;
import com.uabart.todolist.gui.GuiListener;
import com.uabart.todolist.gui.GuiMessage;

public class MoveGuiButton extends GuiButton {

    private final GuiListener listener;
    private final Task task;
    private final boolean isMovingUp;

    public MoveGuiButton(final GuiListener listener, final Task task, final boolean isMovingUp) {
        super();
        if (isMovingUp) {
            this.label = "↑";
        } else {
            this.label = "↓";
        }
        this.listener = listener;
        this.task = task;
        this.isMovingUp = isMovingUp;
    }

    @Override
    public boolean onButtonPress(final boolean click) {
        if (isMovingUp) {
            listener.update(GuiMessage.MOVE_UP, task);
        } else {
            listener.update(GuiMessage.MOVE_DOWN, task);
        }
        return false;
    }
}
