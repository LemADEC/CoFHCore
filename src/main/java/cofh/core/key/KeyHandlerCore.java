package cofh.core.key;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class KeyHandlerCore {

	public static final KeyHandlerCore INSTANCE = new KeyHandlerCore();

	static TMap<String, IKeyBinding> clientBinds = new THashMap<>();
	static TMap<String, IKeyBinding> serverBinds = new THashMap<>();
	static ArrayList<IKeyBinding> keys = new ArrayList<>();

	public static boolean isKeyDown(int key) {

		return (key != 0 && key < 256) && (key < 0 ? Mouse.isButtonDown(key + 100) : Keyboard.isKeyDown(key));
	}

	public static boolean addClientKeyBind(IKeyBinding binding) {

		if (!clientBinds.containsKey(binding.getUUID())) {
			keys.add(binding);
			clientBinds.put(binding.getUUID(), binding);
			return true;
		}
		return false;
	}

	public static boolean addServerKeyBind(IKeyBinding binding) {

		if (!serverBinds.containsKey(binding.getUUID())) {
			serverBinds.put(binding.getUUID(), binding);
			return true;
		}
		return false;
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	@SideOnly (Side.CLIENT)
	public void handleKeyInputEvent(KeyInputEvent event) {

		for (IKeyBinding key : keys) {
			int press = key.getKey();
			if (press > 0 && isKeyDown(press)) {
				if (key.keyPressClient() && key.hasServerSide()) {
					PacketKey.sendToServer(key.getUUID());
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly (Side.CLIENT)
	public void handleMouseInputEvent(MouseInputEvent event) {

		for (IKeyBinding key : keys) {
			int press = key.getKey();
			if (press < 0 && isKeyDown(press)) {
				if (key.keyPressClient() && key.hasServerSide()) {
					PacketKey.sendToServer(key.getUUID());
				}
			}
		}
	}

}
