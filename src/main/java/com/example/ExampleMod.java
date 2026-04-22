package com.example; // Исправлено под твой путь

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ExampleMod implements ClientModInitializer { // Используем ClientModInitializer
    private static KeyBinding keyBinding;

    @Override
    public void onInitializeClient() { // Метод для клиента
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.spearswap.dash", 
            InputUtil.Type.KEYSYM, 
            GLFW.GLFW_KEY_Q, 
            "category.spearswap"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                while (keyBinding.wasPressed()) {
                    var inv = client.player.getInventory();
                    int oldSlot = inv.selectedSlot;
                    int spearSlot = -1;

                    for (int i = 0; i < 9; i++) {
                        if (inv.getStack(i).isOf(Items.TRIDENT)) {
                            spearSlot = i;
                            break;
                        }
                    }

                    if (spearSlot != -1) {
                        inv.selectedSlot = spearSlot;
                        Vec3d look = client.player.getRotationVec(1.0F);
                        client.player.setVelocity(look.x * 1.5, 0.2, look.z * 1.5);
                        
                        final int targetSlot = oldSlot;
                        new Thread(() -> {
                            try { Thread.sleep(400); } catch (Exception ignored) {}
                            client.execute(() -> inv.selectedSlot = targetSlot);
                        }).start();
                    }
                }
            }
        });
    }
}
