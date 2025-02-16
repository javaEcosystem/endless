/*
 * Endless.java -
 *
 * Author: Johan Lebek
 * Created at: Sun Feb 16 10:47:00 CET 2025
 *
 * Copyright (C) 2025 Johan Lebek
 *
 * Licensed under the MIT License.
 * You may obtain a copy of the license at
 * https://opensource.org/licenses/MIT
 */

package com.johan.endless;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
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

import static net.minecraft.client.gui.Gui.drawRect;

@Mod(modid = Endless.MODID, version = Endless.VERSION)
public class Endless {

    private final Minecraft MC = Minecraft.getMinecraft();
    public static final String MODID = "endless";
    public static final String VERSION = "1.3";

    public static String autogg = "gg";
    private final int gridColor = 0xFFFFFFFF;
    private final Map<String, Integer> activeKillers = new HashMap<String, Integer>();

    private final KeyBinding toggleInventoryKey = new KeyBinding("Toggle Inventory", Keyboard.KEY_RIGHT, "Endless");
    private final KeyBinding toggleArmorKey = new KeyBinding("Toggle Armor", Keyboard.KEY_LEFT, "Endless");
    private final KeyBinding toggleMenuKey = new KeyBinding("Toggle Menu", Keyboard.KEY_UP, "Endless");
    private final KeyBinding toggleStrengthKey = new KeyBinding("[sw] Toggle Active Buffed Players", Keyboard.KEY_DOWN, "Endless");
    private final KeyBinding togglePlayAgain = new KeyBinding("Toggle Auto-rq", Keyboard.KEY_INSERT, "Endless");
    private final KeyBinding toggleAutoGG = new KeyBinding("Toggle Auto-gg", Keyboard.KEY_RCONTROL, "Endless");

    private final Pattern duelPattern = Pattern.compile("WINNER!");
    private final Pattern endPattern = Pattern.compile("1st Killer");
    private final Pattern tntEndPattern = Pattern.compile("1st Place:");
    private final Pattern dropperEndPattern = Pattern.compile("#1");
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

    public static int fpsX = 300;
    public static int fpsY = 5;
    public static int invX = 5;
    public static int invY = 5;
    public static int armX = 10;
    public static int armY = 60;
    public static int potX = 415;
    public static int potY = 320;
    public static int strX = 5;
    public static int strY = 220;

    public static int fpsColor = 0x00FF00;
    public static int strColor = 0x00FF00;
    public static int potColor = 0x00FF00;
    public static int highlightArmorColor = 0x80FF0000;
    public static int highlightWeaponColor = 0x800000FF;

    private boolean autoggEnabled = true;
    private boolean showInventory = true;
    private boolean quickPlayAgain = true;
    private boolean showArmor = true;
    private boolean showStrength = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(toggleInventoryKey);
        ClientRegistry.registerKeyBinding(toggleArmorKey);
        ClientRegistry.registerKeyBinding(toggleMenuKey);
        ClientRegistry.registerKeyBinding(toggleStrengthKey);
        ClientRegistry.registerKeyBinding(togglePlayAgain);
        ClientRegistry.registerKeyBinding(toggleAutoGG);
        ClientCommandHandler.instance.registerCommand(new ModCommands());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
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
        Matcher matcherDuel = duelPattern.matcher(message);
        Matcher matcherSwDeath = swDeathPattern.matcher(message);
        Matcher matcherBwDeath = bwDeathPattern.matcher(message);
        Matcher matcherTntDeath = tntDeathPattern.matcher(message);
        Matcher matcherTagDeath = tagDeathPattern.matcher(message);
        Matcher matcherEnd = endPattern.matcher(message);
        Matcher matcherTntEnd = tntEndPattern.matcher(message);
        Matcher matcherDropperEnd = dropperEndPattern.matcher(message);
        Matcher matcherDropperFinished = dropperFinishedPattern.matcher(message);
        Matcher matcherCopsEnd = copsEndPattern.matcher(message);

        if (matcherDuel.find() && autoggEnabled && quickPlayAgain) {
            MC.thePlayer.sendChatMessage(autogg);
            KeyBinding.onTick(MC.gameSettings.keyBindsHotbar[4].getKeyCode());
            KeyBinding.onTick(MC.gameSettings.keyBindUseItem.getKeyCode());
        } else if(matcherDuel.find() && autoggEnabled){
            MC.thePlayer.sendChatMessage(autogg);
        } else if(matcherDuel.find() && quickPlayAgain){
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
        }

        if (matcherEnd.find() && autoggEnabled) {
            MC.thePlayer.sendChatMessage(autogg);
        } else if(matcherTntEnd.find() && autoggEnabled){
            MC.thePlayer.sendChatMessage(autogg);
        } else if(matcherDropperEnd.find() && autoggEnabled){
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
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        try {
            if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

            FontRenderer fontRenderer = MC.fontRendererObj;
            RenderItem renderItem = MC.getRenderItem();

            ItemStack pot = null;
            int count = 0;
            for (int i = 9; i < 45; i++) {
                ItemStack stack = MC.thePlayer.inventoryContainer.getInventory().get(i);
                if (stack != null && stack.getDisplayName().contains("Splash") && stack.getDisplayName().contains("Heal")) {
                    pot = stack;
                    count++;
                }
            }

            int fps = Minecraft.getDebugFPS();
            fontRenderer.drawString("[FPS] " + fps, fpsX, fpsY, fpsColor);

            if (showStrength){ displayStrength(fontRenderer); }
            if (showInventory){ displayInventory(renderItem); }
            if (showArmor){ displayArmor(renderItem); }

            displayPot(count, pot, renderItem, fontRenderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayStrength(FontRenderer f){
        int xOffest = strX;
        int yOffset = strY;
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 1.0f);
        f.drawString("[Active Strength]", xOffest, yOffset, strColor);
        for (Map.Entry<String, Integer> entry : activeKillers.entrySet()) {
            if(!entry.getKey().equals("it") && !entry.getKey().equals("mc")){
                String displayText = entry.getKey() + " - " + (entry.getValue() / 20) + "s";
                f.drawString(displayText, xOffest, yOffset+10, 0xFFFFFF);
                yOffset += 10;
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
    }

    private void displayInventory(RenderItem r) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 1.0f);

        int slotSize = 10;
        int columns = 9;
        int rows = 4;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = (invX + col * slotSize) * 2;
                int y = (invY + row * slotSize) * 2;

                drawRect(x, y, x + slotSize * 2, y + 1, gridColor);
                drawRect(x, y, x + 1, y + slotSize * 2, gridColor);
                drawRect(x + slotSize * 2 - 1, y, x + slotSize * 2, y + slotSize * 2, gridColor);
                drawRect(x, y + slotSize * 2 - 1, x + slotSize * 2, y + slotSize * 2, gridColor);
            }
        }
        for (int i = 9; i < 45; i++) {
            ItemStack stack = MC.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack != null) {

                if(stack.getDisplayName().contains("Skip")){
                    KeyBinding.onTick(MC.gameSettings.keyBindsHotbar[0].getKeyCode());
                    KeyBinding.onTick(MC.gameSettings.keyBindUseItem.getKeyCode());
                }

                int row = (i - 9) / columns;
                int col = (i - 9) % columns;
                int invStartX = invX;
                int invStartY = invY;
                int x = ((invStartX + col * slotSize) * 2)+2;
                int y = ((invStartY + row * slotSize) * 2)+2;
                boolean shouldHighlightArmor = false;
                boolean shouldHighlightWeapon = false;

                if (stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    ItemStack helmet = MC.thePlayer.inventoryContainer.getInventory().get(5);
                    ItemStack chestplate = MC.thePlayer.inventoryContainer.getInventory().get(6);
                    ItemStack leggings = MC.thePlayer.inventoryContainer.getInventory().get(7);
                    ItemStack boots = MC.thePlayer.inventoryContainer.getInventory().get(8);

                    shouldHighlightArmor =
                                    (helmet == null && armor.armorType == 0) ||
                                    (helmet != null && armor.armorType == 0 && (
                                                    (helmet.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Gold")) ||
                                                    (helmet.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Chain")) ||
                                                    (helmet.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Chain")) ||
                                                    (helmet.getDisplayName().contains("Chain") && stack.getDisplayName().contains("Iron")) ||
                                                    (helmet.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Iron")) ||
                                                    (helmet.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Iron")) ||
                                                    (helmet.getDisplayName().contains("Chain") && stack.getDisplayName().contains("Diamond")) ||
                                                    (helmet.getDisplayName().contains("Iron") && stack.getDisplayName().contains("Diamond")) ||
                                                    (helmet.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Diamond")) ||
                                                    (helmet.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Diamond"))
                                    )) ||

                                    (chestplate == null && armor.armorType == 1) ||
                                    (chestplate != null && armor.armorType == 1 && (
                                                    (chestplate.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Gold")) ||
                                                    (chestplate.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Chain")) ||
                                                    (chestplate.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Chain")) ||
                                                    (chestplate.getDisplayName().contains("Chain") && stack.getDisplayName().contains("Iron")) ||
                                                    (chestplate.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Iron")) ||
                                                    (chestplate.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Iron")) ||
                                                    (chestplate.getDisplayName().contains("Chain") && stack.getDisplayName().contains("Diamond")) ||
                                                    (chestplate.getDisplayName().contains("Iron") && stack.getDisplayName().contains("Diamond")) ||
                                                    (chestplate.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Diamond")) ||
                                                    (chestplate.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Diamond"))
                                    )) ||

                                    (leggings == null && armor.armorType == 2) ||
                                    (leggings != null && armor.armorType == 2 && (
                                                    (leggings.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Gold")) ||
                                                    (leggings.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Chain")) ||
                                                    (leggings.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Chain")) ||
                                                    (leggings.getDisplayName().contains("Chain") && stack.getDisplayName().contains("Iron")) ||
                                                    (leggings.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Iron")) ||
                                                    (leggings.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Iron")) ||
                                                    (leggings.getDisplayName().contains("Chain") && stack.getDisplayName().contains("Diamond")) ||
                                                    (leggings.getDisplayName().contains("Iron") && stack.getDisplayName().contains("Diamond")) ||
                                                    (leggings.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Diamond")) ||
                                                    (leggings.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Diamond"))
                                    )) ||

                                    (boots == null && armor.armorType == 3) ||
                                    (boots != null && armor.armorType == 3 && (
                                                    (boots.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Gold")) ||
                                                    (boots.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Chain")) ||
                                                    (boots.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Chain")) ||
                                                    (boots.getDisplayName().contains("Chain") && stack.getDisplayName().contains("Iron")) ||
                                                    (boots.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Iron")) ||
                                                    (boots.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Iron")) ||
                                                    (boots.getDisplayName().contains("Chain") && stack.getDisplayName().contains("Diamond")) ||
                                                    (boots.getDisplayName().contains("Iron") && stack.getDisplayName().contains("Diamond")) ||
                                                    (boots.getDisplayName().contains("Gold") && stack.getDisplayName().contains("Diamond")) ||
                                                    (boots.getDisplayName().contains("Leather") && stack.getDisplayName().contains("Diamond"))
                                    ));
                }
                ItemStack bestWeapon = null;
                for(int j=9; j<45; j++){
                    ItemStack w = MC.thePlayer.inventoryContainer.getInventory().get(j);
                    if (w != null && (w.getItem() instanceof ItemSword || w.getItem() instanceof ItemAxe)) {
                        if (bestWeapon == null || compareWeaponRarity(w, bestWeapon) > 0) {
                            bestWeapon = w;
                        }
                    }
                }
                if (bestWeapon == stack) {
                    shouldHighlightWeapon = true;
                }
                if (shouldHighlightArmor) { drawRect(x-1, y-1, (x + slotSize * 2)-3, (y + slotSize * 2)-3, highlightArmorColor); }
                if (shouldHighlightWeapon) {drawRect(x-1, y-1, (x + slotSize * 2)-3, (y + slotSize * 2)-3, highlightWeaponColor); }
                r.renderItemIntoGUI(stack, x, y);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    private void displayArmor(RenderItem r){
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 1.0f);

        int slotSize = 10;
        int rows = 4;
        for (int row = 0; row < rows; row++) {
            int x = armX;
            int y = (armY + row * slotSize)*2;

            drawRect(x, y, x + slotSize * 2, y + 1, gridColor);
            drawRect(x, y, x + 1, y + slotSize * 2, gridColor);
            drawRect(x + slotSize * 2 - 1, y, x + slotSize * 2, y + slotSize * 2, gridColor);
            drawRect(x, y + slotSize * 2 - 1, x + slotSize * 2, y + slotSize * 2, gridColor);
        }

        for (int i = 5; i < 9; i++) {
            ItemStack stack = MC.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack != null) {
                int armStartX = armX;
                int armStartY = armY;
                r.renderItemIntoGUI(stack, armStartX+2, ((armStartY+ slotSize)*2) + (i-6)*20 + 2);
                int maxDurability = stack.getMaxDamage();
                if (maxDurability > 0) {

                    int damage = stack.getItemDamage();
                    int durabilityLeft = maxDurability - damage;
                    int durabilityPercentage = (int) ((durabilityLeft / (float) maxDurability) * 100);
                    int x = armStartX + 23;
                    int y = ((armStartY + slotSize) * 2) + (i - 6) * 20 + 6;

                    if(durabilityPercentage>50){
                        MC.fontRendererObj.drawString(durabilityPercentage + "%", x, y, 0x00FF00);
                    } else if(durabilityPercentage>20){
                        MC.fontRendererObj.drawString(durabilityPercentage + "%", x, y, 0xFFA500);
                    } else {
                        MC.fontRendererObj.drawString(durabilityPercentage + "%", x, y, 0xFF0000);
                    }
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    private void displayPot(int counter, ItemStack p, RenderItem r, FontRenderer f){
        if (p == null){return;}
        if (p.getDisplayName().contains("Splash") && p.getDisplayName().contains("Heal")){
            f.drawString(String.valueOf(counter), potX+15, potY+5, potColor);
        }

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        r.renderItemIntoGUI(p, potX, potY);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    private int compareWeaponRarity(ItemStack a, ItemStack b) {
        double damageA = getWeaponDamage(a);
        double damageB = getWeaponDamage(b);
        return Double.compare(damageA, damageB);
    }

    private double getWeaponDamage(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0.0;
        List<String> tooltip = stack.getTooltip(MC.thePlayer, MC.gameSettings.advancedItemTooltips);
        String pattern = "(\\+\\d+(?:\\.\\d+)?) Attack Damage";

        for (String line : tooltip) {
            if (line.matches(".*" + pattern + ".*")) {
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    try {
                        return Double.parseDouble(m.group(1));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return 0.0;
    }
}
