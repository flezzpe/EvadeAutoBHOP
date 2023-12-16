import java.awt.*;

public class DisplayUtil {

    public int getDisplayCenter(String type) {
        return switch (type.toUpperCase()) {
            case "X" -> Toolkit.getDefaultToolkit().getScreenSize().width / 2;
            case "Y" -> Toolkit.getDefaultToolkit().getScreenSize().height / 2;
            default -> 0;
        };
    }

}
