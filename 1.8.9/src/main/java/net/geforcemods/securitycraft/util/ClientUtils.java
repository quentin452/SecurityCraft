package net.geforcemods.securitycraft.util;

import java.net.URI;
import java.net.URISyntaxException;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientUtils{

	@SideOnly(Side.CLIENT)
	public static void closePlayerScreen(){
		Minecraft.getMinecraft().thePlayer.closeScreen();
	}

	/**
	 * Takes a screenshot, and sends the player a notification. <p>
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static void takeScreenshot() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(Minecraft.getMinecraft().mcDataDir, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, Minecraft.getMinecraft().getFramebuffer()));
	}

	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static String getFormattedMinecraftTime(){
		Long time = Long.valueOf(Minecraft.getMinecraft().theWorld.provider.getWorldTime());

		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (time.longValue() / 16.666666F % 60.0F);

		return String.format("%02d:%02d %s", new Object[]{Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM"});
	}

	/**
	 * Sends the client-side NBTTagCompound of a block's TileEntity to the server.
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static void syncTileEntity(TileEntity tileEntity){
		NBTTagCompound tag = new NBTTagCompound();
		tileEntity.writeToNBT(tag);
		SecurityCraft.network.sendToServer(new PacketSSyncTENBTTag(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), tag));
	}

	/**
	 * Sends the client-side NBTTagCompound of a player's held item to the server.
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static void syncItemNBT(ItemStack stack){
		SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(stack));
	}

	@SideOnly(Side.CLIENT)
	public static void openURL(String url) {
		URI uri = null;

		try {
			uri = new URI(url);
		}
		catch(URISyntaxException e) {
			e.printStackTrace();
		}

		if(uri == null) return;

		try {
			Class desktopClass = Class.forName("java.awt.Desktop");
			Object getDesktopResult = desktopClass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			desktopClass.getMethod("browse", new Class[] {URI.class}).invoke(getDesktopResult, new Object[] {uri});
		}

		catch (Throwable throwable) {}
	}

}