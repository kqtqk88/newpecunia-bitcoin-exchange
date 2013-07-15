package com.petrsmid.bitexchange.bitstamp.impl.dto;

import org.joda.time.DateTime;

import com.petrsmid.bitexchange.bitstamp.Transaction;

public class TransactionMapper {
	
	public static Transaction mapTransactionDTO2Transaction(TransactionDTO dto) {
		Transaction tr = new Transaction();
		
		tr.setBtcAmount(dto.getAmount());
		tr.setBtcPrice(dto.getPrice());
		
		if (dto.getDate() != null) {
			Long unixDate = dto.getDate();
			DateTime dateTime = new DateTime(unixDate * 1000);			
			tr.setTimestamp(dateTime);
		}
		
		tr.setTransactionId(dto.getTid());
		
		return tr;
	}

}
