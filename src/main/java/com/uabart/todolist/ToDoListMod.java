package com.uabart.todolist;

import com.uabart.todolist.entity.Category;
import com.uabart.todolist.entity.Options;
import com.uabart.todolist.entity.Task;
import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.handler.DrawHandler;
import com.uabart.todolist.handler.NEIToDoGuiHandler;
import com.uabart.todolist.handler.OverlayDrawHandler;
import com.uabart.todolist.manager.Manager;

import cpw.mods.fml.common.event.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import codechicken.nei.api.API;
import cpw.mods.fml.common.Mod;

@Mod(modid = ToDoListMod.MODID, version = ToDoListMod.VERSION)
public class ToDoListMod {
    public static final String MODID = "todolist";
    public static final String VERSION = "1.0.6";

    @Mod.Instance
    public static ToDoListMod instance;
    private String serverName = "";
    private File home_dir, server_config;

    private Logger logger = Logger.getLogger(this.MODID);

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
        this.logger.info("Server is starting, loading base settings");
        serverName = event.getServer().getWorldName();

        this.logger.info(String.format("Server name is %s", serverName));
        home_dir = new File(Minecraft.getMinecraft().mcDataDir, "config" + File.separator + "todolist");
        home_dir.mkdirs();
        server_config = new File(home_dir, serverName);

        DrawHandler.init = true;

        if (server_config.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(TaskHolder.class, Task.class, Category.class, Category.Any.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                TaskHolder th = (TaskHolder) unmarshaller.unmarshal(server_config);
                Manager.getHolder().setCategories(th.getCategories());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        API.registerNEIGuiHandler(new NEIToDoGuiHandler());
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        this.logger.info("Saving data");
        DrawHandler.init = true;

        JAXBContext context;
        try {
            context = JAXBContext.newInstance(TaskHolder.class, Task.class, Category.class, Category.Any.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(Manager.getHolder(), server_config);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
