package com.ForgeEssentials.snooper;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.snooper.API;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.snooper.response.PlayerArmor;
import com.ForgeEssentials.snooper.response.PlayerInfoResonce;
import com.ForgeEssentials.snooper.response.PlayerInv;
import com.ForgeEssentials.snooper.response.PlayerList;
import com.ForgeEssentials.snooper.response.ServerInfo;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.network.rcon.IServer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;

@FEModule(name = "SnooperModule", parentMod = ForgeEssentials.class, configClass = ConfigSnooper.class)
public class ModuleSnooper
{
	@Config
	public static ConfigSnooper			configSnooper;

	public static int					port;
	public static String				hostname;
	public static boolean				enable;

	public static RConQueryThread		theThread;
	private static ArrayList<String>	names;

	public static boolean				autoReboot;

	public ModuleSnooper()
	{
		OutputHandler.SOP("Snooper module is enabled. Loading...");
		MinecraftForge.EVENT_BUS.register(this);

		API.registerResponce(0, new ServerInfo());
		API.registerResponce(1, new PlayerList());

		API.registerResponce(5, new PlayerInfoResonce());
		API.registerResponce(6, new PlayerArmor());
		API.registerResponce(7, new PlayerInv());
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandReloadQuery());
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.Snooper._ALL_", true);
	}

	public static void startQuery()
	{
		try
		{
			if (theThread != null)
			{
				ModuleSnooper.theThread.closeAllSockets_do(true);
				ModuleSnooper.theThread.running = false;
			}
			if (enable)
			{
				theThread = new RConQueryThread((IServer) FMLCommonHandler.instance().getMinecraftServerInstance());
				theThread.startThread();
			}
		}
		catch (Exception e)
		{
		}
	}
}
