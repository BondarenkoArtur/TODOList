package com.uabart.todolist.logic;

import com.uabart.todolist.entity.Category;
import com.uabart.todolist.entity.Options;
import com.uabart.todolist.entity.Task;
import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.gui.GuiListener;
import com.uabart.todolist.gui.GuiMessage;
import com.uabart.todolist.gui.Layout;

import java.util.Comparator;
import java.util.Stack;

public class Logic implements GuiListener {

    private final boolean isShowCategories;
    private Layout layout;
    private TaskHolder holder;
    private Stack<Task> stack;
    private Stack<Integer> pages;
    private Sorter sorter;
    private Category selected;

    public Logic() {
        sorter = new Sorter();
        isShowCategories = Options.getInstance().showCategories();
        resetLogic();
    }

    private void resetLogic() {
        stack = new Stack<Task>();
        pages = new Stack<Integer>();
        pages.push(0);
    }

    public void init(final Layout layout, final TaskHolder holder) {
        this.layout = layout;
        this.holder = holder;
        layout.setListener(this);
        resetLogic();
        final Stack<Task> tempStack = new Stack<Task>();
        final boolean isSelected = generateStack(holder, tempStack);
        if (isSelected) {
            while (!tempStack.isEmpty()) {
                stack.push(tempStack.pop());
            }
            final Task topItem = stack.peek();
            if (topItem instanceof Category) {
                layout.showCategory((Category) topItem, 0);
            } else {
                layout.showTask(topItem);
            }
        } else {
            if (isShowCategories) {
                layout.showMain(pages.peek());
            } else {
                final Category categoryAny = holder.getCategories().get(0);
                stack.push(categoryAny);
                layout.showCategory(categoryAny, pages.peek());
                selected = categoryAny;
            }
        }
    }

    private boolean generateStack(final TaskHolder holder, final Stack<Task> tempStack) {
        boolean isSelected = false;
        for (int catId = holder.getCategories().size() - 1; catId >= 0; catId--) {
            if (generateForStack(tempStack, holder.getCategories().get(catId))) {
                isSelected = true;
                break;
            }
        }
        return isSelected;
    }

    private boolean generateForStack(final Stack<Task> tempStack, final Task task) {
        boolean selected = false;
        if (task.isSelected() != null && task.isSelected()) {
            selected = selectItem(tempStack, task);
        } else if (!task.listSubtasks().isEmpty()) {
            for (Task subtask : task.listSubtasks()) {
                if (generateForStack(tempStack, subtask)) {
                    selected = true;
                    break;
                }
            }
            selected = selected && selectItem(tempStack, task);
        }
        return selected;
    }

    private boolean selectItem(final Stack<Task> tempStack, final Task task) {
        tempStack.push(task);
        pages.push(0);
        if (task instanceof Category) {
            selected = (Category) task;
        }
        return true;
    }

    @Override
    public void update(final GuiMessage message, final Task task) {
        try {
            switch (message) {
                case ADD_TASK:
                    addTask();
                    break;

                case ADD_CATEGORY:
                    addCategory();
                    break;

                case BACK:
                    onBackPressed();
                    break;

                case COMPLETE:
                    onCompleteTask(task);
                    break;

                case DELETE:
                    onDeleteTask(task);
                    break;

                case MOVE_UP:
                    onMoveUpTask(task);
                    break;

                case MOVE_DOWN:
                    onMoveDownTask(task);
                    break;

                case NEXT_PAGE:
                    onNextPage();
                    break;

                case PREVIOUS_PAGE:
                    onPreviousPage();
                    break;

                case SELECT:
                    onSelect(task);
                    break;

                case REFRESH:
                    onRefresh();
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onRefresh() {
        if (stack.isEmpty()) {
            layout.showMain(0);
        } else {
            final Task task = stack.peek();
            if (task instanceof Category) {
                layout.showCategory((Category) task, pages.peek());
            } else {
                layout.showTask(task);
            }
        }
    }

    private void onSelect(final Task task) {
        if (!stack.isEmpty()) {
            stack.peek().setSelected(null);
        }
        task.setSelected(true);
        if (task instanceof Category) {
            final Category category = (Category) task;
            stack.push(category);
            pages.push(0);
            selected = category;
            layout.showCategory((Category) stack.peek(), pages.peek());

        } else {
            stack.push(task);
            pages.push(0);
            layout.showTask(task);
        }
    }

    private void onPreviousPage() {
        int current = pages.pop();

        if (current > 0) {
            current--;

            if (stack.isEmpty()) {
                layout.showMain(current);
            } else {
                layout.showCategory((Category) stack.peek(), current);
            }

            pages.push(current);
        }
    }

    private void onNextPage() {
        int current = pages.pop() + 1;

        float math;

        if (stack.isEmpty()) {
            // main screen
            math = (float) holder.getCategories().size() / (float) Options.getInstance().getMaxTasksOnScreen();
        } else {
            // Discover the fraction of the number of pages to be
            // displayed
            math = (float) selected.getActiveTasks().size() / (float) Options.getInstance().getMaxTasksOnScreen();

            if (Options.getInstance().showCompletedTasks()) {
                math += (float) selected.getCompletedTasks().size() / (float) Options.getInstance().getMaxTasksOnScreen();
            }
        }

        // Only add one extra page if we have any fraction
        final int maxPages = ((int) math) == math ? (int) math : (int) math + 1;

        if (current < maxPages) {
            if (stack.isEmpty()) {
                layout.showMain(current);
            } else {
                layout.showCategory((Category) stack.peek(), current);
            }
        } else {
            current--;
        }

        pages.push(current);
    }

    private void onMoveDownTask(final Task task) {
        if (!task.equals(stack.peek())) {
            stack.peek().moveTask(task, false);
            layout.showTask(stack.peek());
        }
    }

    private void onMoveUpTask(final Task task) {
        if (!task.equals(stack.peek())) {
            stack.peek().moveTask(task, true);
            layout.showTask(stack.peek());
        }
    }

    private void onDeleteTask(final Task task) {
        if (task.equals(stack.peek())) {
            // deleting current shown item
            stack.peek().setSelected(null);
            stack.pop();
            pages.pop();
            if (stack.isEmpty() && task instanceof Category) {
                // deleting a category
                holder.getCategories().remove(task);
            } else {
                stack.peek().removeTask(task);
            }
            if (stack.isEmpty()) {
                // We deleted a category
                layout.showMain(pages.peek());
            } else if (stack.peek() instanceof Category) {
                // We deleted a task within a category
                stack.peek().setSelected(true);
                layout.showCategory((Category) stack.peek(), pages.peek());
            } else {
                // We deleted a sub-task
                stack.peek().setSelected(true);
                layout.showTask(stack.peek());
            }
        } else {
            // Deleted without selecting (i.e. sub-task within a task)
            stack.peek().removeTask(task);
            layout.showTask(stack.peek());
        }
    }

    private void onCompleteTask(final Task task) {
        if (task.isCompleted()) {
            task.setCompleted(false);
        } else {
            task.setCompleted(true);
        }

        if (stack.peek() instanceof Category) {
            layout.showCategory((Category) stack.peek(), pages.peek());
        } else if (stack.isEmpty()) {
            layout.showMain(pages.peek());
        }
    }

    private void onBackPressed() {
        stack.peek().setSelected(null);
        stack.pop();
        pages.pop();
        if (!stack.isEmpty()) {
            if (stack.peek() instanceof Category) {
                layout.showCategory((Category) stack.peek(), pages.peek());
            } else {
                layout.showTask(stack.peek());
            }
            stack.peek().setSelected(true);
        } else {
            if (pages.isEmpty()) {
                pages.push(0);
            }
            layout.showMain(pages.peek());
            selected = null;
        }
    }

    private void addCategory() {
        final Category cat = new Category();
        cat.setName("New Category");
        cat.setSelected(true);
        stack.push(cat);
        pages.push(0);
        holder.getCategories().add(cat);
        layout.showCategory(cat, pages.peek());
    }

    private void addTask() {
        final Task newOne = new Task();
        newOne.setName("Empty");
        if (!(stack.peek() instanceof Category)) {
            // has a parent
            stack.peek().addTask(newOne);
            layout.showTask(stack.peek());
        } else {
            // no parent
            stack.peek().addTask(newOne);
            stack.peek().setSelected(null);
            newOne.setSelected(true);
            stack.push(newOne);
            pages.push(0);
            layout.showTask(newOne);
        }
    }

    private class Sorter implements Comparator<Task> {
        @Override
        public int compare(final Task o1, final Task o2) {
            return o1.getPriority() - o2.getPriority();
        }
    }
}
