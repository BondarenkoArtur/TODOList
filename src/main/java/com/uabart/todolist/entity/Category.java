package com.uabart.todolist.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

public class Category extends Task {

    public List<Task> getActiveTasks() {
        ArrayList<Task> actives = new ArrayList<Task>();
        for (Task t : subtasks)
            if (!t.isCompleted())
                actives.add(t);
        return actives;
    }

    public List<Task> getCompletedTasks() {
        ArrayList<Task> completed = new ArrayList<Task>();
        for (Task t : subtasks)
            if (t.isCompleted())
                completed.add(t);
        return completed;
    }

    public boolean has(Task task) {
        return subtasks.contains(task);
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    public static class Any extends Category {

        @XmlTransient
        private TaskHolder holder;

        /**
         * Serialization only
         */
        public Any() {
        }

        public Any(TaskHolder holder) {
            this.holder = holder;
        }

        void setHolder(TaskHolder holder) {
            this.holder = holder;
        }

        @Override
        public List<Task> getActiveTasks() {
            List<Task> actives = new ArrayList<Task>();
            actives.addAll(super.getActiveTasks());
            for (Category cat : holder.getCategories()) {
                if (cat != this) {
                    actives.addAll(cat.getActiveTasks());
                }
            }
            return actives;
        }

        @Override
        public List<Task> getCompletedTasks() {
            List<Task> completed = new ArrayList<Task>();
            completed.addAll(super.getCompletedTasks());
            for (Category cat : holder.getCategories()) {
                if (cat != this) {
                    completed.addAll(cat.getCompletedTasks());
                }
            }
            return completed;
        }

        @Override
        public List<Task> listSubtasks() {
            List<Task> tasks = new ArrayList<Task>();
            tasks.addAll(subtasks);
            for (Category cat : holder.getCategories()) {
                if (cat != this) {
                    tasks.addAll(cat.listSubtasks());
                }
            }
            return tasks;
        }

        @Override
        public String getName() {
            return "Any";
        }
    }
}
