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

    private Layout layout;
    private TaskHolder holder;
    private Stack<Task> stack;
    private Stack<Integer> pages;
    private Sorter sorter;
    private Category selected;

    public Logic() {
        stack = new Stack<Task>();
        pages = new Stack<Integer>();
        sorter = new Sorter();
        pages.push(0);
    }

    public void init(Layout layout, TaskHolder holder) {
        this.layout = layout;
        this.holder = holder;
        layout.setListener(this);
        boolean isShowCategories = Options.getInstance().showCategories();
        if (isShowCategories) {
            layout.showMain(pages.peek());
        } else {
            Category categoryAny = holder.getCategories().get(0);
            stack.push(categoryAny);
            layout.showCategory(categoryAny, pages.peek());
            selected = categoryAny;
        }
    }

    @Override
    public void update(GuiMessage message, Object obj) {

        try {

            switch (message) {
                case ADD_TASK:

                    Task newOne = new Task();
                    newOne.setName("Empty");
                    if (!(stack.peek() instanceof Category)) {
                        // has a parent
                        stack.peek().addTask(newOne);
                        layout.showTask(stack.peek());
                    } else {
                        // no parent
                        stack.peek().addTask(newOne);
                        stack.push(newOne);
                        pages.push(0);
                        layout.showTask(newOne);
                    }

                    break;

                case ADD_CATEGORY:

                    Category cat = new Category();
                    cat.setName("New Category");
                    stack.push(cat);
                    pages.push(0);
                    holder.getCategories().add(cat);
                    layout.showCategory(cat, pages.peek());

                    break;

                case BACK:

                    stack.pop();
                    pages.pop();
                    if (!stack.isEmpty()) {
                        if (stack.peek() instanceof Category)
                            layout.showCategory((Category) stack.peek(), pages.peek());
                        else
                            layout.showTask(stack.peek());
                    } else {
                        if (pages.isEmpty()) {
                            pages.push(0);
                        }
                        layout.showMain(pages.peek());
                        selected = null;
                    }

                    break;

                case COMPLETE:

                    Task about = (Task) obj;

                    if (about.isCompleted()) {
                        about.setCompleted(false);
                    } else {
                        about.setCompleted(true);
                    }

                    if (stack.peek() instanceof Category)
                        layout.showCategory((Category) stack.peek(), pages.peek());
                    else if (stack.isEmpty())
                        layout.showMain(pages.peek());

                    break;

                case DELETE:

                    if (obj.equals(stack.peek())) {
                        // deleting current shown item
                        stack.pop();
                        pages.pop();
                        if (stack.isEmpty()) {
                            // deleting a category
                            holder.getCategories().remove(obj);
                        } else {
                            stack.peek().removeTask((Task) obj);
                        }

                        if (stack.isEmpty())
                            // We deleted a category
                            layout.showMain(pages.peek());
                        else if (stack.peek() instanceof Category)
                            // We deleted a task within a category
                            layout.showCategory((Category) stack.peek(), pages.peek());
                        else
                            // We deleted a sub-task
                            layout.showTask(stack.peek());
                    } else {
                        // Deleted without selecting (i.e. sub-task within a task)
                        stack.peek().removeTask((Task) obj);
                        layout.showTask(stack.peek());
                    }

                    break;

                case NEXT_PAGE:

                    int c = pages.pop() + 1;

                    float math;

                    if (stack.isEmpty()) {
                        // main screen
                        math = (float) holder.getCategories().size() / (float) Options.getInstance().getMaxTasksOnScreen();

                    } else {
                        // Discover the fraction of the number of pages to be
                        // displayed
                        math = (float) selected.getActiveTasks().size() / (float) Options.getInstance().getMaxTasksOnScreen();

                        if (Options.getInstance().showCompletedTasks())
                            math += (float) selected.getCompletedTasks().size() / (float) Options.getInstance().getMaxTasksOnScreen();
                    }

                    // Only add one extra page if we have any fraction
                    int maxPages = ((int) math) == math ? (int) math : (int) math + 1;

                    if (c < maxPages) {
                        if (stack.isEmpty())
                            layout.showMain(c);
                        else
                            layout.showCategory((Category) stack.peek(), c);
                    } else
                        c--;

                    pages.push(c);

                    break;

                case PREVIOUS_PAGE:

                    int cc = pages.pop();

                    if (cc > 0) {
                        cc--;

                        if (stack.isEmpty())
                            layout.showMain(cc);
                        else
                            layout.showCategory((Category) stack.peek(), cc);

                        pages.push(cc);
                    }

                    break;

                case SELECT:

                    if (obj instanceof Category) {
                        Category cat3 = (Category) obj;
                        stack.push(cat3);
                        pages.push(0);
                        selected = cat3;
                        layout.showCategory((Category) stack.peek(), pages.peek());

                    } else {
                        Task about3 = (Task) obj;
                        stack.push(about3);
                        pages.push(0);
                        layout.showTask(about3);
                    }

                    break;

                case REFRESH:
                    if (stack.isEmpty()) {
                        layout.showMain(0);
                    } else {
                        Task task = stack.peek();
                        if (task instanceof Category) {
                            layout.showCategory((Category) task, pages.peek());
                        } else {
                            layout.showTask(task);
                        }
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Sorter implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getPriority() - o2.getPriority();
        }
    }
}
