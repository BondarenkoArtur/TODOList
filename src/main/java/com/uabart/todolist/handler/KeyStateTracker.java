package com.uabart.todolist.handler;

import com.uabart.todolist.manager.Manager;

import codechicken.nei.KeyManager;
import codechicken.nei.KeyManager.IKeyStateTracker;
import codechicken.nei.api.API;
import net.minecraft.client.Minecraft;

import static com.uabart.todolist.entity.Options.DEFAULT_KEY;
import static com.uabart.todolist.entity.Options.KEY_IDENTIFIER;

public class KeyStateTracker implements IKeyStateTracker {
    @Override
    public void tickKeyStates() {
        if (Minecraft.getMinecraft().currentScreen == null) {
            if (KeyManager.keyStates.get(KEY_IDENTIFIER).down) {
                Manager.getLayout().toggleMenuHidden();
            }
        }
    }

    public static void load() {
        API.addKeyBind(KEY_IDENTIFIER, DEFAULT_KEY);
        KeyManager.trackers.add(new KeyStateTracker());
    }
}