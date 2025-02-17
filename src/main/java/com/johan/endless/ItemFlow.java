/*
 * ItemFlow.java -
 *
 * Author: Johan Lebek
 * Created at: Mon Feb 17 15:35:00 CET 2025
 *
 * Copyright (C) 2025 Johan Lebek
 *
 * Licensed under the MIT License.
 * You may obtain a copy of the license at
 * https://opensource.org/licenses/MIT
 */

package com.johan.endless;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.util.*;

public class ItemFlow {

    private static final Map<String, Integer> previousInventory = new HashMap<String, Integer>();
    private static final List<ItemChange> addedItems = new ArrayList<ItemChange>();
    private static final List<ItemChange> removedItems = new ArrayList<ItemChange>();
    private static final int DISPLAY_DURATION = 20;

    public static void updateInventory() {
        Map<String, Integer> tempInventory = new HashMap<String, Integer>();

        for (int i = 9; i < 45; i++) {
            ItemStack stack = Endless.MC.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack != null && stack.getItem() != null) {
                String itemName = stack.getDisplayName();
                int itemCount = stack.stackSize;
                if (tempInventory.containsKey(itemName)) {
                    tempInventory.put(itemName, tempInventory.get(itemName) + itemCount);
                } else {
                    tempInventory.put(itemName, itemCount);
                }
            }
        }

        for (String item : tempInventory.keySet()) {
            int newCount = tempInventory.get(item);
            int oldCount = previousInventory.containsKey(item) ? previousInventory.get(item) : 0;

            if (newCount > oldCount) {
                displayItemChange(item, newCount - oldCount, true);
            } else if (newCount < oldCount) {
                displayItemChange(item, oldCount - newCount, false);
            }
        }

        for (String item : previousInventory.keySet()) {
            if (!tempInventory.containsKey(item)) {
                displayItemChange(item, previousInventory.get(item), false);
            }
        }
        previousInventory.clear();
        previousInventory.putAll(tempInventory);
    }

    private static void displayItemChange(String itemName, int count, boolean added) {
        int color = added ? 0x00FF00 : 0xFF0000;
        String changeMessage = (added ? "+" : "-") + itemName + " x" + count;
        ItemChange itemChange = new ItemChange(changeMessage, color, getCurrentTick());

        if (added) {
            addedItems.add(itemChange);
        } else {
            removedItems.add(itemChange);
        }
        cleanUpOldItems();
    }

    private static void cleanUpOldItems() {
        Iterator<ItemChange> iterator = addedItems.iterator();
        while (iterator.hasNext()) {
            ItemChange item = iterator.next();
            if (getCurrentTick() - item.tick > DISPLAY_DURATION) {
                iterator.remove();
            }
        }

        iterator = removedItems.iterator();
        while (iterator.hasNext()) {
            ItemChange item = iterator.next();
            if (getCurrentTick() - item.tick > DISPLAY_DURATION) {
                iterator.remove();
            }
        }
    }

    private static int getCurrentTick() {
        return (int) (System.nanoTime() / 1000000000L * 20);
    }

    private static class ItemChange {
        String message;
        int color;
        int tick;

        ItemChange(String message, int color, int tick) {
            this.message = message;
            this.color = color;
            this.tick = tick;
        }
    }

    public static void renderItemChanges(FontRenderer fontRenderer) {
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 1.0f);
        int xOffest = Endless.flowX;
        int yOffset = Endless.flowY;
        fontRenderer.drawString("[Items Flow]", xOffest, yOffset, Endless.flowColor);

        for (ItemChange change : addedItems) {
            fontRenderer.drawString(change.message, xOffest, yOffset + 10, change.color);
            yOffset += 10;
        }

        for (ItemChange change : removedItems) {
            fontRenderer.drawString(change.message, xOffest, yOffset + 10, change.color);
            yOffset += 10;
        }

        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
    }
}
