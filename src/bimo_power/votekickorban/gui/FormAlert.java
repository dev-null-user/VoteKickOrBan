package bimo_power.votekickorban.gui;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowModal;

public class FormAlert extends FormWindowModal implements Listener {

    public int ID_WINDOW = 3424234;

    public FormAlert(String title, String content, String trueButtonText, String falseButtonText) {
        super(title, content, trueButtonText, falseButtonText);
    }



}
