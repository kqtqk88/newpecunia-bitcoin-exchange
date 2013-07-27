package com.newpecunia.bitstamp.webdriver.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.newpecunia.bitstamp.service.impl.BitstampCredentials;
import com.newpecunia.bitstamp.webdriver.BitstampSession;
import com.newpecunia.bitstamp.webdriver.BitstampWebdriverException;
import com.newpecunia.bitstamp.webdriver.InternationalWithdrawRequest;
import com.newpecunia.net.HttpReader;
import com.newpecunia.net.HttpReaderFactory;
import com.newpecunia.net.HttpReaderOutput;

public class BitstampSessionImpl implements BitstampSession {
	
	private static final int MINIMUM_DEPOSIT_LIMIT = 50; //minimum deposit in USD
	private static final int MAXIMUM_DEPOSIT_LIMIT = 1000000; //maximum deposit in USD

	private HttpReader httpReader;
	
	private BitstampSessionImpl() {}
	
	private String lastOpenedUrl = null; //for Referer header
	
	//package private
	static BitstampSessionImpl createSession(HttpReaderFactory httpReaderFactory, BitstampCredentials credentials) throws IOException, BitstampWebdriverException {
		BitstampSessionImpl session = new BitstampSessionImpl();
		
		session.httpReader = httpReaderFactory.createNewHttpSessionReader();
		
		session.login(credentials.getUsername(), credentials.getPassword());
		return session;
	}

	private void login(String username, String password) throws IOException, BitstampWebdriverException {
		//navigate to login page
		String loginPage = navigateToLoginPage();
		//parse login form
		Document pageDom = Jsoup.parse(loginPage);
		Element form = pageDom.getElementsByTag("form").get(0);
		LinkedHashMap<String, String> params = getHiddenParamsFromForm(form);
		//add parameters for username and password
		params.put("username", username);
		params.put("password", password);
		//perform login
		post(BitstampWebdriverConstants.LOGIN_URL, params);
	}

	private LinkedHashMap<String, String> getHiddenParamsFromForm(Element form) {
		LinkedHashMap<String, String> hiddenAttributes = new LinkedHashMap<>();
		for (Element input : form.getElementsByTag("input")) {
			if (input.attr("type").equalsIgnoreCase("hidden")) {
				String name = input.attr("name");
				String value = input.attr("value");
				hiddenAttributes.put(name,  value);				
			}
		}
		return hiddenAttributes;
	}

	private String navigateToLoginPage() throws IOException, BitstampWebdriverException {
		String url = BitstampWebdriverConstants.LOGIN_URL;
		HttpReaderOutput loginPageOutput = httpReader.getWithMetadata(url);
		verifyResultCode(loginPageOutput.getResultCode(), url);
		lastOpenedUrl = url;
		verifyPageContainsText(loginPageOutput.getOutput(), url,
				"<h1>Member Login</h1>", "id_username", "id_password", "login_form");
		return loginPageOutput.getOutput();
	}

	@Override
	public boolean isWaitingForDeposit() throws IOException, BitstampWebdriverException {
		String url = BitstampWebdriverConstants.DEPOSIT_URL;
		String page = get(url);
		verifyPageContainsText(page, url, "Deposit");
		if (page.toUpperCase().contains("WAITING FOR YOU TO SEND THE FUNDS...")) {
			return true;
		} else if (page.toUpperCase().contains("YOUR DEPOSIT REQUESTS")){
			return false;
		} else {
			throw new BitstampWebdriverException("Cannot determine state of deposit waiting.");
		}
	}
	
	@Override
	public void createInternationalUSDDeposit(int amount, String firstname, String surname, String comment) throws IOException, BitstampWebdriverException {
		if (amount < MINIMUM_DEPOSIT_LIMIT) {
			throw new BitstampWebdriverException("Deposit amount must be at lease "+MINIMUM_DEPOSIT_LIMIT+" USD.");
		}
		if (amount > MAXIMUM_DEPOSIT_LIMIT) {
			throw new BitstampWebdriverException("Deposit amount must be maximally "+MAXIMUM_DEPOSIT_LIMIT+" USD.");
		}
		
		//navigate to the international deposit page
		String url = BitstampWebdriverConstants.INTERNATIONAL_DEPOSIT_URL;
		String page = get(url);
		//verify whether deposit is possible - the Bitstamp is not waiting for another deposit.
		if (page.toUpperCase().contains("WAITING FOR YOU TO SEND THE FUNDS...")) {
			throw new BitstampWebdriverException("In status of waiting for a deposit. Either wait until the deposit is finished or cancel the request.");
		}
		verifyPageContainsText(page, url, "INTERNATIONAL WIRE TRANSFER");
		
		//parse international deposit form
		Document pageDom = Jsoup.parse(page);
		Element form = pageDom.getElementsByTag("form").get(0);
		LinkedHashMap<String, String> params = getHiddenParamsFromForm(form);
		//add parameters of the deposit form
		params.put("first_name", firstname);
		params.put("last_name", surname);
		params.put("amount", ""+amount);
		params.put("currency", "USD");
		params.put("comment", comment);
		//perform deposit
		post(url, params);
		
		//verify that Bitstamp is now waiting for deposit
		if (!isWaitingForDeposit()) {
			throw new BitstampWebdriverException("Some error ocurred while creating deposit - Bitstamp is NOT in status \"waiting for deposit\".");
		}
	}

	@Override
	public void cancelLastDeposit() throws IOException, BitstampWebdriverException {
		String url = BitstampWebdriverConstants.CANCEL_URL;
		String page = get(url);
		verifyPageContainsText(page, url, "YOUR DEPOSIT REQUESTS");
	}

	@Override
	public void createInternationalWithdraw(InternationalWithdrawRequest request) throws IOException, BitstampWebdriverException {
		//navigate to the international withdraw page
		String url = BitstampWebdriverConstants.INTERNATIONAL_WITHDRAW_URL;
		String page = get(url);
		verifyPageContainsText(page, url, "INTERNATIONAL WIRE TRANSFER", "Withdraw");
		
		//parse international withdraw form
		Document pageDom = Jsoup.parse(page);
		Element form = pageDom.getElementsByTag("form").get(0);
		LinkedHashMap<String, String> params = getHiddenParamsFromForm(form);
		//add parameters of the withdraw form
		params.put("name", request.getName());
		params.put("amount", ""+request.getAmount());
		params.put("address", request.getAddress());
		params.put("postal_code", request.getPostalCode());
		params.put("city", request.getCity());
		params.put("iban", request.getIban());
		params.put("bic", request.getBic());
		params.put("bank_name", request.getBankName());
		params.put("bank_address", request.getBankAddress());
		params.put("bank_postal_code", request.getBankPostalCode());
		params.put("bank_city", request.getBankCity());
		
		//perform withdraw
		post(url, params);		
	}
	
	private String get(String url) throws IOException, BitstampWebdriverException {
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Referer", lastOpenedUrl));		
		HttpReaderOutput output = httpReader.getWithMetadata(url, headers);
		verifyResultCode(output.getResultCode(), url);
		lastOpenedUrl = url;
		return output.getOutput();
	}
	
	private String post(String url, LinkedHashMap<String, String> params) throws IOException, BitstampWebdriverException {
		List<Header> headers = new ArrayList<>();
		if (lastOpenedUrl != null) {
			headers.add(new BasicHeader("Referer", lastOpenedUrl));
		}
		List<NameValuePair> paramList = new ArrayList<>(); 
		for (Entry<String, String> entry : params.entrySet()) {
			paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}		
		HttpReaderOutput result = httpReader.postWithMetadata(url, headers, paramList);
		verifyResultCode(result.getResultCode(), url);
		lastOpenedUrl = url;
		return result.getOutput();
	}
	
	private void verifyPageContainsText(String page, String pageUrl, String ... texts) throws BitstampWebdriverException {
		if (page == null) {throw new BitstampWebdriverException("Content of page "+pageUrl+" is null."); }
		String pageUpperCase = page.toUpperCase();
		for (String text : texts) {
			if (!pageUpperCase.contains(text.toUpperCase())) {
				throw new BitstampWebdriverException("Unable to open "+pageUrl+".");
			}
		}
	}
	
	private void verifyResultCode(int code, String pageUrl) throws BitstampWebdriverException {
		if (code >= 400) {
			throw new BitstampWebdriverException("Error "+code+" returned when opening page "+pageUrl+".");
		}
	}
	
}