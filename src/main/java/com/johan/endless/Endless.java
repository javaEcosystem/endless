/*
 * Endless.java -
 *
 * Author: Johan Lebek
 * Created at: Sat Mar 8 23:00:00 CET 2025
 *
 * Copyright (C) 2025 Johan Lebek
 *
 * Licensed under the MIT License.
 * You may obtain a copy of the license at
 * https://opensource.org/licenses/MIT
 */

package com.johan.endless;

import com.johan.endless.commands.*;
import com.johan.endless.hud.ItemFlow;
import com.johan.endless.hud.ModHUD;
import com.johan.endless.hud.ModMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = Endless.MODID, version = Endless.VERSION)
public class Endless {

    public static final Minecraft MC = Minecraft.getMinecraft();
    public static final String MODID = "endless";
    public static final String VERSION = "1.3";

    public static String cmdF7 = null;
    public static String cmdF8 = null;
    public static String cmdF9 = null;
    public static String cmdF10 = null;

    private final KeyBinding toggleF7Key = new KeyBinding("F7 command", Keyboard.KEY_F7, "Endless");
    private final KeyBinding toggleF8Key = new KeyBinding("F8 command", Keyboard.KEY_F8, "Endless");
    private final KeyBinding toggleF9Key = new KeyBinding("F9 command", Keyboard.KEY_F9, "Endless");
    private final KeyBinding toggleF10Key = new KeyBinding("F10 command", Keyboard.KEY_F10, "Endless");
    private final KeyBinding toggleInventoryKey = new KeyBinding("Toggle Inventory", Keyboard.KEY_UP, "Endless");
    private final KeyBinding toggleArmorKey = new KeyBinding("Toggle Armor", Keyboard.KEY_LEFT, "Endless");
    private final KeyBinding toggleMenuKey = new KeyBinding("Toggle Menu", Keyboard.KEY_RMENU, "Endless");
    private final KeyBinding toggleStrengthKey = new KeyBinding("[sw] Toggle Active Buffed Players", Keyboard.KEY_DOWN, "Endless");
    private final KeyBinding togglePlayAgain = new KeyBinding("Toggle Auto-rq", Keyboard.KEY_INSERT, "Endless");
    private final KeyBinding toggleAutoGG = new KeyBinding("Toggle Auto-gg", Keyboard.KEY_END, "Endless");
    private final KeyBinding toggleItemFlow = new KeyBinding("Toggle Item Flow", Keyboard.KEY_DELETE, "Endless");
    private final KeyBinding toggleActivePet = new KeyBinding("Toggle Active Pet", Keyboard.KEY_RIGHT, "Endless");

    private final Pattern duelPatternOne = Pattern.compile("WINNER!");
    private final Pattern duelPatternTwo = Pattern.compile("Duels Tokens");
    private final Pattern endPattern = Pattern.compile("1st Killer");
    private final Pattern sumoEndPattern = Pattern.compile("1st -");
    private final Pattern sumoDeathPattern = Pattern.compile("You have gained");
    private final Pattern tntEndPattern = Pattern.compile("1st Place:");
    private final Pattern dropperEndPattern = Pattern.compile("You didn't finish!");
    private final Pattern copsEndPattern = Pattern.compile("Best Cop:");
    private final Pattern dropperFinishedPattern = Pattern.compile("You finished Map 5");
    private final Pattern swDeathPattern = Pattern.compile("You died! Want to play again");
    private final Pattern tntDeathPattern = Pattern.compile("You died! You can now spectate the game!");
    private final Pattern bwDeathPattern = Pattern.compile("You have been eliminated!");
    private final Pattern tagDeathPattern = Pattern.compile("You blew up!");

    private final Pattern killPatternOne = Pattern.compile("(\\w+) by (\\w+)\\.");
    private final Pattern killPatternTwo = Pattern.compile("(\\w+) to (\\w+)\\.");
    private final Pattern killPatternThree = Pattern.compile("(\\w+) of (\\w+)\\.");
    private final Pattern killPatternFour = Pattern.compile("(\\w+) on (\\w+)'s (\\w+)\\.");
    private final Pattern killPatternFive = Pattern.compile("(\\w+) for (\\w+)\\.");

    public static final Map<String, Integer> activeKillers = new HashMap<String, Integer>();
    public static IInventory currentChestInventory;
    public static ItemStack pet;
    public static String currentChestTitle;
    public static String autogg = "gg";

    public static int fpsX = 300;
    public static int fpsY = 5;
    public static int invX = 15;
    public static int invY = 5;
    public static int armX = 10;
    public static int armY = 45;
    public static int petX = 210;
    public static int petY = 45;
    public static int potX = 415;
    public static int potY = 320;
    public static int strX = 85;
    public static int strY = 100;
    public static int flowX = 10;
    public static int flowY = 200;

    public static int fpsColor = 0x00FF00;
    public static int strColor = 0x00FF00;
    public static int flowColor = 0xFFFFFF;
    public static int potColor = 0x00FF00;
    public static int highlightArmorColor = 0x80FF0000;
    public static int highlightWeaponColor = 0x800000FF;
    public static final int gridColor = 0xFFFFFFFF;

    private boolean autoggEnabled = false;
    private boolean showInventory = false;
    private boolean quickPlayAgain = false;
    private boolean showArmor = false;
    private boolean showStrength = false;
    private boolean showItemFlow = false;
    private boolean showActivePet = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(toggleF7Key);
        ClientRegistry.registerKeyBinding(toggleF8Key);
        ClientRegistry.registerKeyBinding(toggleF9Key);
        ClientRegistry.registerKeyBinding(toggleF10Key);
        ClientRegistry.registerKeyBinding(toggleInventoryKey);
        ClientRegistry.registerKeyBinding(toggleArmorKey);
        ClientRegistry.registerKeyBinding(toggleMenuKey);
        ClientRegistry.registerKeyBinding(toggleStrengthKey);
        ClientRegistry.registerKeyBinding(togglePlayAgain);
        ClientRegistry.registerKeyBinding(toggleAutoGG);
        ClientRegistry.registerKeyBinding(toggleItemFlow);
        ClientRegistry.registerKeyBinding(toggleActivePet);

        ClientCommandHandler.instance.registerCommand(new CmdF7());
        ClientCommandHandler.instance.registerCommand(new CmdF8());
        ClientCommandHandler.instance.registerCommand(new CmdF9());
        ClientCommandHandler.instance.registerCommand(new CmdF10());
        ClientCommandHandler.instance.registerCommand(new CmdAutogg());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleF7Key.isPressed() && cmdF7 != null) {
            MC.thePlayer.sendChatMessage(cmdF7);
        }
        if (toggleF8Key.isPressed() && cmdF8 != null) {
            MC.thePlayer.sendChatMessage(cmdF8);
        }
        if (toggleF9Key.isPressed() && cmdF9 != null) {
            MC.thePlayer.sendChatMessage(cmdF9);
        }
        if (toggleF10Key.isPressed() && cmdF10 != null) {
            MC.thePlayer.sendChatMessage(cmdF10);
        }
        if (toggleInventoryKey.isPressed()) {
            showInventory = !showInventory;
        }
        if (toggleArmorKey.isPressed()) {
            showArmor = !showArmor;
        }
        if (toggleStrengthKey.isPressed()) {
            showStrength = !showStrength;
        }
        if (togglePlayAgain.isPressed()) {
            quickPlayAgain = !quickPlayAgain;
        }
        if (toggleAutoGG.isPressed()) {
            autoggEnabled = !autoggEnabled;
        }
        if (toggleItemFlow.isPressed()) {
            showItemFlow = !showItemFlow;
        }
        if (toggleActivePet.isPressed()) {
            showActivePet = !showActivePet;
        }
        if (toggleMenuKey.isPressed()) {
            if (Minecraft.getMinecraft().currentScreen == null) {
                Minecraft.getMinecraft().displayGuiScreen(new ModMenu());
            } else if (Minecraft.getMinecraft().currentScreen instanceof ModMenu) {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        Matcher matcherOne = killPatternOne.matcher(message);
        Matcher matcherTwo = killPatternTwo.matcher(message);
        Matcher matcherThree = killPatternThree.matcher(message);
        Matcher matcherFour = killPatternFour.matcher(message);
        Matcher matcherFive = killPatternFive.matcher(message);
        Matcher matcherDuelOne = duelPatternOne.matcher(message);
        Matcher matcherDuelTwo = duelPatternTwo.matcher(message);
        Matcher matcherSwDeath = swDeathPattern.matcher(message);
        Matcher matcherBwDeath = bwDeathPattern.matcher(message);
        Matcher matcherTntDeath = tntDeathPattern.matcher(message);
        Matcher matcherTagDeath = tagDeathPattern.matcher(message);
        Matcher matcherEnd = endPattern.matcher(message);
        Matcher matcherTntEnd = tntEndPattern.matcher(message);
        Matcher matcherDropperEnd = dropperEndPattern.matcher(message);
        Matcher matcherDropperFinished = dropperFinishedPattern.matcher(message);
        Matcher matcherCopsEnd = copsEndPattern.matcher(message);
        Matcher matcherSumoEnd = sumoEndPattern.matcher(message);
        Matcher matcherSumoDeath = sumoDeathPattern.matcher(message);

        if (matcherDuelOne.find() && autoggEnabled) {
            MC.thePlayer.sendChatMessage(autogg);
        }
        if (matcherDuelTwo.find() && quickPlayAgain) {
            KeyBinding.onTick(MC.gameSettings.keyBindsHotbar[4].getKeyCode());
            KeyBinding.onTick(MC.gameSettings.keyBindUseItem.getKeyCode());
        }

        if (matcherSwDeath.find() && quickPlayAgain) {
            MC.thePlayer.sendChatMessage("/play solo_normal");
        } else if(matcherBwDeath.find() && quickPlayAgain){
            MC.thePlayer.sendChatMessage("/play bedwars_eight_one");
        } else if(matcherTagDeath.find() && quickPlayAgain){
            MC.thePlayer.sendChatMessage("/play tnt_tntag");
        } else if(matcherTntDeath.find() && quickPlayAgain){
            MC.thePlayer.sendChatMessage("/play tnt_tntrun");
        } else if(matcherDropperFinished.find() && quickPlayAgain){
            MC.thePlayer.sendChatMessage("/play arcade_dropper");
        } else if(matcherSumoDeath.find() && quickPlayAgain){
            KeyBinding.onTick(MC.gameSettings.keyBindsHotbar[7].getKeyCode());
            KeyBinding.onTick(MC.gameSettings.keyBindUseItem.getKeyCode());
        }

        if (matcherEnd.find() && autoggEnabled) {
            MC.thePlayer.sendChatMessage(autogg);
        } else if(matcherTntEnd.find() && autoggEnabled){
            MC.thePlayer.sendChatMessage(autogg);
        } else if(matcherDropperEnd.find() && autoggEnabled){
            MC.thePlayer.sendChatMessage(autogg);
        } else if(matcherSumoEnd.find() && autoggEnabled){
            MC.thePlayer.sendChatMessage(autogg);
        }

        if (matcherOne.find()) {
            String killer = matcherOne.group(2);
            activeKillers.put(killer, 100);
        } else if (matcherTwo.find()) {
            String killer = matcherTwo.group(2);
            activeKillers.put(killer, 100);
        } else if (matcherThree.find()) {
            String killer = matcherThree.group(2);
            activeKillers.put(killer, 100);
        } else if (matcherFour.find()) {
            String killer = matcherFour.group(2);
            activeKillers.put(killer, 100);
        } else if (matcherFive.find()) {
            String killer = matcherFive.group(2);
            activeKillers.put(killer, 100);
        }

        if (matcherCopsEnd.find() && autoggEnabled && quickPlayAgain){
            MC.thePlayer.sendChatMessage(autogg);
            MC.thePlayer.sendChatMessage("/play mcgo_normal");
        } else if(matcherCopsEnd.find() && autoggEnabled){
            MC.thePlayer.sendChatMessage(autogg);
        } else if(matcherCopsEnd.find() && quickPlayAgain){
            MC.thePlayer.sendChatMessage("/play mcgo_normal");
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<Map.Entry<String, Integer>> iterator = activeKillers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                int timeLeft = entry.getValue() - 1;
                if (timeLeft <= 0) {
                    iterator.remove();
                } else {
                    entry.setValue(timeLeft);
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.InitGuiEvent event) {
        if (event.gui instanceof GuiChest) {
            GuiChest chestGui = (GuiChest) event.gui;
            IInventory chestInventory = ((ContainerChest) chestGui.inventorySlots).getLowerChestInventory();
            currentChestTitle = chestInventory.getDisplayName().getUnformattedText();
            currentChestInventory = chestInventory;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        try {
            if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

            ItemStack pot = null;
            int count = 0;
            for (int i = 9; i < 45; i++) {
                ItemStack stack = MC.thePlayer.inventoryContainer.getInventory().get(i);
                if (stack != null && stack.getDisplayName().contains("Splash") && stack.getDisplayName().contains("Heal")) {
                    pot = stack;
                    count++;
                }
            }

            FontRenderer fontRenderer = MC.fontRendererObj;
            RenderItem renderItem = MC.getRenderItem();

            int fps = Minecraft.getDebugFPS();
            fontRenderer.drawString("[FPS] " + fps, fpsX, fpsY, fpsColor);

            if (showStrength){ ModHUD.displayStrength(fontRenderer); }
            if (showInventory){ ModHUD.displayInventory(renderItem); }
            if (showArmor){ ModHUD.displayArmor(renderItem); }
            if (showActivePet){ ModHUD.retrievePet(); }
            if (showItemFlow){
                ItemFlow.updateInventory();
                ItemFlow.renderItemChanges(fontRenderer);
            }

            if(showActivePet && pet != null){
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.enableDepth();
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.75f, 0.75f, 1.0f);
                renderItem.renderItemIntoGUI(pet, petX + 2, (petY + 10) * 2 - 18);
                GlStateManager.popMatrix();
                GlStateManager.disableDepth();
                RenderHelper.disableStandardItemLighting();
            }

            ModHUD.displayPot(count, pot, renderItem, fontRenderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
