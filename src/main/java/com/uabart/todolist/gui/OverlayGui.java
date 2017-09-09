package com.uabart.todolist.gui;

import com.uabart.todolist.manager.Manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.Widget;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OverlayGui extends Gui {

    private static Minecraft mc = NEIClientUtils.mc();

    public void renderGameOverlay(float partialTicks, int mouseX, int mouseY) {
        if (mc.thePlayer != null &&
                mc.theWorld != null) {
            RenderHelper.enableGUIStandardItemLighting();
            for (Widget widget : Manager.getLayout().getToDrawOverlay()) {
                widget.draw(mouseX, mouseY);
            }
        }
    }
}
