package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Turn implements Serializable {
	protected static final Logger log = LoggerFactory.getLogger(Turn.class);
	private static final long serialVersionUID = 1L;
	protected String cardNumber;
	protected Integer cardId;
	protected Integer agreementId;
	protected String cardStartDt;
	protected String cardfinishDt;
	protected Double turnAmt;
	protected String monthDt;

	public Turn() {
		super();
	}

	public Turn(String cardNumber, Integer cardId, Integer agreementId, String cardStartDt, String cardfinishDt,
			Double turnAmt, String monthDt) {
		super();
		this.cardNumber = cardNumber;
		this.cardId = cardId;
		this.agreementId = agreementId;
		this.cardStartDt = cardStartDt;
		this.cardfinishDt = cardfinishDt;
		this.turnAmt = turnAmt;
		this.monthDt = monthDt;
	}

	public Turn(TransactionXCard trn, Double turnAmt, String monthDt) {
		super();
		this.cardNumber = trn.cardNumber;
		this.cardId = trn.cardId;
		this.agreementId = trn.agreementId;
		this.cardStartDt = trn.startDt;
		this.cardfinishDt = trn.finishDt;
		this.turnAmt = turnAmt;
		this.monthDt = monthDt;
	}

	public Turn(Turn turn) {
		super();
		this.cardNumber = turn.cardNumber;
		this.cardId = turn.cardId;
		this.agreementId = turn.agreementId;
		this.cardStartDt = turn.cardStartDt;
		this.cardfinishDt = turn.cardfinishDt;
		this.turnAmt = turn.turnAmt;
		this.monthDt = turn.monthDt;
	}

	public Turn(Turn turn, TransactionXCard trn) {
		this(turn);
		this.turnAmt += trn.transactionAmt;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	public String getCardStartDt() {
		return cardStartDt;
	}

	public void setCardStartDt(String startDt) {
		this.cardStartDt = startDt;
	}

	public String getCardFinishDt() {
		return cardfinishDt;
	}

	public void setCardFinishDt(String finishDt) {
		this.cardfinishDt = finishDt;
	}

	public Double getTurnAmt() {
		return turnAmt;
	}

	public void setTurnAmt(Double turnAmt) {
		this.turnAmt = turnAmt;
	}

	public String getMonthDt() {
		return monthDt;
	}

	public void setMonthDt(String monthDt) {
		this.monthDt = monthDt;
	}

	@Override
	public String toString() {
		return "cardNumber: " + cardNumber + ", cardId: " + cardId + ", agreementId: " + agreementId + ", cardStartDt: "
				+ cardStartDt + ", cardfinishDt: " + cardfinishDt + ", turnAmt: " + turnAmt + ", monthDt: " + monthDt;
	}
}
