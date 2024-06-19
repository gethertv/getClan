package dev.gether.getclan.incognito;

import dev.gether.getclan.utils.NMSReflection;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Method;
import java.util.UUID;

public class IncognitoAddon {

    private boolean incognitoEnable = false;
    private Method isIncognitoMethod;
    private Method incognitoName;
    private Object userManager;
    public IncognitoAddon() {
        try {
            Class<?> getIncognitoClazz = NMSReflection.getClass("dev.gether.getincognito.GetIncognito");
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("getIncognito");

            if (getIncognitoClazz != null && getIncognitoClazz.isInstance(plugin)) {
                Object getIncognito = getIncognitoClazz.cast(plugin);
                incognitoEnable = true;

                Method getUserManagerMethod = getIncognito.getClass().getMethod("getUserManager");
                userManager = getUserManagerMethod.invoke(getIncognito);

                Class<?> userManagerClazz = NMSReflection.getClass("dev.gether.getincognito.user.UserManager");

                if (userManagerClazz.isInstance(userManager)) {
                    // method
                    isIncognitoMethod = userManagerClazz.getMethod("isIncognito", UUID.class);
                    incognitoName = userManagerClazz.getMethod("getIncognitoName", UUID.class);

                    MessageUtil.logMessage(ConsoleColor.GREEN, " ✔  Addon incognito ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isIncognito(UUID uuid) {
        if(!incognitoEnable) {
            return false;
        }
        try {
            return (boolean) isIncognitoMethod.invoke(userManager, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getIncognitoName(Player player) {
        if(!incognitoEnable) {
            return player.getName();
        }
        if(isIncognito(player.getUniqueId())) {
            try {
                return (String) incognitoName.invoke(userManager, player.getUniqueId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return player.getName();
    }


}
