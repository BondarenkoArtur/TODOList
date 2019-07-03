package com.uabart.todolist.gui;

import com.uabart.todolist.entity.Category;
import com.uabart.todolist.entity.Options;
import com.uabart.todolist.entity.Task;
import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.gui.components.DeleteGuiButton;
import com.uabart.todolist.gui.components.FieldButtonName;
import com.uabart.todolist.gui.components.FieldCompletedCheckbox;
import com.uabart.todolist.gui.components.FieldIcon;
import com.uabart.todolist.gui.components.FieldMainName;
import com.uabart.todolist.gui.components.GuiButton;
import com.uabart.todolist.gui.components.MoveGuiButton;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import codechicken.nei.Button;
import codechicken.nei.ItemPanel;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.TextField;
import codechicken.nei.Widget;
import codechicken.nei.guihook.GuiContainerManager;

public class Layout {

    private GuiButton previousPage, nextPage, addTask, addCategory, back;
    private GuiButton showHideButton;
    private List<Widget> toDraw;
    private List<Widget> toDrawOverlay;
    private List<FieldIcon> fieldIcons;
    private HashMap<Task, List<Widget>> widgetMap;
    private TaskHolder holder;
    private GuiListener listener;
    private boolean getFocus = false;
    private boolean isShowCategories;

    public Layout() {
        resetLayout();
    }

    public void resetLayout() {
        toDraw = new ArrayList<Widget>();
        toDrawOverlay = new ArrayList<Widget>();
        fieldIcons = new ArrayList<FieldIcon>();
        widgetMap = new HashMap<Task, List<Widget>>();
    }

    private void sendMessage(GuiMessage toSend) {
        sendMessage(toSend, null);
    }

    private void sendMessage(GuiMessage toSend, Task task) {
        listener.update(toSend, task);
    }

    public void setListener(GuiListener listener) {
        this.listener = listener;
    }

    public void init(final TaskHolder holder) {
        this.holder = holder;
        resetLayout();

        isShowCategories = Options.getInstance().showCategories();

        boolean edgeAlign = true;
//		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
        int offsetx = edgeAlign ? 0 : 3;

        showHideButton = new GuiButton("ToDo", 0, 0, 20, 6) {
            @Override
            public boolean onButtonPress(boolean b) {
                toggleMenuHidden();
                return true;
            }
        };

        previousPage = new GuiButton("<", 0, 20, 20, 6) {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.PREVIOUS_PAGE);
                return true;
            }

            @Override
            public String getButtonTip() {
                return "Previous page";
            }
        };

        Integer draw_offset = previousPage.x + previousPage.w + offsetx;

        addTask = new GuiButton("Add task", draw_offset, previousPage.y, 20, 0) {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.ADD_TASK);
                return true;
            }
        };

        addCategory = new GuiButton("Add category", draw_offset, previousPage.y, 20, 0) {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.ADD_CATEGORY);
                return true;
            }
        };

        int wt = Math.max(addTask.contentWidth(), addCategory.contentWidth()) + 6;
        addTask.w = wt;
        addCategory.w = wt;

        back = new GuiButton("Back", addTask.x + offsetx + wt, addTask.y, addTask.h,  6) {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.BACK);
                return false;
            }
        };

        nextPage = new GuiButton(">", back.x + back.w + offsetx, addTask.y, 20, 6) {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.NEXT_PAGE);
                return true;
            }

            @Override
            public String getButtonTip() {
                return "Next page";
            }
        };
    }

    public void toggleMenuHidden() {
        Options.getInstance().setVisible(!Options.getInstance().getVisible());
        sendMessage(GuiMessage.REFRESH);
    }

    public void showCategory(final Category category, int currentPage) {
        // Clear current screen
        toDraw.clear();
        toDrawOverlay.clear();

        // alignment check
        boolean edgeAlign = true;
//		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
        int offsety = edgeAlign ? 0 : 3;

        FieldMainName categoryName = new FieldMainName(category);
        categoryName.x = addTask.x;
        categoryName.y = 40;
        categoryName.w = 100;
        categoryName.h = back.h;

        final Button categoryDelete = new DeleteGuiButton(listener, category, DeleteGuiButton.Type.CATEGORY);
        categoryDelete.x = previousPage.x;
        categoryDelete.y = categoryName.y - 1;
        categoryDelete.h = 22;
        categoryDelete.w = categoryDelete.contentWidth() + 6;

        int count = 0;
        // first we display the non-completed tasks
        List<Task> active = category.getActiveTasks();
        for (int i = currentPage * Options.getInstance().getMaxTasksOnScreen(); i < active.size() && count < Options.getInstance().getMaxTasksOnScreen(); i++) {
            Task t = active.get(i);

            FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(t) {
                @Override
                public boolean onButtonPress(boolean rightclick) {
                    sendMessage(GuiMessage.COMPLETE, getTask());
                    return true;
                }
            };
            checkbox.y = 60 + offsety + count * 20;
            checkbox.h = 17;
            checkbox.w = checkbox.contentWidth() + 6;
            checkbox.x = previousPage.x;

            FieldButtonName textfield = new FieldButtonName(t, 17, 100) {
                @Override
                public boolean onButtonPress(boolean rightclick) {
                    sendMessage(GuiMessage.SELECT, this.getTask());
                    return true;
                }
            };
            textfield.x = addTask.x;
            textfield.y = checkbox.y;

            count++;
            toDraw.add(checkbox);
            toDraw.add(textfield);
        }

        boolean drawCompleted = Options.getInstance().showCompletedTasks();

        // only then we show the completed ones
        List<Task> completed = category.getCompletedTasks();
        if (drawCompleted) {
            for (int i = currentPage * Options.getInstance().getMaxTasksOnScreen() - active.size() + count; i < completed.size() && count < Options.getInstance().getMaxTasksOnScreen(); i++) {
                Task t = completed.get(i);

                FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(t) {
                    @Override
                    public boolean onButtonPress(boolean rightclick) {
                        sendMessage(GuiMessage.COMPLETE, getTask());
                        return true;
                    }
                };
                checkbox.y = 60 + offsety + count * 20;
                checkbox.h = 17;
                checkbox.w = checkbox.contentWidth() + 6;
                checkbox.x = previousPage.x;

                FieldButtonName textfield = new FieldButtonName(t, 17, 100) {
                    @Override
                    public boolean onButtonPress(boolean rightclick) {
                        sendMessage(GuiMessage.SELECT, this.getTask());
                        return true;
                    }
                };
                textfield.x = addTask.x;
                textfield.y = checkbox.y;

                count++;
                toDraw.add(checkbox);
                toDraw.add(textfield);
            }
        }

        // Disable previous/next page buttons
        if ((active.size() + (drawCompleted ? completed.size() : 0)) > currentPage * Options.getInstance().getMaxTasksOnScreen() + Options.getInstance().getMaxTasksOnScreen())
            nextPage.state = 0;
        else
            nextPage.state = 2;

        if (currentPage > 0)
            previousPage.state = 0;
        else
            previousPage.state = 2;

        // Add basic buttons
        if (category instanceof Category.Any)
            categoryName.setEditable(false);
        else
            toDraw.add(categoryDelete);

        toDraw.add(categoryName);
        toDraw.add(previousPage);
        toDraw.add(nextPage);
        toDraw.add(addTask);
        if (isShowCategories) {
            toDraw.add(back);
        }

        if (!Options.getInstance().getVisible()) {
            toDraw.clear();
            toDrawOverlay.clear();
        }
        toDraw.add(showHideButton);
    }

    public void showTask(final Task task) {

        toDraw.clear();
        toDrawOverlay.clear();
        fieldIcons.clear();

        FieldIcon icon = new FieldIcon(task) {
            @Override
            public boolean handleClickExt(int mx, int my, int button) {
                if (button == 2) {
                    onClick(mx, my, task, this);
                }
                return super.handleClickExt(mx, my, button);
            }

            @Override
            public void onGuiClick(int mousex, int mousey) {
                if (LayoutManager.itemPanel.contains(mousex, mousey)) {
                    onClick(mousex, mousey, task, this);
                }
            }
        };

        icon.x = addTask.x + 3;
        icon.y = 42;
        icon.w = 18;
        icon.h = 18;

        FieldMainName mainName = new FieldMainName(task);
        mainName.x = icon.x + icon.w;
        mainName.y = icon.y - 2;
        mainName.w = 100;
        mainName.h = addTask.h;

        FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(task) {
            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.COMPLETE, getTask());
                return false;
            }
        };
        checkbox.w = checkbox.contentWidth() + 6;
        checkbox.h = 11;
        checkbox.y = mainName.y - 1;
        checkbox.x = previousPage.x;

        final Button delete = new DeleteGuiButton(listener, task, DeleteGuiButton.Type.TASK);
        delete.x = checkbox.x;
        delete.y = checkbox.y + checkbox.h;
        delete.h = checkbox.h;
        delete.w = delete.contentWidth() + 6;

        toDraw.add(checkbox);
        toDraw.add(delete);
        toDraw.add(mainName);
        toDraw.add(icon);
        fieldIcons.add(icon);

        FieldMainName transpMainName = mainName.clone();
        transpMainName.setBackgroundVisible(false);
        FieldIcon transpMainIcon = icon.clone();
        transpMainIcon.setBackgroundVisible(false);
        toDrawOverlay.add(transpMainName);
        toDrawOverlay.add(transpMainIcon);
        int n = 0;
        for (final Task sub : task.listSubtasks()) {
            if (!sub.isCompleted() || Options.getInstance().showCompletedTasks()) {
                FieldMainName subName = new FieldMainName(sub) {
                    @Override
                    public boolean handleClick(int mousex, int mousey, int button) {
                        if (button == 2) {
                            sendMessage(GuiMessage.SELECT, this.getTask());
                            return true;
                        }
                        return super.handleClick(mousex, mousey, button);
                    }
                };
                subName.x = mainName.x;
                subName.y = (mainName.y + 10) + (n + 1) * mainName.h + n;
                subName.w = mainName.w;
                subName.h = mainName.h - 2;
                if (getFocus)
                    subName.setFocus(true);

                FieldCompletedCheckbox subCheckbox = new FieldCompletedCheckbox(sub) {
                    @Override
                    public boolean onButtonPress(boolean rightclick) {
                        sendMessage(GuiMessage.COMPLETE, getTask());
                        return true;
                    }
                };
                subCheckbox.w = checkbox.w;
                subCheckbox.h = checkbox.h - 1;
                subCheckbox.y = subName.y - 1;
                subCheckbox.x = previousPage.x;

                final Button subDelete = new DeleteGuiButton(listener, sub, DeleteGuiButton.Type.SUBTASK);
                subDelete.x = subCheckbox.x;
                subDelete.y = subCheckbox.y + subCheckbox.h;
                subDelete.h = subCheckbox.h;
                subDelete.w = subCheckbox.w;

                FieldIcon subicon = new FieldIcon(sub) {
                    @Override
                    public boolean handleClickExt(int mx, int my, int button) {
                        if (button == 2) {
                            onClick(mx, my, sub, this);
                        }
                        return super.handleClickExt(mx, my, button);
                    }

                    @Override
                    public void onGuiClick(int mousex, int mousey) {
                        if (LayoutManager.itemPanel.contains(mousex, mousey)) {
                            onClick(mousex, mousey, sub, this);
                        }
                    }
                };
                subicon.x = icon.x;
                subicon.y = subName.y + 2;
                subicon.h = icon.h - 2;
                subicon.w = icon.w - 2;
                subicon.offset = 1;

                final Button subMoveUp = new MoveGuiButton(listener, sub, true);
                final Button subMoveDown = new MoveGuiButton(listener, sub, false);
                subMoveUp.x = subName.x + subName.w;
                subMoveUp.y = subName.y;
                subMoveUp.h = subName.h / 2;
                subMoveUp.w = subMoveUp.contentWidth() + 4;
                subMoveDown.x = subMoveUp.x;
                subMoveDown.y = subMoveUp.y + subMoveUp.h;
                subMoveDown.h = subMoveUp.h;
                subMoveDown.w = subMoveUp.w;

                toDraw.add(subCheckbox);
                toDraw.add(subDelete);
                toDraw.add(subicon);
                toDraw.add(subName);
                toDraw.add(subMoveUp);
                toDraw.add(subMoveDown);
                fieldIcons.add(subicon);

                FieldMainName transpSubName = subName.clone();
                transpSubName.setBackgroundVisible(false);
                FieldIcon transpSubIcon = subicon.clone();
                transpSubIcon.setBackgroundVisible(false);
                toDrawOverlay.add(transpSubName);
                toDrawOverlay.add(transpSubIcon);
                n++;
            }
        }

        if (!task.isCompleted()) {
            TextField newSub = new TextField("Empty") {
                private boolean changed = false;

                @Override
                public void onTextChange(String oldText) {
                    if (!changed) {
                        getFocus = true;
                        changed = true;
                        sendMessage(GuiMessage.ADD_TASK);
                    }
                }

                @Override
                public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
                    if (!contains(mx, my))
                        return tooltip;

                    tooltip.add("Right click to enable");
                    return tooltip;
                }

                @Override
                public void setFocus(boolean focus) {
                }

            };
            newSub.x = mainName.x;
            newSub.y = (mainName.y + 10) + (n + 1) * mainName.h + n;
            newSub.w = mainName.w;
            newSub.h = mainName.h;
            toDraw.add(newSub);
        }

        getFocus = false;

        toDraw.add(back);

        if (!Options.getInstance().getVisible()) {
            toDraw.clear();
            toDrawOverlay.clear();
        }
        toDraw.add(showHideButton);

    }

    private void onClick(int mouseX, int mouseY, Task task, FieldIcon icon) {
        if (icon.changing)
            if (LayoutManager.itemPanel.contains(mouseX, mouseY)
                && (!NEIClientConfig.isHidden() && NEIClientConfig.isEnabled())) {
                ItemPanel.ItemPanelSlot item = LayoutManager.itemPanel.getSlotMouseOver(mouseX, mouseY);
                if (item != null) {
                    ItemStack stack = item.item;
                    task.createReference(stack);

                    if (task.getName().isEmpty() || task.getName().equals("Empty"))
                        task.setName(stack.getDisplayName());

                    icon.changing = false;
                }
            } else {
                Minecraft mc = NEIClientUtils.mc();
                GuiContainer guiContainer = mc.currentScreen instanceof GuiContainer ? (GuiContainer) mc.currentScreen : null;
                if (guiContainer != null) {
                    Slot slot = GuiContainerManager.getSlotMouseOver(guiContainer);
                    if (slot != null && slot.getStack() != null) {
                        ItemStack stack = slot.getStack();
                        stack.stackSize = 1;
                        task.setReference(stack);

                        if (task.getName().isEmpty() || task.getName().equals("Empty"))
                            task.setName(stack.getDisplayName());

                        icon.changing = false;
                    }
                }
            }
    }

    public void showMain(int currentPage) {

        // Clear current screen
        toDraw.clear();
        toDrawOverlay.clear();

        // alignment check
        boolean edgeAlign = true;
//		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
        int offsety = edgeAlign ? 0 : 3;

        int count = 0;
        // first we display the non-completed tasks
        for (int i = currentPage * Options.getInstance().getMaxTasksOnScreen(); i < holder.getCategories().size() && count < Options.getInstance().getMaxTasksOnScreen(); i++, count++) {
            Category c = holder.getCategories().get(i);

            FieldButtonName textfield = new FieldButtonName(c, 17, 100) {
                @Override
                public boolean onButtonPress(boolean rightclick) {
                    sendMessage(GuiMessage.SELECT, this.getTask());
                    return true;
                }
            };
            textfield.x = addTask.x;
            textfield.y = 40 + offsety + count * 20;

            toDraw.add(textfield);
        }

        // Disable previous/next page buttons
        if ((holder.getCategories().size()) > currentPage * Options.getInstance().getMaxTasksOnScreen() + Options.getInstance().getMaxTasksOnScreen())
            nextPage.state = 0;
        else
            nextPage.state = 2;

        if (currentPage > 0)
            previousPage.state = 0;
        else
            previousPage.state = 2;

        // Add basic buttons
        toDraw.add(previousPage);
        toDraw.add(nextPage);
        toDraw.add(addCategory);

        if (!Options.getInstance().getVisible()) {
            toDraw.clear();
            toDrawOverlay.clear();
        }
        toDraw.add(showHideButton);

    }

    public List<Widget> getToDraw() {
        return toDraw;
    }

    public List<Widget> getToDrawOverlay() {
        return toDrawOverlay;
    }

    public List<FieldIcon> getFieldIcons() {
        return fieldIcons;
    }
}
