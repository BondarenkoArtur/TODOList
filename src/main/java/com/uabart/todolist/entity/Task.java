package com.uabart.todolist.entity;

import com.uabart.todolist.logic.TaskListener;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso({Category.class, Category.Any.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class Task {

    @XmlElement
    protected List<Task> subtasks;
    @XmlElement
    protected String tagCompound;
    @XmlElement
    private Boolean isSelected;
    private String name;
    private int moveSymbols;
    private int priority;
    private boolean completed;
    private int itemID, itemDamage;

    @XmlTransient
    private ItemStack reference;
    @XmlTransient
    private List<TaskListener> listeners;
    @XmlTransient
    private List<Task> immutable;

    public Task() {
        this.name = "";
        this.priority = 0;
        this.moveSymbols = 0;
        this.completed = false;
        this.reference = null;
        this.subtasks = new ArrayList<Task>();
        this.listeners = new ArrayList<TaskListener>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateListener();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
        updateListener();
    }

    public Boolean isSelected() {
        return isSelected;
    }

    public void setSelected(final Boolean selected) {
        isSelected = selected;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        updateListener();
    }

    @XmlTransient
    public ItemStack getReference() {
        return reference;
    }

    public void createReference(ItemStack reference) {
        setReference(reference);
    }

    public void loadReference(ItemStack reference) {
        setReference(reference);
    }

    public void setReference(ItemStack reference) {
        this.reference = reference;
        this.itemID = Item.getIdFromItem(reference.getItem());
        this.itemDamage = reference.getItemDamage();
        if (reference.getTagCompound() != null) {
            this.tagCompound = reference.getTagCompound().toString();
        }

        updateListener();
    }

    public void moveTask(final Task toMove, final boolean isMovingUp) {
        if (subtasks.size() > 1) {
            final int moving = isMovingUp ? -1 : 1;
            final int indexFrom = subtasks.indexOf(toMove);
            final int indexTo = indexFrom + moving;
            if (indexTo >= 0 && indexTo < subtasks.size()) {
                Collections.swap(subtasks, indexFrom, indexTo);
            }
        }
    }

    public void addTask(Task toAdd) {
        subtasks.add(toAdd);
    }

    public boolean removeTask(Task toRemove) {
        return subtasks.remove(toRemove);
    }

    /**
     * Do not try to edit this collection, as it's immutable.
     *
     * @return
     */
    public List<Task> listSubtasks() {
        if (immutable == null)
            immutable = Collections.unmodifiableList(subtasks);
        return immutable;
    }

    public void setListener(TaskListener listener) {
        this.listeners.add(listener);
    }

    public void updateListener() {
        for (TaskListener listener : listeners)
            listener.update(this);
    }

    int getItemID() {
        return itemID;
    }

    int getItemDamage() {
        return itemDamage;
    }

    NBTTagCompound getTagCompound() {
        NBTTagCompound nbt = null;
        if (tagCompound != null) {
            try {
                nbt = (NBTTagCompound) JsonToNBT.func_150315_a(tagCompound);
            } catch (NBTException e) {
                e.printStackTrace();
            }
        }
        return nbt;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setItemDamage(int itemDamage) {
        this.itemDamage = itemDamage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj == this;
    }

    public void setMove(int move) {
        this.moveSymbols = move;
    }

    public int getMove() {
        return moveSymbols;
    }
}
