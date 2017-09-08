package com.uabart.todolist.entity;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Options {

    private static final boolean DEFAULT_VALUE_COMPLETED_TASKS = true;
    private static final boolean DEFAULT_VALUE_SHOW_CATEGORIES = false;
    private static final int DEFAULT_VALUE_MAX_TASKS_ON_SCREEN = 7;
    private static final int DEFAULT_VALUE_MAX_CATEGORIES = 8;
    private static Options instance;
    private Configuration config;
    private Property UI_show_completed_tasks;
    private Property UI_max_tasks_on_screen;
    private Property UI_max_categories_on_screen;
    private Property UI_show_categories;

    public static Options getInstance() {
        return instance;
    }

    public static Options load(FMLPreInitializationEvent event) {
        instance = new Options();
        instance.config = new Configuration(new File(event.getModConfigurationDirectory(), "ToDoList.cfg"));
        instance.config.load();

        instance.config.addCustomCategoryComment("UI", "UI definitions");
        instance.UI_show_completed_tasks = instance.config.get("UI", "showCompletedTasks", DEFAULT_VALUE_COMPLETED_TASKS, "If tasks that are already completed should be shown or not");
        instance.UI_max_tasks_on_screen = instance.config.get("UI", "maximumTasksOnScreen", DEFAULT_VALUE_MAX_TASKS_ON_SCREEN, "How many tasks should be displayed on the category screen");
        instance.UI_max_categories_on_screen = instance.config.get("UI", "maximumCategoriesOnScreen", DEFAULT_VALUE_MAX_CATEGORIES, "How many categories should be displayed on the main screen");
        instance.UI_show_categories = instance.config.get("UI", "showCategories", DEFAULT_VALUE_SHOW_CATEGORIES, "Should I show categories on just use Any category all the time");

        instance.config.save();

        return instance;
    }

    public boolean showCompletedTasks() {
        return UI_show_completed_tasks.getBoolean(DEFAULT_VALUE_COMPLETED_TASKS);
    }

    public boolean showCategories() {
        return UI_show_categories.getBoolean(DEFAULT_VALUE_SHOW_CATEGORIES);
    }

    public int getMaxTasksOnScreen() {
        return UI_max_tasks_on_screen.getInt(DEFAULT_VALUE_MAX_TASKS_ON_SCREEN);
    }

    public int getMaxCategoriesOnScreen() {
        return UI_max_categories_on_screen.getInt(DEFAULT_VALUE_MAX_CATEGORIES);
    }

}
