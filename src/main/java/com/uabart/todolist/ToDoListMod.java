package com.uabart.todolist;

import com.uabart.todolist.entity.Category;
import com.uabart.todolist.entity.Options;
import com.uabart.todolist.entity.Task;
import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.handler.NEIToDoGuiHandler;
import com.uabart.todolist.handler.OverlayDrawHandler;
import com.uabart.todolist.manager.Manager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import codechicken.nei.api.API;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = ToDoListMod.MODID, version = ToDoListMod.VERSION)
public class ToDoListMod {

    public static final String MODID = "todolist";
    public static final String VERSION = "1.0.14";

    @Mod.Instance("todolist")
    public static ToDoListMod instance;

    private Logger logger;

    private File configDir;
    private File currentServerConfig;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
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
    public void preInit(final FMLPreInitializationEvent event) {
        Options.load(event);
        configDir = new File(event.getModConfigurationDirectory(), MODID);
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        try {
            final JAXBContext context = JAXBContext.newInstance(
                TaskHolder.class, Task.class, Category.class, Category.Any.class);
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onClientConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        logger.info("Server is starting, loading base settings");

        final String serverName;
        if (event.isLocal) {
            serverName = Minecraft.getMinecraft().getIntegratedServer().getWorldName() + ".xml";
        } else {
            serverName = FMLClientHandler.instance().getClient().func_147104_D().serverName + ".xml";
        }

        logger.info(String.format("Server filename is %s", serverName));
        currentServerConfig = new File(configDir, serverName);

        if (currentServerConfig.exists()) {
            try {
                final TaskHolder taskHolder = (TaskHolder) unmarshaller.unmarshal(currentServerConfig);
                Manager.getHolder().setCategories(taskHolder.getCategories());
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        } else {
            final TaskHolder empty = new TaskHolder();
            Manager.getHolder().setCategories(empty.getCategories());
        }
        Manager.init();
        API.registerNEIGuiHandler(new NEIToDoGuiHandler());
    }

    @SubscribeEvent
    public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Manager.getLayout().resetLayout();
        saveConfig();
    }

    public void saveConfig() {
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(Manager.getHolder(), currentServerConfig);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
