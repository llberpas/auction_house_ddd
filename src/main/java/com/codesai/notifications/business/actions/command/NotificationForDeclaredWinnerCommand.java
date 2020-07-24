package com.codesai.notifications.business.actions.command;

public class NotificationForDeclaredWinnerCommand implements Command {

    public final String bidderId;
    public final String auctionId;

    public NotificationForDeclaredWinnerCommand(String bidderId, String auctionId) {
        this.bidderId = bidderId;
        this.auctionId = auctionId;
    }
}
