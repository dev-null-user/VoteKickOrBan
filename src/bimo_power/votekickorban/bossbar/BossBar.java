package bimo_power.votekickorban.bossbar;

import bimo_power.votekickorban.Main;
import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DummyBossBar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBar {
    private int current = 100, currentMax = 100,period=1;
    private Plugin plugin;
    public handle handle;

    public boolean isRunning = false;
    private Map<Player,Long> dbb = new HashMap<>();

    public BossBar(Plugin plugin){
        this.plugin = plugin;
    }

    public Map<Player, Long> getDbb() {
        return dbb;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getCurrentMax() {
        return currentMax;
    }

    public void setCurrentMax(int currentMax) {
        this.currentMax = currentMax;
    }

    public int getCurrent() {
        return current;
    }

    public void removePlayer(Player player){
        this.dbb.remove(player);
    }
    public void addPlayer(Player player,Long bossBarId){
        this.dbb.put(player,bossBarId);
    }

    public void showBar(String msg){
        handle.onShowBossBar();
        dbb = new HashMap<Player, Long>();
        Map<UUID, Player> players = plugin.getServer().getOnlinePlayers();
        final String[] msg_status = {"*****************"};
        if(players.size() == 0) return;
        if(!isRunning) isRunning = true;
        else return;
        new NukkitRunnable(){

            @Override
            public void run() {
                for (Player p : players.values()){
                    DummyBossBar.Builder builder = new DummyBossBar.Builder(p);
                    builder.color(BlockColor.BLUE_BLOCK_COLOR);
                    builder.text(msg+"\n\n"+ msg_status[0]);
                    builder.length(currentMax);
                    dbb.put(p,p.createBossBar(builder.build()));
                }
            }

        }.run();
        new NukkitRunnable(){
            @Override
            public void run() {
                handle.onHandleUpdateOnly();
                current--;
                if(current == 0 || !isRunning) {
                    this.cancel();
                    for (Map.Entry<Player, Long> p : dbb.entrySet()){
                        p.getKey().removeBossBar(p.getValue());
                        handle.onProcessSelectPlayer(current,p.getKey());
                    }
                    current = currentMax;
                    handle.onHideBossBar(isRunning,msg);
                    isRunning = false;
                    return;
                }
                for (Map.Entry<Player, Long> p : dbb.entrySet()){
                    msg_status[0] = handle.onProcessSelectPlayer(current,p.getKey());
                    p.getKey().updateBossBar(msg+"\n\n"+ msg_status[0],current,p.getValue());

                }
            }

        }.runTaskTimerAsynchronously(plugin,0,20 * period);

    }

    public interface handle{
        void onHideBossBar(boolean complete, String msg);
        void onShowBossBar();
        void onHandleUpdateOnly();
        String onProcessSelectPlayer(int current, Player player);
    }
}
