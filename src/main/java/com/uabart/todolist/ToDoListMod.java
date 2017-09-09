package com.uabart.todolist;

import com.uabart.todolist.entity.Category;
import com.uabart.todolist.entity.Options;
import com.uabart.todolist.entity.Task;
import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.handler.DrawHandler;
import com.uabart.todolist.handler.NEIToDoGuiHandler;
import com.uabart.todolist.handler.OverlayDrawHandler;
import com.uabart.todolist.manager.Manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.lang.reflect.Field;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import codechicken.nei.api.API;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ToDoListMod.MODID, version = ToDoListMod.VERSION)
public class ToDoListMod {
    public static final String MODID = "todolist";
    public static final String VERSION = "1.0.4";

    @Mod.Instance
    public static ToDoListMod instance;
    private boolean wasLoaded = false;
    private String serverName = "";
    private File dir, server;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new OverlayDrawHandler());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Options.load(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        // Hacked the Minecraft class to get the Server name!
        if (event.world.provider.dimensionId == 0 && !wasLoaded) {

            Field[] fields = Minecraft.getMinecraft().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getGenericType().toString().equals(ServerData.class.toString())) {
                    boolean b = field.isAccessible();
                    field.setAccessible(true);
                    ServerData obj = null;
                    try {
                        obj = (ServerData) field.get(Minecraft.getMinecraft());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (obj == null)
                        serverName = MinecraftServer.getServer().getWorldName();
                    else
                        serverName = obj.serverName;

                    if (!b)
                        field.setAccessible(false);
                }
            }

            dir = new File(Minecraft.getMinecraft().mcDataDir, "config" + File.separator + "todolist");
            dir.mkdirs();
            server = new File(dir, serverName);

            DrawHandler.init = true;
            wasLoaded = true;

            if (server.exists()) {
                try {
                    JAXBContext context = JAXBContext.newInstance(TaskHolder.class, Task.class, Category.class, Category.Any.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    TaskHolder th = (TaskHolder) unmarshaller.unmarshal(server);
                    Manager.getHolder().setCategories(th.getCategories());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            API.registerNEIGuiHandler(new NEIToDoGuiHandler());
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.provider.dimensionId == 0 && wasLoaded) {
            wasLoaded = false;
            DrawHandler.init = true;

            JAXBContext context;
            try {
                context = JAXBContext.newInstance(TaskHolder.class, Task.class, Category.class, Category.Any.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(Manager.getHolder(), server);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }
}
