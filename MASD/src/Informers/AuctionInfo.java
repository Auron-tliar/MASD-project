package Informers;

public class AuctionInfo {

	protected String questInfo;
	protected Integer startPrice;
	protected Integer minBid;
	protected Integer time;

	public AuctionInfo(String questInfo, Integer startPrice, Integer minBid, Integer time) {
		super();
		this.questInfo = questInfo;
		this.startPrice = startPrice;
		this.minBid = minBid;
		this.time = time;
	}

	public String getQuestInfo() {
		return questInfo;
	}

	public Integer getStartPrice() {
		return startPrice;
	}

	public Integer getMinBid() {
		return minBid;
	}

	public Integer getTime() {
		return time;
	}

}
