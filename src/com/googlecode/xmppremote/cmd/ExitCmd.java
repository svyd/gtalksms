package com.googlecode.xmppremote.cmd;

import com.googlecode.xmppremote.MainService;
import com.googlecode.xmppremote.R;


public class ExitCmd extends CommandHandlerBase {

    public ExitCmd(MainService mainService) {
        super(mainService, CommandHandlerBase.TYPE_SYSTEM, new Cmd("exit", "quit"));
    }

    @Override
    protected void execute(String cmd, String args) {
       sMainService.stopSelf();
    }

    @Override
    protected void initializeSubCommands() {
        mCommandMap.get("exit").setHelp(R.string.chat_help_stop, null);   
    }
}
