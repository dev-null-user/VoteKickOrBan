package bimo_power.votekickorban;

import bimo_power.votekickorban.bossbar.BossBar;
import bimo_power.votekickorban.gui.FormAlert;
import bimo_power.votekickorban.utils.DownloaderApi;
import bimo_power.votekickorban.utils.TextUtils;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;
import me.onebone.economyapi.EconomyAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends PluginBase implements Listener, BossBar.handle {

    private BossBar bossBar;
//    private FormAlert formAlert;
//    private FormWindowModadal formAlert;
    private FormAlert formAlert;

    private List<String> listPlayerNameYes = new ArrayList<>();
    public int playersYesCount = 0;

    private List<String> listPlayerNameNo = new ArrayList<>();
    public int playersNoCount = 0;

    private Player playerKick;
    private int playerFine = 0;
    private CommandSender playerSendKick;

    public int PLAYER_KICK_STATUS        = 1;
    public int PLAYER_BAN_STATUS         = 2;
    public int PLAYER_KICK_FINE_STATUS   = 3;
    public int PLAYER_BAN_IP_STATUS      = 4;

    private int current_status = 0;
    private String report_sample_msg  = "";
    private String report_msg         = "";
    private String bossBarTextLineOne = "";

    @Override
    public void onLoad() {
        super.onLoad();
        getLogger().info("Loading...");
        new DownloaderApi(this);

//        getServer().getCommandMap().register("vote",new VoteKickCommand());
//        getServer().getCommandMap().register("vote",new VoteBanCommand());

    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.saveDefaultConfig();
//        this.getConfig().save();
//        this.saveConfig();
        this.getServer().getPluginManager().registerEvents(this,this);
        new NukkitRunnable(){
            @Override
            public void run() {
                if(!getConfig().exists("messages")) {
                    getLogger().info("Create config...");
                    getConfig().set("messages.helps.0", ">>>>>> Список команд <<<<<<");
                    getConfig().set("messages.helps.1", "/vote - дать свой голос");
                    getConfig().set("messages.helps.2", "/vote kickFine <player> <fine> <message> - кикнуть игрока с указанием штрафа");
//            this.gfig().set("messages.helps.3", "/vote banTimer <player> <message> <timer: minutes> - забанить игрока с указанием время бана");
                    getConfig().set("messages.helps.4", "/vote kick <player> <message> - кикнуть игрока");
                    getConfig().set("messages.helps.5", "/vote ban <player> <message> - забанить игрока");
                    getConfig().set("messages.helps.6", "/vote stop - отменить голосование");

                    getConfig().set("messages.errors.err1", "Наберите команду /vote help");
                    getConfig().set("messages.errors.err2", "Вы уже дали свой голос!");
                    getConfig().set("messages.errors.err3", "Пока отсутствует голосование!");
                    getConfig().set("messages.errors.err4", "Отсутствует игрок!");
                    getConfig().set("messages.errors.err5", "Вы не можете кикнуть или забанить самого себя!");
                    getConfig().set("messages.errors.err6", "Вы не являетесь владельцем текущей голосовании!");
                    getConfig().set("messages.errors.err7", "Мало аргументов введите команду /vote help !");
                    getConfig().set("messages.errors.err8", "Игрок {playerKick} отключился с сервера!");
                    getConfig().set("messages.errors.err9", "Вы не можете проголосовать через консоль!");
                    getConfig().set("messages.errors.err10", "Аргумент {argument} должен быть цифрой!");
                    getConfig().set("messages.errors.err11", "Аргумент {argument} должен быть словом!");
                    getConfig().set("messages.errors.err12", "Голосование еще не завершено!");


                    getConfig().set("messages.info.stopVote", "#lГолосование было приостановлено!");
                    getConfig().set("messages.info.complete", "#lГолосование было завершено!");
                    getConfig().set("messages.info.sendVoteYes", "#lВы отправили свой голос (Подтверждение)!");
                    getConfig().set("messages.info.sendVoteNo", "#lВы отправили свой голос (Отклонение)!");

                    getConfig().set("messages.info.startKickOrBanVote", "#lГолосование было создано игроком #b{playerSendKick}#f!%n{msg}%nПричина: {reportMsg}");

                    getConfig().set("messages.info.backVote", "#lИгрок {playerKick} остался на сервере по голосованию!");
                    getConfig().set("messages.info.countVotes", "#lПроголосовали за изгнание: {countYes}%nПроголосовали за помилование: {countNo}%nВсего проголосовало: {countAll}");
                    getConfig().set("messages.info.backVoteEqual", "#lГолоса по итогу получились одиннаковыми!");
                    getConfig().set("messages.info.kickVote", "#lИгрок {playerKick} был кикнут из сервера по голосованию!");
                    getConfig().set("messages.info.kickFineVote", "#lИгрок {playerKick} был кикнут из сервера и был наложен штраф {fineMoney}$ по голосованию!");


                    getConfig().set("messages.info.banReportMsgVote", "#lВы были забанены по голосованию! %nПричина: {reportMsg}");
                    getConfig().set("messages.info.banVote", "#lИгрок {playerKick} был забанен!");

                    getConfig().set("messages.info.kickReportMsgVote", "Вы были кикнуты по голосованию! %nПричина: {reportMsg}");
                    getConfig().set("messages.info.kickFineReportMsgVote", "Вы были кикнуты по голосованию и оштрафованы на {fineMoney}$! %nПричина: {reportMsg}");
                    getConfig().set("messages.info.kickChatInfo", "#l#aИгрок {playerSendKick} хочет выгнать игрока {playerKick}");
                    getConfig().set("messages.info.kickChatInfoFine", "#l#aИгрок {playerSendKick} хочет выгнать игрока {playerKick} и хочет оштрафовать на {fineMoney}$");
                    getConfig().set("messages.info.banChatInfo", "#l#aИгрок {playerSendKick} хочет забанить игрока {playerKick}");


                    getConfig().set("messages.info.formAlert.title", "Выберите голос!");
                    getConfig().set("messages.info.formAlert.content", "Действие: {infoSender}%n%n{infoFine}%n%nПричина: {reportMsg}");
                    getConfig().set("messages.info.formAlert.contentExtraFine", "Будет оштрафован на {fineMoney}$");
                    getConfig().set("messages.info.formAlert.btnYes", "Да");
                    getConfig().set("messages.info.formAlert.btnNo", "Нет");

                    getConfig().set("messages.info.bossBar", "#lИгрок #e{playerSendKick}#f хочет выгнать игрока #c{playerKick}#f");
                    getConfig().set("messages.info.bossBarBan", "#lИгрок #e{playerSendKick}#f хочет забанить игрока #c{playerKick}#f");
                    getConfig().set("messages.info.bossBarVotes", "#l#e| Всего: {countAll} | Выгнать: {countYes} | Оставить {countNo} | Осталось: {countComplete} |");

                    getConfig().set("options.maxTimeComplete",      100);
                    getConfig().set("options.sounds.kick",          "random.hurt");
                    getConfig().set("options.sounds.noKick",        "random.levelup");
                    getConfig().set("options.sounds.equalVotes",    "random.totem");
                    getConfig().set("options.sounds.timerMim",      "random.toast");

                    saveConfig();
                }
            }
        }.run();

        formAlert = new FormAlert(
                this.getConfig().getString("messages.info.formAlert.title"),
                "",
                this.getConfig().getString("messages.info.formAlert.btnYes"),
                this.getConfig().getString("messages.info.formAlert.btnNo"));
        formAlert.setResponse(this.getFullName());
        bossBar = new BossBar(this);
        bossBar.handle = this;
        bossBar.setCurrentMax(getConfig().getInt("options.maxTimeComplete"));

        getLogger().info("Plugin Enable");

    }

    @Override
    public void onDisable() {
        super.onDisable();
        getLogger().info("Plugin Disable");
    }

    @Override
    public void onHideBossBar(boolean complete, String msg){
        getLogger().info("onHideBossBar");

        if(!complete){
            getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.info.stopVote"),null));
            resetTemp();
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("playerKick", playerKick.getName());
        map.put("countYes", String.valueOf(playersYesCount));
        map.put("countNo", String.valueOf(playersNoCount));
        map.put("countAll", String.valueOf(playersYesCount + playersNoCount));



        if(getServer().getPlayer(playerKick.getName()) == null) {
            getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.errors.err4"),map));
            resetTemp();
            return;
        }

        if (listPlayerNameYes.size() > listPlayerNameNo.size()) {
            getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.info.complete"),null));
            getServer().broadcastMessage(TextUtils.suffix(getConfig().getString("messages.info.countVotes"),map));
            if(PLAYER_KICK_STATUS == current_status){
                //kick
                getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.info.kickVote"),map));
                playerKick.kick(report_sample_msg);
            }
            else if(PLAYER_KICK_FINE_STATUS == current_status){
                //kick and fine
                map.put("fineMoney", String.valueOf(playerFine));
                getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.info.kickFineVote"),map));
                EconomyAPI.getInstance().reduceMoney(playerKick,playerFine);
                playerKick.kick(report_sample_msg);
                playerFine=0;
            }
            else if(PLAYER_BAN_STATUS == current_status){
                //ban by name
                getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.info.banVote"),map));
                playerKick.setBanned(true);
                playerKick.kick(report_sample_msg);
            }
//            else {
//                //ban by ip
//            }
        }
        else if (playersYesCount == playersNoCount){
            getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.info.backVoteEqual"),map));
            getServer().broadcastMessage(TextUtils.suffix(getConfig().getString("messages.info.countVotes"),map));
        }
        else{
            getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.info.backVote"),map));
            getServer().broadcastMessage(TextUtils.suffix(getConfig().getString("messages.info.countVotes"),map));
        }
        resetTemp();
    }

    private void resetTemp(){
        playersYesCount = 0;
        playersNoCount  = 0;
        playerFine      = 0;

        listPlayerNameNo  = new ArrayList<>();
        listPlayerNameYes = new ArrayList<>();
    }

    @Override
    public void onShowBossBar() {
        getLogger().info("onShowBossBar");
        Map<String,String> map = new HashMap<>();
        String msg = "";
        map.put("playerKick",playerKick.getName());
        if(current_status == PLAYER_KICK_STATUS) msg = TextUtils.suffix(this.getConfig().getString("messages.info.kickChatInfo"),map);
        else if(current_status == PLAYER_KICK_FINE_STATUS) {
            map.put("fineMoney",String.valueOf(playerFine));
            msg = TextUtils.suffix(this.getConfig().getString("messages.info.kickChatInfoFine"),map);
        }
        else if(current_status == PLAYER_BAN_STATUS) msg = TextUtils.suffix(this.getConfig().getString("messages.info.banChatInfo"),map);

        map.put("playerSendKick",playerSendKick instanceof Player ? playerSendKick.getName() : "Server");
        map.put("msg",msg);
        map.put("reportMsg", report_msg);
        getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.info.startKickOrBanVote"),map));
        sendSoundPlayers("note.pling");
    }

    @Override
    public void onHandleUpdateOnly() {
//        playersYesCount = listPlayerNameYes.size();
//        playersNoCount  = listPlayerNameNo.size();
    }

    @Override
    public String onProcessSelectPlayer(int current, Player p) {
        if(current < 10) {
            if (current == 0) {
                if (playersYesCount == playersNoCount) {
                    sendSoundPlayer(getConfig().getString("options.sounds.equalVotes"), p);
                }
                else if (playersYesCount > playersNoCount) {
                    sendSoundPlayer(getConfig().getString("options.sounds.kick"), p);

                }
                else {
                    sendSoundPlayer(getConfig().getString("options.sounds.noKick"), p);
                }
            } else sendSoundPlayer(getConfig().getString("options.sounds.timerMim"), p);
        }
        Map<String,String> map = new HashMap<>();

        map.put("countAll",String.valueOf(playersYesCount + playersNoCount));
        map.put("countYes",String.valueOf(playersYesCount));
        map.put("countNo",String.valueOf(playersNoCount));
        map.put("countComplete",String.valueOf(current));

        return TextUtils.suffix(this.getConfig().getString("messages.info.bossBarVotes"),map);
    }

    private boolean inOnListArr(List<String> arr, String find){
        for (String s : arr){
            if(s.equals(find)) return true;
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!label.equals("vote")){
            sender.sendMessage(this.getConfig().getString("messages.errors.err1"));
            return true;
        }

        if(args.length == 0) {
            if(!bossBar.isRunning){
                getServer().broadcastMessage(this.getConfig().getString("messages.errors.err3"));
                return true;
            }
            if(sender instanceof Player) {
                if(inOnListArr(listPlayerNameYes,sender.getName()) || inOnListArr(listPlayerNameNo,sender.getName())){
                    sender.sendMessage(this.getConfig().getString("messages.errors.err2"));
                    return true;
                }
                Map<String, String> map = new HashMap<>();

                map.put("infoSender", bossBarTextLineOne);
                map.put("reportMsg",  report_msg);
                if(playerFine > 0){
                    map.put("fineMoney", String.valueOf(playerFine));
                    map.put("infoFine",  TextUtils.suffix(this.getConfig().getString("messages.info.formAlert.contentExtraFine"),map));
                }
                else{
                    map.put("infoFine","");
                }


                formAlert.setContent(TextUtils.suffix(this.getConfig().getString("messages.info.formAlert.content"),map));
                new NukkitRunnable(){
                    @Override
                    public void run() {
                        Player pl = (Player) sender;
                        pl.showFormWindow(formAlert,formAlert.ID_WINDOW);
                    }
                }.run();
            }
            else getServer().broadcastMessage(this.getConfig().getString("messages.errors.err9"));

            return true;
        }

        switch (args[0]){
            case "help":
                sender.sendMessage(TextUtils.suffix(this.getConfig().getString("messages.helps.0"),null));
                sender.sendMessage(TextUtils.suffix(this.getConfig().getString("messages.helps.1"),null));
                sender.sendMessage(TextUtils.suffix(this.getConfig().getString("messages.helps.2"),null));
                sender.sendMessage(TextUtils.suffix(this.getConfig().getString("messages.helps.4"),null));
                sender.sendMessage(TextUtils.suffix(this.getConfig().getString("messages.helps.5"),null));
                sender.sendMessage(TextUtils.suffix(this.getConfig().getString("messages.helps.6"),null));
                break;

            case "kick":
                if(bossBar.isRunning) {
                    sender.sendMessage(this.getConfig().getString("messages.errors.err12"));
                    return true;
                }
                if(args.length < 3) {
                    sender.sendMessage(this.getConfig().getString("messages.errors.err7"));
                    return true;
                }
                report_sample_msg = "";

                playerSendKick = sender;
                playerKick     = getServer().getPlayer(args[1]);

                if(playerKick != null){
                    Map<String, String> map = new HashMap<>();

                    for(int i = 2; i<args.length;i++) report_sample_msg += " " +args[i];

                    report_sample_msg = report_sample_msg.trim();

                    map.put("reportMsg", report_sample_msg);
                    report_msg = report_sample_msg;
                    report_sample_msg = TextUtils.suffix(this.getConfig().getString("messages.info.kickReportMsgVote"),map);

                    if(playerSendKick instanceof Player && playerKick.getName().equals(((Player) playerSendKick).getName())){
                        sender.sendMessage(this.getConfig().getString("messages.errors.err5"));
                        return true;
                    }

                    map = new HashMap<>();
                    String nameSender = sender instanceof Player ? ((Player) playerSendKick).getName() : "Server";
                    map.put("playerSendKick", nameSender);
                    map.put("playerKick", playerKick.getName());

                    listPlayerNameNo.add(playerKick.getName());
                    playersNoCount++;

                    listPlayerNameYes.add(nameSender);
                    playersYesCount++;

                    current_status = PLAYER_KICK_STATUS;

                    bossBarTextLineOne = TextUtils.suffix(this.getConfig().getString("messages.info.bossBar"),map);

                    bossBar.showBar(bossBarTextLineOne);
                }
                else{
                    sender.sendMessage(this.getConfig().getString("messages.errors.err4"));
                    return true;
                }
                break;

            case "ban":
                if(bossBar.isRunning) {
                    sender.sendMessage(this.getConfig().getString("messages.errors.err12"));
                    return true;
                }
                if(args.length < 3) {
                    sender.sendMessage(this.getConfig().getString("messages.errors.err7"));
                    return true;
                }
                report_sample_msg = "";

                playerSendKick = sender;
                playerKick     = getServer().getPlayer(args[1]);

                if(playerKick != null){
                    Map<String, String> map = new HashMap<>();

                    for(int i = 2; i<args.length;i++) report_sample_msg += " " +args[i];

                    report_sample_msg = report_sample_msg.trim();

                    map.put("reportMsg", report_sample_msg);
                    report_msg = report_sample_msg;
                    report_sample_msg = TextUtils.suffix(this.getConfig().getString("messages.info.banReportMsgVote"),map);

                    if(playerSendKick instanceof Player && playerKick.getName().equals(((Player) playerSendKick).getName())){
                        sender.sendMessage(this.getConfig().getString("messages.errors.err5"));
                        return true;
                    }

                    map = new HashMap<>();
                    String nameSender = sender instanceof Player ? ((Player) playerSendKick).getName() : "Server";
                    map.put("playerSendKick", nameSender);
                    map.put("playerKick", playerKick.getName());

                    listPlayerNameNo.add(playerKick.getName());
                    playersNoCount++;

                    listPlayerNameYes.add(nameSender);
                    playersYesCount++;

                    current_status = PLAYER_BAN_STATUS;

                    bossBarTextLineOne = TextUtils.suffix(this.getConfig().getString("messages.info.bossBarBan"),map);

                    bossBar.showBar(bossBarTextLineOne);
                }
                else{
                    sender.sendMessage(this.getConfig().getString("messages.errors.err4"));
                    return true;
                }
                break;

            case "kickFine":
                if(bossBar.isRunning) {
                    sender.sendMessage(this.getConfig().getString("messages.errors.err12"));
                    return true;
                }

                if(args.length < 4) {
                    sender.sendMessage(this.getConfig().getString("messages.errors.err7"));
                    return true;
                }

                Map<String,String> map = new HashMap<>();

                int fineMoney = 0;
                try {
                    fineMoney = Integer.parseInt(args[2]);
                }
                catch (Exception e){
                    map.put("argument","fineMoney");
                    sender.sendMessage(TextUtils.suffix(getConfig().getString("messages.errors.err10"),map));
                    return true;
                }

                playerFine = fineMoney;

                report_sample_msg = "";

                playerSendKick = sender;
                playerKick     = getServer().getPlayer(args[1]);

                if(playerKick != null){
                    map = new HashMap<>();

                    for(int i = 3; i<args.length;i++) report_sample_msg += " " +args[i];

                    report_sample_msg = report_sample_msg.trim();

                    map.put("reportMsg", report_sample_msg);
                    map.put("fineMoney", String.valueOf(fineMoney));
                    report_msg = report_sample_msg;
                    report_sample_msg = TextUtils.suffix(this.getConfig().getString("messages.info.kickFineReportMsgVote"),map);

                    if(playerSendKick instanceof Player && playerKick.getName().equals(((Player) playerSendKick).getName())){
                        sender.sendMessage(this.getConfig().getString("messages.errors.err5"));
                        return true;
                    }

                    map = new HashMap<>();
                    String nameSender = sender instanceof Player ? ((Player) playerSendKick).getName() : "Server";
                    map.put("playerSendKick", nameSender);
                    map.put("playerKick", playerKick.getName());

                    listPlayerNameNo.add(playerKick.getName());
                    playersNoCount++;

                    listPlayerNameYes.add(nameSender);
                    playersYesCount++;

                    current_status = PLAYER_KICK_FINE_STATUS;

                    bossBarTextLineOne = TextUtils.suffix(this.getConfig().getString("messages.info.bossBar"),map);

                    bossBar.showBar(bossBarTextLineOne);
                }
                else{
                    sender.sendMessage(this.getConfig().getString("messages.errors.err4"));
                    return true;
                }

                break;

            case "stop":
                if(sender != playerSendKick) {
                    sender.sendMessage(this.getConfig().getString("messages.errors.err6"));
                    return true;
                }
                bossBar.setRunning(false);
//                sendSoundPlayers("random.click");
                break;
        }

        return true;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        bossBar.removePlayer(event.getPlayer());

        if(playerKick != null && playerKick.getName().equals(event.getPlayer().getName())){
            bossBar.setRunning(false);
            Map<String,String> map = new HashMap<>();
            map.put("playerKick",playerKick.getName());
            getServer().broadcastMessage(TextUtils.suffix(this.getConfig().getString("messages.errors.err8"),map));
        }
    }

    @Deprecated
    @EventHandler
    public void onPacketReceive(DataPacketReceiveEvent e) {
        if (e.getPacket() instanceof SetLocalPlayerAsInitializedPacket) {
            if(bossBar.isRunning){
                Player player = e.getPlayer();
                bossBar.addPlayer(player,player.createBossBar("-_-_-_-_-_-_-",bossBar.getCurrentMax()));
            }
        }
    }

    private void sendSoundPlayers(String sound){
        PlaySoundPacket pk = new PlaySoundPacket();

        pk.name = sound;
        pk.volume = 1;
        pk.pitch = 1;

        for (Player player : getServer().getOnlinePlayers().values()){
            pk.x = (int) player.x;
            pk.y = (int) player.y;
            pk.z = (int) player.z;
            player.dataPacket(pk);
        }
    }

    private void sendSoundPlayer(String sound, Player player){
        PlaySoundPacket pk = new PlaySoundPacket();

        pk.name = sound;
        pk.volume = 1;
        pk.pitch = 1;

        pk.x = (int) player.x;
        pk.y = (int) player.y;
        pk.z = (int) player.z;

        player.dataPacket(pk);
    }

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent e) {
        Player player = e.getPlayer();
        if(e.getWindow() instanceof FormWindowModal){
            if (e.getResponse() == null) return;
            if(e.getWindow().wasClosed()) return;

            FormResponseModal formResponseModal = (FormResponseModal) e.getWindow().getResponse();

            if(e.getFormID() == formAlert.ID_WINDOW){
                if(formResponseModal.getClickedButtonText().equals(this.getConfig().getString("messages.info.formAlert.btnYes"))){
                    playersYesCount++;
                    listPlayerNameYes.add(player.getName());
                }
                else if(formResponseModal.getClickedButtonText().equals(this.getConfig().getString("messages.info.formAlert.btnNo"))){
                    playersNoCount++;
                    listPlayerNameNo.add(player.getName());
                }
            }
        }
    }
}
