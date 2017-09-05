package com.uabart.todolist.entity;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Options {

    private static Options instance;
    private Configuration config;
    private Property UI_show_completed_tasks;
    private Property UI_max_tasks_on_screen;
    private Property UI_max_categories_on_screen;

    public static Options getInstance() {
        return instance;
    }

    public static Options load(FMLPreInitializationEvent event) {
        instance = new Options();
        instance.config = new Configuration(new File(event.getModConfigurationDirectory(), "Todo mod.cfg"));
        instance.config.load();

        instance.config.addCustomCategoryComment("UI", "UI definitions");
        instance.UI_show_completed_tasks = instance.config.get("UI", "showCompletedTasks", true, "If tasks that are already completed should be shown or not");
        instance.UI_max_tasks_on_screen = instance.config.get("UI", "maximumTasksOnScreen", 5, "How many tasks should be displayed on the category screen");
        instance.UI_max_categories_on_screen = instance.config.get("UI", "maximumCategoriesOnScreen", 6, "How many categories should be displayed on the main screen");

        instance.config.save();

        return instance;
    }

    public boolean showCompletedTasks() {
        return UI_show_completed_tasks.getBoolean(false);
    }

    public int getMaxTasksOnScreen() {
        return UI_max_tasks_on_screen.getInt(7);
    }

    public int getMaxCategoriesOnScreen() {
        return UI_max_categories_on_screen.getInt(8);
    }

}
