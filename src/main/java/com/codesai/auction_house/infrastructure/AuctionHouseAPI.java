package com.codesai.auction_house.infrastructure;

import com.codesai.auction_house.business.actions.commands.BidAuctionCommand;
import com.codesai.auction_house.business.actions.commands.CreateAuctionCommand;
import com.codesai.auction_house.business.actions.commands.RetrieveAuctionCommand;
import com.codesai.auction_house.business.model.auction.Auction;
import com.codesai.auction_house.business.model.auction.exceptions.AcutionNotFoundException;
import com.codesai.auction_house.business.model.auction.exceptions.AuctionException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.time.LocalDate;
import java.util.Optional;

import static com.codesai.auction_house.infrastructure.ActionFactory.*;
import static org.eclipse.jetty.http.HttpStatus.*;


public class AuctionHouseAPI {
    public static String createAuction(Request request, Response response) {
        var command = createAuctionCommandFrom(request.body());
        if (command.isPresent()) {
            var auctionId = createAuctionAction().execute(command.get());
            response.status(CREATED_201);
            response.header("Location", request.url() + "/" + auctionId);
            return auctionId;
        }
        response.status(422);
        return "The auction body is not well formed.";
    }

    public static String retrieveAuction(Request request, Response response) throws JSONException {
        try {
            Auction auction = retrieveAuctionAction().execute(new RetrieveAuctionCommand(auctionIdFrom(request)));
            response.header("Content-type", "application/json");
            response.status(OK_200);
            return createAuctionJsonFrom(auction);
        } catch (AcutionNotFoundException e) {
            response.status(NOT_FOUND_404);
            return "An auction with that id does not exists.";
        }
    }

    public static Object bidAuction(Request request, Response response) throws JSONException {
        try {
            bidAuctionAction().execute(new BidAuctionCommand(auctionIdFrom(request), amountFrom(request)));
            return createdOk(response);
        } catch (AuctionException e) {
            return anAuctionException(response, e);
        } catch (Exception e) {
            return anUnknownError(response, e);
        }
    }

    private static Object createdOk(Response response) {
        response.status(CREATED_201);
        return "OK";
    }

    private static String auctionIdFrom(Request request) {
        return request.params("id");
    }

    private static double amountFrom(Request request) {
        var body = request.body();
        var bodyAsJson = new Gson().fromJson(body, JsonObject.class);
        return bodyAsJson.get("amount").getAsDouble();
    }

    private static String anAuctionException(Response response, Exception e) throws JSONException {
        response.status(422);
        return aJsonErrorException(response, e);
    }

    private static String anUnknownError(Response response, Exception e) throws JSONException {
        response.status(500);
        return aJsonErrorException(response, e);
    }

    private static String aJsonErrorException(Response response, Exception e) throws JSONException {
        response.type("application/json");
        return new JSONObject()
                .put("name", e.getClass().getSimpleName())
                .put("description", e.getMessage())
                .toString();
    }

    private static String createAuctionJsonFrom(Auction auction) throws JSONException {
        return new JSONObject()
                .put("item", new JSONObject()
                        .put("name", auction.item.name)
                        .put("description", auction.item.description)
                )
                .put("initial_bid", auction.startingPrice.amount)
                .put("conquer_price", auction.conquerPrice.amount)
                .put("expiration_date", auction.expirationDate.toString())
                .put("minimum_overbidding_price", auction.minimumOverbiddingPrice.amount)
                .toString();
    }

    private static Optional<CreateAuctionCommand> createAuctionCommandFrom(String body) {
        try {
            var json = new Gson().fromJson(body, JsonObject.class);
            return Optional.of(new CreateAuctionCommand(
                    json.get("item").getAsJsonObject().get("name").getAsString(),
                    json.get("item").getAsJsonObject().get("description").getAsString(),
                    json.get("initial_bid").getAsDouble(),
                    json.get("conquer_price").getAsDouble(),
                    LocalDate.parse(json.get("expiration_date").getAsString()),
                    json.get("minimum_overbidding_price").getAsDouble()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
