package com.googlecode.xmppremote.cmd;

import com.googlecode.xmppremote.MainService;

public class ToastCmd extends CommandHandlerBase {

    public ToastCmd(MainService mainService) {
        super(mainService, CommandHandlerBase.TYPE_INTERNAL, new Cmd("toast"));
        // TODO if your command needs references, init them here
    }

    protected void execute(String cmd, String args) {        
        if (!args.equals("")) {
            MainService.displayToast(args, null, false);
        }
    }
    
    @Override
    protected void initializeSubCommands() {
    }
}
