package com.uabart.todolist.gui.components;

import com.uabart.todolist.entity.Task;
import com.uabart.todolist.logic.TaskListener;

import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import codechicken.nei.TextField;

import static codechicken.lib.gui.GuiDraw.drawString;
import static codechicken.lib.gui.GuiDraw.getStringWidth;

public class FieldMainName extends TextField implements TaskListener {

    private Task task;
    private boolean editable = true;
    private boolean updating = false;
    private boolean isBackgroundVisible = true;

    private static int COMPLETED_FOCUSED = 0xFF00EE00;
    private static int COMPLETED_UNFOCUSED = 0xFF009900;
    private static int NOT_COMPLETED_FOCUSED = 0xFFE0E0E0;
    private static int NOT_COMPLETED_UNFOCUSED = 0xFF909090;

    public FieldMainName(Task task) {
        super(task.toString());
        this.task = task;
        this.task.setListener(this);
        setText(task.getName());
    }

    @Override
    public FieldMainName clone() {
        FieldMainName cloneObject = new FieldMainName(this.task);
        cloneObject.x = this.x;
        cloneObject.y = this.y;
        cloneObject.w = this.w;
        cloneObject.h = this.h;
        return cloneObject;
    }

    @Override
    @Deprecated
    /**
     * Don't use this to check for modifications
     */
    public void onTextChange(String oldText) {
        if (updating) {
            // avoid looping in event firing
            return;
        }
        task.setName(this.text());
    }

    @Override
    public void draw(int mousex, int mousey) {
        if (isBackgroundVisible) {
            drawBox();
        }

        String drawtext = text();

        if (text().length() > getMaxTextLength()) {
            int startOffset = drawtext.length() - getMaxTextLength();
            if (startOffset < 0 || startOffset > drawtext.length())
                startOffset = 0;
            int move = task.getMove();
            drawtext = drawtext.substring(startOffset + move, getMaxTextLength() + move + startOffset);
            if (move != 0)
                drawtext += "..";
            if (move != -text().length() + getMaxTextLength())
                drawtext = ".." + drawtext;
        }

        if (focused() && (cursorCounter / 6) % 2 == 0)
            drawtext = drawtext + '_';

        int textWidth = getStringWidth(text());
        int textx = centered ? x + (w - textWidth) / 2 : x + 4;
        int texty = y + (h + 1) / 2 - 3;

        drawString(drawtext, textx, texty, getTextColour());

    }

    private int getMaxTextLength() {
        return w / 6 + 1;
    }

    @Override
    public void setFocus(boolean focus) {
        if (focus && !editable) {
            super.setFocus(false);
        } else
            super.setFocus(focus);
    }

    @Override
    public boolean handleKeyPress(int keyID, char keyChar) {
        if (!focused() || !editable)
            return false;
        boolean s = true;
        int move = task.getMove();
        if (Keyboard.KEY_LEFT == keyID)
            move -= 1;
        else if (Keyboard.KEY_RIGHT == keyID)
            move += 1;
        else {
            s = super.handleKeyPress(keyID, keyChar);
            move = 0;
        }

        if (move > 0)
            move = 0;
        else
            move = Math.max(move, -text().length() + getMaxTextLength());
        task.setMove(move);
        return s;
    }

    @Override
    public boolean onMouseWheel(int i, int mousex, int mousey) {
        if (!contains(mousex, mousey))
            return false;
        int move = task.getMove();
        move += i;
        if (move > 0)
            move = 0;
        else
            move = Math.max(move, -text().length() + getMaxTextLength());
        task.setMove(move);
        return true;
    }

    @Override
    public int getTextColour() {
        if (task.isCompleted())
            return focused() ? COMPLETED_FOCUSED : COMPLETED_UNFOCUSED;
        else
            return focused() ? NOT_COMPLETED_FOCUSED : NOT_COMPLETED_UNFOCUSED;
    }

    @Override
    public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
        if (!contains(mx, my) || focused())
            return tooltip;

        Pattern regex = Pattern.compile("(.{1,20}(?:\\s|$))|(.{0,20})", Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(text());
        while (regexMatcher.find()) {
            if (!regexMatcher.group().isEmpty())
                tooltip.add(regexMatcher.group());
        }

        return tooltip;
    }

    @Override
    public void update(Task task) {
        updating = true;
        setText(task.getName());
        updating = false;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setBackgroundVisible(boolean backgroundVisible) {
        isBackgroundVisible = backgroundVisible;
    }


    public Task getTask() {
        return task;
    }
}