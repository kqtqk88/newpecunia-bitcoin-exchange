package com.newpecunia.net;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class JsonCodecTest {

	private String jsonOrder = "{\"price\": \"10000\", \"amount\": \"0.01\", \"type\": 1, \"id\": 4477442, \"datetime\": \"2013-06-27 15:41:41.177225\"}";
	private String jsonOrderList = "[{\"price\": \"10000.00\", \"amount\": \"0.01000000\", \"type\": 1, \"id\": 4477442, \"datetime\": \"2013-06-27 15:41:41\"}," +
									 "{\"price\": \"10000.00\", \"amount\": \"0.01000000\", \"type\": 1, \"id\": 4477442, \"datetime\": \"2013-06-27 15:41:41\"}]";
	
	@Test
	public void parseFromJson() throws JsonParsingException {
		TestDTO order = JsonCodec.INSTANCE.parseJson(jsonOrder, TestDTO.class);
		assertEquals(new BigDecimal("10000"), order.getPrice());
		assertEquals(new BigDecimal("0.01"), order.getAmount());
		assertEquals("4477442", order.getId());
		assertEquals("2013-06-27 15:41:41.177225", order.getDatetime());
	}
	
	@Test
	public void parseListFromJson() throws JsonParsingException {
		TestDTO[] orders = JsonCodec.INSTANCE.parseJson(jsonOrderList, TestDTO[].class);
		assertEquals(2, orders.length);
	}
}
