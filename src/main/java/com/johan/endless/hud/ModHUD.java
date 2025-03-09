/*
 * ModHUD.java -
 *
 * Author: Johan Lebek
 * Created at: Tue Feb 18 23:33:00 CET 2025
 *
 * Copyright (C) 2025 Johan Lebek
 *
 * Licensed under the MIT License.
 * You may obtain a copy of the license at
 * https://opensource.org/licenses/MIT
 */

package com.johan.endless.hud;

import com.johan.endless.Endless;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.client.gui.Gui.drawRect;

public class ModHUD {

    public static void displayStrength(FontRenderer f){
        int xOffest = Endless.strX;
        int yOffset = Endless.strY;
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 1.0f);
        f.drawString("[Buffed Players]", xOffest, yOffset, Endless.strColor);
        for (Map.Entry<String, Integer> entry : Endless.activeKillers.entrySet()) {
            if(!entry.getKey().equals("it") && !entry.getKey().equals("mc")){
                String displayText = entry.getKey() + " - " + (entry.getValue() / 20) + "s";
                f.drawString(displayText, xOffest, yOffset+10, 0xFFFFFF);
                yOffset += 10;
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
    }

    public static void displayInventory(RenderItem r) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 1.0f);

        int slotSize = 10;
        int columns = 9;
        int rows = 4;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = (Endless.invX + col * slotSize) * 2;
                int y = (Endless.invY + row * slotSize) * 2;

                drawRect(x, y, x + slotSize * 2, y + 1, Endless.gridColor);
                drawRect(x, y, x + 1, y + slotSize * 2, Endless.gridColor);
                drawRect(x + slotSize * 2 - 1, y, x + slotSize * 2, y + slotSize * 2, Endless.gridColor);
                drawRect(x, y + slotSize * 2 - 1, x + slotSize * 2, y + slotSize * 2, Endless.gridColor);
            }
        }
        for (int i = 9; i < 45; i++) {
            ItemStack stack = Endless.MC.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack != null) {

                if(stack.getDisplayName().contains("Skip")){
                    KeyBinding.onTick(Endless.MC.gameSettings.keyBindsHotbar[0].getKeyCode());
                    KeyBinding.onTick(Endless.MC.gameSettings.keyBindUseItem.getKeyCode());
                }

                int row = (i - 9) / columns;
                int col = (i - 9) % columns;
                int invStartX = Endless.invX;
                int invStartY = Endless.invY;
                int x = ((invStartX + col * slotSize) * 2)+2;
                int y = ((invStartY + row * slotSize) * 2)+2;
                boolean shouldHighlightArmor = false;
                boolean shouldHighlightWeapon = false;

                if (stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    ItemStack helmet = Endless.MC.thePlayer.inventoryContainer.getInventory().get(5);
                    ItemStack chestplate = Endless.MC.thePlayer.inventoryContainer.getInventory().get(6);
                    ItemStack leggings = Endless.MC.thePlayer.inventoryContainer.getInventory().get(7);
                    ItemStack boots = Endless.MC.thePlayer.inventoryContainer.getInventory().get(8);

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
                    ItemStack w = Endless.MC.thePlayer.inventoryContainer.getInventory().get(j);
                    if (w != null && (w.getItem() instanceof ItemSword || w.getItem() instanceof ItemAxe)) {
                        if (bestWeapon == null || compareWeaponRarity(w, bestWeapon) > 0) {
                            bestWeapon = w;
                        }
                    }
                }
                if (bestWeapon == stack) {
                    shouldHighlightWeapon = true;
                }
                if (shouldHighlightArmor) { drawRect(x-1, y-1, (x + slotSize * 2)-3, (y + slotSize * 2)-3, Endless.highlightArmorColor); }
                if (shouldHighlightWeapon) {drawRect(x-1, y-1, (x + slotSize * 2)-3, (y + slotSize * 2)-3, Endless.highlightWeaponColor); }
                r.renderItemIntoGUI(stack, x, y);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    public static void displayArmor(RenderItem r){
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 1.0f);

        int slotSize = 10;
        int rows = 4;
        for (int row = 0; row < rows; row++) {
            int x = Endless.armX;
            int y = (Endless.armY + row * slotSize)*2;

            drawRect(x, y, x + slotSize * 2, y + 1, Endless.gridColor);
            drawRect(x, y, x + 1, y + slotSize * 2, Endless.gridColor);
            drawRect(x + slotSize * 2 - 1, y, x + slotSize * 2, y + slotSize * 2, Endless.gridColor);
            drawRect(x, y + slotSize * 2 - 1, x + slotSize * 2, y + slotSize * 2, Endless.gridColor);
        }

        for (int i = 5; i < 9; i++) {
            ItemStack stack = Endless.MC.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack != null) {
                int armStartX = Endless.armX;
                int armStartY = Endless.armY;
                r.renderItemIntoGUI(stack, armStartX+2, ((armStartY+ slotSize)*2) + (i-6)*20 + 2);
                int maxDurability = stack.getMaxDamage();
                if (maxDurability > 0) {

                    int damage = stack.getItemDamage();
                    int durabilityLeft = maxDurability - damage;
                    int durabilityPercentage = (int) ((durabilityLeft / (float) maxDurability) * 100);
                    int x = armStartX + 23;
                    int y = ((armStartY + slotSize) * 2) + (i - 6) * 20 + 6;

                    if(durabilityPercentage>50){
                        Endless.MC.fontRendererObj.drawString(durabilityPercentage + "%", x, y, 0x00FF00);
                    } else if(durabilityPercentage>20){
                        Endless.MC.fontRendererObj.drawString(durabilityPercentage + "%", x, y, 0xFFA500);
                    } else {
                        Endless.MC.fontRendererObj.drawString(durabilityPercentage + "%", x, y, 0xFF0000);
                    }
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    public static void displayPot(int counter, ItemStack p, RenderItem r, FontRenderer f){
        if (p == null){return;}
        if (p.getDisplayName().contains("Splash") && p.getDisplayName().contains("Heal")){
            f.drawString(String.valueOf(counter), Endless.potX+15, Endless.potY+5, Endless.potColor);
        }

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        r.renderItemIntoGUI(p, Endless.potX, Endless.potY);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    private static int compareWeaponRarity(ItemStack a, ItemStack b) {
        double damageA = getWeaponDamage(a);
        double damageB = getWeaponDamage(b);
        return Double.compare(damageA, damageB);
    }

    private static double getWeaponDamage(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0.0;
        List<String> tooltip = stack.getTooltip(Endless.MC.thePlayer, Endless.MC.gameSettings.advancedItemTooltips);
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

    public static void retrievePet() {
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 1.0f);

        int slotSize = 10;
        int x = Endless.petX;
        int y = Endless.petY * 2;

        drawRect(x, y, x + slotSize * 2, y + 1, Endless.gridColor);
        drawRect(x, y, x + 1, y + slotSize * 2, Endless.gridColor);
        drawRect(x + slotSize * 2 - 1, y, x + slotSize * 2, y + slotSize * 2, Endless.gridColor);
        drawRect(x, y + slotSize * 2 - 1, x + slotSize * 2, y + slotSize * 2, Endless.gridColor);

        GlStateManager.popMatrix();
        GlStateManager.disableDepth();

        if (!"Pets".equals(Endless.currentChestTitle)) return;
        int[][] slotRanges = {{10, 16}, {19, 25}, {28, 34}, {37, 43}};
        for (int[] range : slotRanges) {
            findAndUpdatePet(range[0], range[1]);
        }
    }

    private static void findAndUpdatePet(int startSlot, int endSlot) {
        if (Endless.currentChestInventory == null) return;

        for (int i = startSlot; i < endSlot; i++) {
            ItemStack stack = Endless.currentChestInventory.getStackInSlot(i);
            if (stack==null) continue;

            List<String> tooltip = stack.getTooltip(Endless.MC.thePlayer, Endless.MC.gameSettings.advancedItemTooltips);
            for (String line : tooltip) {
                if (line.contains("Click to despawn!")) {
                    Endless.pet = stack;
                    return;
                }
            }
        }
    }
}
