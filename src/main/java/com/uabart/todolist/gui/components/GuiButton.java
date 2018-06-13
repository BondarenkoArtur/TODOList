package com.uabart.todolist.gui.components;

import codechicken.nei.Button;

public abstract class GuiButton extends Button {

    public GuiButton(String s) {
        super(s);
    }

    public GuiButton(String s, Integer x, Integer y, Integer h, Integer w) {
        super(s);
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = this.contentWidth() + w;
    }

    public GuiButton() {
        super();
    }

    @Override
    public boolean handleClick(int i, int j, int k) {
        if (state == 2)
            return false;
        return super.handleClick(i, j, k);
    }

}
