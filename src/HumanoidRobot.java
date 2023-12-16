import java.awt.*;

public class HumanoidRobot {

    private final Robot robot;
    private final DisplayUtil displayUtil;

    {
        try {
            robot = new Robot();
            displayUtil = new DisplayUtil();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void holdKey(int keyID) {
        robot.keyPress(keyID);
    }

    public void unHoldKey(int keyID) {
        robot.keyRelease(keyID);
    }

    public void keyPress(int key, int delay) {
        robot.keyPress(key);
        robot.delay(delay);
        robot.keyRelease(key);
    }

    public void mouseMove(String mode, int offsetX, double steps) {
        int centerX = displayUtil.getDisplayCenter("X");
        int centerY = displayUtil.getDisplayCenter("Y");

        if (mode.equalsIgnoreCase("SMOOTH")) {
            int targetX = centerX - offsetX;
            int deltaX = (int) ((targetX - centerX) / steps);

            for (double i = 0; i < steps; i++) {
                centerX += deltaX;
                robot.mouseMove(centerX, centerY);
                robot.delay(1);
            }

            int returnDeltaX = (int) ((displayUtil.getDisplayCenter("X") - centerX) / steps);
            for (int i = 0; i < steps; i++) {
                centerX += returnDeltaX;
                robot.mouseMove(centerX, centerY);
                robot.delay(1);
            }
        } else {
            robot.mouseMove(centerX, centerY);
        }
    }
}
