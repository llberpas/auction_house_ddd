package com.codesai.notifications.business.actions;

import com.codesai.notifications.business.actions.command.NotificationForDeclaredWinnerCommand;

public class NotificationForDeclaredWinnerAction implements Action<NotificationForDeclaredWinnerCommand> {

    public NotificationForDeclaredWinnerAction() {
    }

    @Override
    public void execute(NotificationForDeclaredWinnerCommand command) {
        System.out.println("Congratulations, " + command.bidderId + ". You've won the auction " + command.auctionId + ". Enjoy your shit!");
    }
}
