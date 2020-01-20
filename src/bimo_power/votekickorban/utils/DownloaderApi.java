package bimo_power.votekickorban.utils;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;

public class DownloaderApi {
    public DownloaderApi(Plugin plugin){
        checkAndRun(plugin);
    }
    private void checkAndRun(Plugin plugin) {
        Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("EconomyAPI") != null && server.getPluginManager().getPlugin("EssentialsNK") != null) return;



        String EconomyAPI = server.getFilePath() + "/plugins/EconomyAPI.jar";
//        String EssentialsNK = server.getFilePath() + "/plugins/EssentialsNK.jar";

        if(server.getPluginManager().getPlugin("EconomyAPI") == null) {
            plugin.getLogger().error("EconomyAPI not found!");
            plugin.getLogger().info("Downloading API...");
            try {
                FileOutputStream fos = new FileOutputStream(EconomyAPI);
                fos.getChannel().transferFrom(Channels.newChannel(new URL("https://drive.google.com/uc?authuser=0&id=1x2INkZfLCs4teac882kEPEqhIOMDsMrv&export=download").openStream()), 0, Long.MAX_VALUE);
                fos.close();
            } catch (Exception e) {
                server.getLogger().logException(e);
                server.getPluginManager().disablePlugin(plugin);
                return;
            }

            plugin.getLogger().info("EconomyAPI downloaded successfully!");
        }
//        if(server.getPluginManager().getPlugin("EssentialsNK") == null) {
//            plugin.getLogger().error("EssentialsNK not found!");
//            plugin.getLogger().info("Downloading API...");
//            try {
//                FileOutputStream fos = new FileOutputStream(EssentialsNK);
//                fos.getChannel().transferFrom(Channels.newChannel(new URL("https://nukkitx.com/resources/essentialsnk.15/download?version=683").openStream()), 0, Long.MAX_VALUE);
//                fos.close();
//            } catch (Exception e) {
//                server.getLogger().logException(e);
//                server.getPluginManager().disablePlugin(plugin);
//                return;
//            }
//
//            plugin.getLogger().info("EssentialsNK downloaded successfully!");
//        }
        server.getPluginManager().loadPlugin(EconomyAPI);
//        server.getPluginManager().loadPlugin(EssentialsNK);
        server.getPluginManager().enablePlugin(server.getPluginManager().getPlugin("EconomyAPI"));
    }
}
