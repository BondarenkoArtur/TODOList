package com.uabart.todolist.gui;

import com.uabart.todolist.entity.Task;

public interface GuiListener {

    void update(GuiMessage message, Task task);

}
