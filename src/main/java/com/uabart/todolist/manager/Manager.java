package com.uabart.todolist.manager;

import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.gui.Layout;
import com.uabart.todolist.handler.KeyStateTracker;
import com.uabart.todolist.logic.Logic;

public final class Manager {

    private static Layout layout;
    private static TaskHolder holder;
    private static Logic logic;

    private Manager() {
    }

    public static void init() {
        getHolder().init();
        getLayout().init(getHolder());
        getLogic().init(getLayout(), getHolder());
        KeyStateTracker.load();
    }

    public static TaskHolder getHolder() {
        if (holder == null) {
            holder = new TaskHolder();
        }
        return holder;
    }

    public static Layout getLayout() {
        if (layout == null) {
            layout = new Layout();
        }
        return layout;
    }

    public static Logic getLogic() {
        if (logic == null) {
            logic = new Logic();
        }
        return logic;
    }
}
