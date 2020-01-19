package unit.bussines.actions;

import com.codesai.auction_house.business.Auction;
import com.codesai.auction_house.business.AuctionRepository;
import com.codesai.auction_house.business.actions.CreateAuctionAction;
import com.codesai.auction_house.business.actions.CreateAuctionCommand;
import com.codesai.auction_house.business.auction.Item;
import com.codesai.auction_house.business.generic.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CreateAuctionActionShould {

    private AuctionRepository auctionRepository = mock(AuctionRepository.class);
    private ArgumentCaptor<Auction> captor;

    @BeforeEach
    void setUp() {
        captor = ArgumentCaptor.forClass(Auction.class);
    }

    @Test public void
    create_an_auction() {
        var expectedAuction = givenAnAuction();
        var createAuctionCommand = new CreateAuctionCommand(
                expectedAuction.item.name,
                expectedAuction.item.description,
                expectedAuction.initialBid.amount,
                expectedAuction.conquerPrice.amount,
                expectedAuction.expirationDate,
                expectedAuction.minimumOverbiddingPrice.amount
        );

        new CreateAuctionAction(auctionRepository).execute(createAuctionCommand);

        verify(auctionRepository).save(this.captor.capture());
        assertIsTheSameAs(this.captor.getValue(), expectedAuction);
    }

    private Auction givenAnAuction() {
        return new Auction(
                    new Item("anyItem", "anyDescription"),
                    Money.money(10.5), Money.money(50),
                    LocalDate.now().plusDays(15),
                    Money.money(1)
            );
    }

    private void assertIsTheSameAs(Auction actualAuction, Auction expectedAuction) {
        assertThat(actualAuction.id).matches("[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}");
        assertThat(actualAuction.item).isEqualTo(expectedAuction.item);
        assertThat(actualAuction.initialBid).isEqualTo(expectedAuction.initialBid);
        assertThat(actualAuction.conquerPrice).isEqualTo(expectedAuction.conquerPrice);
        assertThat(actualAuction.expirationDate).isEqualTo(expectedAuction.expirationDate);
        assertThat(actualAuction.minimumOverbiddingPrice).isEqualTo(expectedAuction.minimumOverbiddingPrice);
    }


}
