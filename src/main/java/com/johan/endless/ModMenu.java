/*
 * ModMenu.java -
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
import net.minecraft.client.gui.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ModMenu extends GuiScreen {
    private static final List<Integer> WOOL_COLORS = Arrays.asList(
            0xFF000000, 0xFFFFFFFF, 0xFFFF0000, 0xFF0000FF, 0xFF00FF00, 0xFFA5FF00,
            0xFFA500FF, 0xFFFFA500, 0xFFFF00A5, 0xFF00A5FF, 0xFF00FFA5, 0xFF808080,
            0xFFFFD700, 0xFF8B4513, 0x80FF0000, 0x800000FF
    );

    private DraggableRectangle draggableRect;
    private String selectedElement = "FPS";
    private int selectedColorIndex = 0;
    private int selectedX = Endless.fpsX;
    private int selectedY = Endless.fpsY;
    private int rectX = 192;
    private int rectY = 108;
    private boolean isColorable = false;
    private boolean showColorText = false;

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(20, width / 2 - 65, height / 2 - 100, 140, 20, "[HUD Element] " + selectedElement));
        buttonList.add(new GuiButton(21, width / 2 - 50, height / 2 + 100, 100, 20, "Save"));

        draggableRect = new DraggableRectangle(0, 0, 4, 4);
        showColors();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 20) {
            cycleSelectedElement();
            button.displayString = "[HUD Element] " + selectedElement;
        } else if (button.id >= 100 && button.id < 116) {
            selectedColorIndex = button.id - 100;
            applySelectedColor();
        } else if (button.id == 21) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        } else if (button.id == 22) {
            selectedX += 10;
            applySelectedPosition();
        } else if (button.id == 23) {
            selectedX -= 10;
            applySelectedPosition();
        } else if (button.id == 24) {
            selectedY += 10;
            applySelectedPosition();
        } else if (button.id == 25) {
            selectedY -= 10;
            applySelectedPosition();
        }
    }

    private void cycleSelectedElement() {
        List<String> elements = Arrays.asList("FPS", "Strength", "Armor HL", "Weapon HL", "Inventory", "Armor", "Potions");
        int nextIndex = (elements.indexOf(selectedElement) + 1) % elements.size();
        selectedElement = elements.get(nextIndex);

        if (selectedElement.equals("FPS")) {
            isColorable = true;
            selectedX = Endless.fpsX;
            selectedY = Endless.fpsY;
        } else if (selectedElement.equals("Strength")) {
            isColorable = true;
            selectedX = Endless.strX;
            selectedY = Endless.strY;
        }else if (selectedElement.equals("Inventory")) {
            isColorable = false;
            selectedX = Endless.invX;
            selectedY = Endless.invY;
        } else if (selectedElement.equals("Armor")) {
            isColorable = false;
            selectedX = Endless.armX;
            selectedY = Endless.armY;
        } else if (selectedElement.equals("Potions")) {
            isColorable = true;
            selectedX = Endless.potX;
            selectedY = Endless.potY;
        } else if (selectedElement.equals("Armor HL")) {
            isColorable = true;
        } else if (selectedElement.equals("Weapon HL")) {
            isColorable = true;
        }

        if(isColorable){
            showColors();
        }

        if(!isColorable){
            hideColors();
        }
    }

    private void applySelectedColor() {
        int color = WOOL_COLORS.get(selectedColorIndex);
        if (selectedElement.equals("FPS")) {
            Endless.fpsColor = color;
        } else if(selectedElement.equals("Strength")){
            Endless.strColor = color;
        } else if(selectedElement.equals("Potions")){
            Endless.potColor = color;
        } else if(selectedElement.equals("Armor HL")){
            Endless.highlightArmorColor = color;
        } else if(selectedElement.equals("Weapon HL")){
            Endless.highlightWeaponColor = color;
        }
    }

    private void applySelectedPosition() {
        if (selectedElement.equals("FPS")) {
            Endless.fpsX = selectedX;
            Endless.fpsY = selectedY;
        } else if (selectedElement.equals("Strength")) {
            Endless.strX = selectedX;
            Endless.strY = selectedY;
        } else if (selectedElement.equals("Inventory")) {
            Endless.invX = selectedX;
            Endless.invY = selectedY;
        } else if (selectedElement.equals("Armor")) {
            Endless.armX = selectedX;
            Endless.armY = selectedY;
        } else if (selectedElement.equals("Potions")) {
            Endless.potX = selectedX;
            Endless.potY = selectedY;
        }
    }

    private void showColors(){
        showColorText = true;
        int xStart = width / 2 - 90;
        int yStart = height / 2 - 50;
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int color = WOOL_COLORS.get(index);
                buttonList.add(new ColorButton(100 + index, (xStart + col * 45)+15, (yStart + row * 25)+30, color));
                index++;
            }
        }
    }

    private void hideColors() {
        showColorText = false;
        for (int i = buttonList.size() - 1; i >= 0; i--) {
            GuiButton button = buttonList.get(i);
            if (button.id >= 100 && button.id < 116) {
                buttonList.remove(i);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        if (selectedElement.equals("FPS")) {
            rectX = 200;
            rectY = 113;
            drawRect(0, 0, rectX, rectY, 0x40A0A0A0);
        } else if (selectedElement.equals("Strength")) {
            rectX = 250;
            rectY = 125;
            drawRect(0, 0, rectX, rectY, 0x40A0A0A0);
        } else if (selectedElement.equals("Inventory")) {
            rectX = 116;
            rectY = 65;
            drawRect(0, 0, rectX, rectY, 0x40A0A0A0);
        } else if (selectedElement.equals("Armor")) {
            rectX = 272;
            rectY = 65;
            drawRect(0, 0, rectX, rectY, 0x40A0A0A0);
        } else if (selectedElement.equals("Potions")) {
            rectX = 208;
            rectY = 111;
            drawRect(0, 0, rectX, rectY, 0x40A0A0A0);
        }
        draggableRect.draw(mc);

        super.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(fontRendererObj, "[Screen Pos]", 40, 115, 0xFFFFFF);
        if (showColorText) {
            drawCenteredString(fontRendererObj, "[Color]", width / 2, 115, 0xFFFFFF);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        draggableRect.mousePressed(mouseX, mouseY);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        draggableRect.mouseDragged(mouseX, mouseY);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        draggableRect.mouseReleased();
    }


    private static class ColorButton extends GuiButton {
        private final int color;

        public ColorButton(int buttonId, int x, int y, int color) {
            super(buttonId, x, y, 16, 16, "");
            this.color = color;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, this.color);
            }
        }
    }

    private class DraggableRectangle {
        private int x, y, width, height;
        private boolean dragging = false;
        private int offsetX, offsetY;

        public DraggableRectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void draw(Minecraft mc) {
            drawRect(x, y, x + width, y + height, 0xFFFFFFFF);
        }

        public void mousePressed(int mouseX, int mouseY) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                dragging = true;
                offsetX = mouseX - x;
                offsetY = mouseY - y;
            }
        }

        public void mouseDragged(int mouseX, int mouseY) {
            if (dragging) {
                int newX = mouseX - offsetX;
                int newY = mouseY - offsetY;

                x = Math.max(0, Math.min(newX, rectX - width));
                y = Math.max(0, Math.min(newY, rectY - height));
                selectedX = x * 3;
                selectedY = y * 3;

                applySelectedPosition();
            }
        }
        public void mouseReleased() {
            dragging = false;
        }
    }
}
