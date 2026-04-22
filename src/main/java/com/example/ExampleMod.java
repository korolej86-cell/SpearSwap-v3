package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ExampleMod implements ModInitializer {
    private static KeyBinding keyBinding;

    @Override
    public void onInitialize() {
        // Регистрируем кнопку Q
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.spearswap.dash", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Q, "category.spearswap"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed() && client.player != null) {
                int oldSlot = client.player.getInventory().selectedSlot;
                int spearSlot = -1;

                // Ищем копьё (трезубец)
                for (int i = 0; i < 9; i++) {
                    if (client.player.getInventory().getStack(i).isOf(Items.TRIDENT)) {
                        spearSlot = i;
                        break;
                    }
                }

                if (spearSlot != -1) {
                    // Свап и рывок
                    client.player.getInventory().selectedSlot = spearSlot;
                    Vec3d look = client.player.getRotationVec(1.0F);
                    client.player.setVelocity(look.x * 1.5, 0.2, look.z * 1.5);
                    
                    // Возврат через 10 тиков (логика клиента)
                    final int finalOldSlot = oldSlot;
                    new Thread(() -> {
                        try { Thread.sleep(400); } catch (InterruptedException e) {}
                        client.execute(() -> client.player.getInventory().selectedSlot = finalOldSlot);
                    }).start();
                }
            }
        });
    }
}
