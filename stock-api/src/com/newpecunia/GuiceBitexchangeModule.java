package com.newpecunia;

import com.google.inject.AbstractModule;
import com.newpecunia.bitstamp.BitstampRequestCountLimitVerifier;
import com.newpecunia.bitstamp.service.BitstampService;
import com.newpecunia.bitstamp.service.impl.BitstampCredentials;
import com.newpecunia.bitstamp.service.impl.BitstampServiceImpl;
import com.newpecunia.bitstamp.service.impl.SecureBitstampCredentialsImpl;
import com.newpecunia.bitstamp.webdriver.BitstampWebdriver;
import com.newpecunia.bitstamp.webdriver.impl.BitstampWebdriverImpl;
import com.newpecunia.net.HttpReaderFactory;
import com.newpecunia.net.HttpReaderFactoryImpl;
import com.newpecunia.net.RequestCountLimitVerifier;
import com.newpecunia.synchronization.ClusterLockProvider;
import com.newpecunia.synchronization.SingleNodeClusterLockProvider;
import com.newpecunia.util.TimeProvider;
import com.newpecunia.util.TimeProviderImpl;

public class GuiceBitexchangeModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HttpReaderFactory.class).to(HttpReaderFactoryImpl.class);
		bind(ClusterLockProvider.class).to(SingleNodeClusterLockProvider.class);
		
		bind(BitstampCredentials.class).toInstance(SecureBitstampCredentialsImpl.newInstance());
		bind(TimeProvider.class).to(TimeProviderImpl.class);
		bind(RequestCountLimitVerifier.class).to(BitstampRequestCountLimitVerifier.class);
		bind(BitstampService.class).to(BitstampServiceImpl.class);
		bind(BitstampWebdriver.class).to(BitstampWebdriverImpl.class);
		
	}

}