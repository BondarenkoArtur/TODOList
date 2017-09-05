package com.uabart.todolist.entity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskHolder {

    private List<Category> categories;

    public TaskHolder() {
    }

    public void init() {
        // we need to re-assign in this method in case JAXB replaces with a null
        // instance

        if (categories == null || categories.isEmpty()) {
            categories = new ArrayList<Category>();
            categories.add(new Category.Any(this));
        } else
            for (Category each : categories) {
                if (each instanceof Category.Any)
                    ((Category.Any) each).setHolder(this);

                for (Task task : each.listSubtasks())
                    loadTask(task);
            }
    }

    private void loadTask(Task task) {
        if (task.getItemID() > 0) {
            ItemStack stack = new ItemStack(Item.getItemById(task.getItemID()));
            stack.setItemDamage(task.getItemDamage());
            task.setReference(stack);
        }
        for (Task sub : task.listSubtasks())
            loadTask(sub);
    }

    @XmlElement
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
