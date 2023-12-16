import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class Core {

    private static boolean isEnabled = false;
    private static boolean hotkeyHandled = false;

    public static void main(String[] args) throws InterruptedException {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "Roblox");

        if (hwnd != null) {

            User32.INSTANCE.RegisterHotKey(null, 1, 0, KeyEvent.VK_R); //Auto Bunny-hop
            User32.INSTANCE.RegisterHotKey(null, 2, 0, KeyEvent.VK_I); //Extra Shutdown

            HumanoidRobot humanoid = new HumanoidRobot();

            while (true) {
                WinUser.MSG msg = new WinUser.MSG();
                while (User32.INSTANCE.PeekMessage(msg, null, 0, 0, 0x0001)) {
                    if (msg.message == WinUser.WM_HOTKEY) {
                        humanoid.keyPress(32, 7);

                        handleButton(humanoid, msg.wParam.intValue());
                        hotkeyHandled = true;
                    }
                }

                if (hotkeyHandled) {
                    hotkeyHandled = false;
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Roblox not found.\nRoblox не найден.", "FlessPe AutoBhop", JOptionPane.ERROR_MESSAGE, null);
            System.exit(0);
        }
    }

    private static void handleButton(HumanoidRobot humanoid, int keyID) throws InterruptedException {
        if (keyID == 1 && !hotkeyHandled) {
            isEnabled = !isEnabled;
            if (isEnabled) {

                humanoid.unHoldKey(65);
                humanoid.holdKey(68);
                humanoid.mouseMove("Smooth", -55, Math.E);

                Thread.sleep(25);

                humanoid.holdKey(65);
                humanoid.mouseMove("Smooth", 35, Math.E);
                humanoid.unHoldKey(68);


            }
        } else if (keyID == 2) {
            System.exit(0);
        }
    }
}

