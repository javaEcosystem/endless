/*
 * ItemFlow.java -
 *
 * Author: Johan Lebek
 * Created at: Mon Feb 17 18:02:00 CET 2025
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
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ItemFlow {

    private static final Map<String, Integer> previousInventory = new HashMap<String, Integer>();
    private static final List<ItemChange> addedItems = new ArrayList<ItemChange>();
    private static final List<ItemChange> removedItems = new ArrayList<ItemChange>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
        fontRenderer.drawString("[Item Flow]", xOffest, yOffset, Endless.flowColor);

        for (final ItemChange change : addedItems) {
            fontRenderer.drawString(change.message, xOffest, yOffset + 10, change.color);
            yOffset += 10;
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    addedItems.remove(change);
                }
            }, 2, TimeUnit.SECONDS);
        }

        for (final ItemChange change : removedItems) {
            fontRenderer.drawString(change.message, xOffest, yOffset + 10, change.color);
            yOffset += 10;
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    removedItems.remove(change);
                }
            }, 2, TimeUnit.SECONDS);
        }
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
    }
}
