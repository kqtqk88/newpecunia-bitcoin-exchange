package com.newpecunia.configuration;

import java.math.BigDecimal;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.newpecunia.NPException;

public class NPConfiguration {
	
	private static final String CONFIG_FILENAME = "npconfig.xml";
	
	public NPConfiguration() {
		reloadConfiguration();
	}
	
	private Configuration config = null;
	
	public void reloadConfiguration() {
		try {
			config = new XMLConfiguration(getClass().getResource(CONFIG_FILENAME));
		} catch (ConfigurationException e) {
			throw new NPException("Cannot load config file.", e);
		}		
	}

	public String getCredentialsPath() {
		return config.getString("credentialsPath");
	}
	
	public String getWebdavEncoding() {
		return config.getString("unicredit.webdav.fileencoding");
	}

	public String getWebdavBaseFolder() {
		return config.getString("unicredit.webdav.baseurl");
	}

	public String getWebdavForeignUploadFolder() {
		return config.getString("unicredit.webdav.upload.foreignfolder");
	}

	public String getWebdavStatusFolder() {
		return config.getString("unicredit.webdav.statusfolder");
	}

	/**
	 * update balance from Unicredit maximaly every 10 minutes
	 */
	public long getBalanceUpdatePeriad() {
		return config.getLong("unicredit.balanceUpdatePeriodMs");
	}

	public BigDecimal getUnicreditReserve() {
		return config.getBigDecimal("unicredit.accountReserve");
	}

	public String getPayerAccountCurrency() {
		return config.getString("unicredit.payerAccountCurrency");
	}
	
	public String getPayerAccountNumber() {
		return config.getString("unicredit.payerAccountNumber");
	}
	
	public String getPayerName() {
		return config.getString("unicredit.payerName");
	}	

	public String getPayerStreet() {
		return config.getString("unicredit.payerStreet");
	}	
	
	public String getPayerCity() {
		return config.getString("unicredit.payerCity");
	}	

	public String getPayerCountry() {
		return config.getString("unicredit.payerCountry");
	}	

	public String getPayerBankSwift() {
		return config.getString("unicredit.payerBankSwift");
	}	

	public String getPaymentStatisticalCode() {
		return config.getString("unicredit.paymentStatisticalCode");
	}	

	public String getPaymentDetailToCustomer() {
		return config.getString("unicredit.paymentDetailToCustomer");
	}	

	public String getPaymentDetailToBitstamp() {
		return config.getString("unicredit.paymentDetailToBitstamp");
	}	

	public BigDecimal getPaymentFee() {
		return config.getBigDecimal("unicredit.paymentFee");
	}
	
	public String getPaymentContact() {
		return config.getString("unicredit.paymentContact");
	}
	
	
}
