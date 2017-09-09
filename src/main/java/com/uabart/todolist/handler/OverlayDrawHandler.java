package com.uabart.todolist.handler;

import com.uabart.todolist.gui.OverlayGui;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OverlayDrawHandler {

    private OverlayGui inGameGUI = new OverlayGui();

    @SubscribeEvent
    public void postRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            inGameGUI.renderGameOverlay(event.partialTicks, event.mouseX, event.mouseY);
        }
    }

}
