package com.uabart.todolist;

import com.uabart.todolist.entity.Category;
import com.uabart.todolist.entity.Options;
import com.uabart.todolist.entity.Task;
import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.handler.DrawHandler;
import com.uabart.todolist.handler.NEIToDoGuiHandler;
import com.uabart.todolist.handler.OverlayDrawHandler;
import com.uabart.todolist.manager.Manager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import codechicken.nei.api.API;
import cpw.mods.fml.common.Mod;

@Mod(modid = ToDoListMod.MODID, version = ToDoListMod.VERSION)
public class ToDoListMod {
    public static final String MODID = "todolist";
    public static final String VERSION = "1.0.8";

    @Mod.Instance("todolist")
    public static ToDoListMod instance;

    private Logger logger = null;

    private File configDir;
    private File currentServerConfig;


    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (logger == null) {
            logger = LogManager.getLogger(MODID);
        }
        logger.info("ToDoList Initializing");
        MinecraftForge.EVENT_BUS.register(new OverlayDrawHandler());

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Options.load(event);
        configDir = new File(event.getModConfigurationDirectory(), MODID);
        if(!configDir.exists()) { configDir.mkdir(); }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        logger.info("Server is starting, loading base settings");
        String serverName = MinecraftServer.getServer().getWorldName()+".json";

        logger.info(String.format("Server name is %s", serverName));
        currentServerConfig = new File(configDir, serverName);

        DrawHandler.init = true;

        if (currentServerConfig.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(TaskHolder.class, Task.class, Category.class, Category.Any.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                TaskHolder th = (TaskHolder) unmarshaller.unmarshal(currentServerConfig);
                Manager.getHolder().setCategories(th.getCategories());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        API.registerNEIGuiHandler(new NEIToDoGuiHandler());
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.logger.info("Saving data");
        DrawHandler.init = true;

        JAXBContext context;
        try {
            context = JAXBContext.newInstance(TaskHolder.class, Task.class, Category.class, Category.Any.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(Manager.getHolder(), currentServerConfig);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
