package vbmod.probe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.fabricmc.api.ClientModInitializer;

/** Reflection-only verification probe: works on every supported Minecraft version. */
public class Probe implements ClientModInitializer {
    private static final String TAG = "[probe] ";

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Class<?> firstClass(String... names) throws ClassNotFoundException {
        for (String name : names) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ignored) {
                // try next
            }
        }
        throw new ClassNotFoundException(String.join(",", names));
    }

    private static Method firstMethod(Class<?> cls, String... names) {
        for (String name : names) {
            for (Method m : cls.getDeclaredMethods()) {
                if (m.getName().equals(name)) {
                    m.setAccessible(true);
                    return m;
                }
            }
        }
        return null;
    }

    private static Object fieldValue(Object target, String... names) throws ReflectiveOperationException {
        for (String name : names) {
            Class<?> cls = target.getClass();
            while (cls != null) {
                try {
                    Field f = cls.getDeclaredField(name);
                    f.setAccessible(true);
                    return f.get(target);
                } catch (NoSuchFieldException e) {
                    cls = cls.getSuperclass();
                }
            }
        }
        throw new NoSuchFieldException(names[0]);
    }

    private static Object singleton(Class<?> cls) throws ReflectiveOperationException {
        Method m = firstMethod(cls, "getInstance", "method_1551");
        if (m == null) {
            throw new NoSuchMethodException("getInstance");
        }
        m.setAccessible(true);
        return m.invoke(null);
    }

    private static void report() {
        try {
            Class<?> mcCls = firstClass("net.minecraft.client.Minecraft", "net.minecraft.class_310");
            Object mc = singleton(mcCls);
            Object options = fieldValue(mc, "options", "field_1690");

            Method bobView = firstMethod(options.getClass(), "bobView", "method_42448");
            if (bobView == null) {
                System.out.println(TAG + "NO bobView accessor on options");
                return;
            }
            bobView.setAccessible(true);
            Object bobbing = bobView.invoke(options);

            Class<?> video = firstClass(
                "net.minecraft.client.gui.screens.options.VideoSettingsScreen",
                "net.minecraft.class_446");
            String videoMethod = null;
            Object[] videoArray = null;
            for (String name : new String[]{"displayOptions", "method_75373", "method_41846"}) {
                Method m = firstMethod(video, name);
                if (m != null && m.getParameterCount() == 1) {
                    m.setAccessible(true);
                    videoArray = (Object[]) m.invoke(null, options);
                    videoMethod = name;
                    break;
                }
            }
            if (videoArray == null) {
                System.out.println(TAG + "NO video options builder found");
            } else {
                System.out.println(TAG + "video=" + videoMethod
                    + " len=" + videoArray.length
                    + " idx0=" + (videoArray[0] == bobbing)
                    + " idx1=" + (videoArray[1] == bobbing));
            }

            Class<?> access = firstClass(
                "net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen",
                "net.minecraft.class_4189");
            Method am = firstMethod(access, "options", "method_41839");
            if (am == null) {
                System.out.println(TAG + "NO accessibility builder found");
            } else {
                am.setAccessible(true);
                Object[] arr = (Object[]) am.invoke(null, options);
                boolean found = false;
                for (Object o : arr) {
                    if (o == bobbing) {
                        found = true;
                    }
                }
                System.out.println(TAG + "accessibility len=" + arr.length + " contains=" + found);
            }
        } catch (Throwable t) {
            System.out.println(TAG + "probe failed: " + t);
        }
    }

    @SuppressWarnings("unchecked")
    private static void dumpSodium() {
        try {
            Class<?> manager = Class.forName("net.caffeinemc.mods.sodium.client.config.ConfigManager");
            Object config = manager.getField("CONFIG").get(null);
            for (Object mod : (java.util.List<Object>) fieldValue(config, "modOptions")) {
                for (Object page : (java.util.List<Object>) fieldValue(mod, "pages")) {
                    Object pageName = page.getClass().getMethod("name").invoke(page);
                    String name = pageName instanceof net.minecraft.network.chat.Component c ? c.getString() : String.valueOf(pageName);
                    System.out.println(TAG + "sodium page=" + name);
                    for (Object group : (java.util.List<Object>) page.getClass().getMethod("groups").invoke(page)) {
                        for (Object option : (java.util.List<Object>) fieldValue(group, "options")) {
                            Object n = fieldValue(option, "name");
                            if (n instanceof net.minecraft.network.chat.Component c) {
                                System.out.println(TAG + "  sodium option=" + c.getString());
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println(TAG + "sodium config api absent");
        } catch (Throwable t) {
            System.out.println(TAG + "sodium dump failed: " + t);
        }
    }

    @Override
    public void onInitializeClient() {
        Thread thread = new Thread(() -> {
            sleep(20000);
            try {
                Class<?> mcCls = firstClass("net.minecraft.client.Minecraft", "net.minecraft.class_310");
                Object mc = singleton(mcCls);
                Method execute = firstMethod(mcCls, "execute", "method_18859");
                if (execute != null) {
                    execute.setAccessible(true);
                    execute.invoke(mc, (Runnable) () -> { report(); dumpSodium(); });
                    return;
                }
            } catch (Throwable ignored) {
                // fall through to direct call
            }
            report();
        }, "vbprobe");
        thread.setDaemon(true);
        thread.start();
    }
}
