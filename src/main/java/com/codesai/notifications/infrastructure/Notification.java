package com.codesai.notifications.infrastructure;

import com.codesai.EventStore;
import com.codesai.notifications.business.actions.NotificationForDeclaredWinnerAction;
import com.codesai.notifications.business.actions.command.NotificationForDeclaredWinnerCommand;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Notification {

    public static void start() {
        System.out.println("starting reputation bounded context");
        EventStore.listen(Notification::processEvent);
    }

    public static void processEvent(String event) {
        System.out.println(String.format("Event received %s", event));
        var bodyAsJson = new Gson().fromJson(event, JsonObject.class);
        if (bodyAsJson.get("eventType").getAsString().equals("DeclareWinnerEvent")) {
            var command = new NotificationForDeclaredWinnerCommand(
                    bodyAsJson.get("winner").getAsString(),
                    bodyAsJson.get("auctionId").getAsString()
            );
            var action = new NotificationForDeclaredWinnerAction();
            action.execute(command);
        }
    }

}
