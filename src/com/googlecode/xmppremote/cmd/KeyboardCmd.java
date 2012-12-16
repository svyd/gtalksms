package com.googlecode.xmppremote.cmd;

import com.googlecode.xmppremote.KeyboardInputMethod;
import com.googlecode.xmppremote.MainService;
import com.googlecode.xmppremote.R;

public class KeyboardCmd extends CommandHandlerBase {
    public KeyboardCmd(MainService mainService) {
        super(mainService, CommandHandlerBase.TYPE_COPY, new Cmd("write", "w"));
    }
    
    @Override
    protected void execute(String cmd, String args) {
        KeyboardInputMethod keyboard = sMainService.getKeyboard();
        
        if (keyboard != null) {
            keyboard.setText(args);
        }
    }
    
    @Override
    protected void initializeSubCommands() {
        mCommandMap.get("write").setHelp(R.string.chat_help_write, "#text#");   
    }
}
