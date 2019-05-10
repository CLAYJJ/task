package org.clay.task.util;

import javax.swing.*;
import java.net.URL;

public final class CommonUtils {

    public static ImageIcon createImageIcon(String path) {
        URL url = CommonUtils.class.getResource(path);
        if (url != null)
            return new ImageIcon(url);
        else
            return null;
    }
}
