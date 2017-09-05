package com.uabart.todolist.gui.components;

import codechicken.nei.Button;

public abstract class MyButton extends Button {

    public MyButton(String s) {
        super(s);
    }

    public MyButton() {
        super();
    }

    @Override
    public boolean handleClick(int i, int j, int k) {
        if (state == 2)
            return false;
        return super.handleClick(i, j, k);
    }

}
