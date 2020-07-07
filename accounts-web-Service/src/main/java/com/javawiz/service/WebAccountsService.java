package com.javawiz.service;

import com.javawiz.config.ConfigProperties;
import com.javawiz.exception.AccountNotFoundException;
import com.javawiz.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Hide the access to the microservice inside this local service.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WebAccountsService {

	private final RestTemplate restTemplate;
	private final ConfigProperties configProperties;

	public Account findByNumber(String accountNumber) {
		log.info("findByNumber() invoked: for " + accountNumber);
		return restTemplate.getForObject(getServiceUrl() + "/accounts/{number}",
				Account.class, accountNumber);
	}

	public List<Account> byOwnerContains(String name) {
		log.info("byOwnerContains() invoked:  for " + name);
		Account[] accounts = null;

		try {
			accounts = restTemplate.getForObject(getServiceUrl()
					+ "/accounts/owner/{name}", Account[].class, name);
		} catch (HttpClientErrorException e) { // 404
			// Nothing found
		}

		if (accounts == null || accounts.length == 0)
			return null;
		else
			return Arrays.asList(accounts);
	}

	public Account getByNumber(String accountNumber) {
		Account account = restTemplate.getForObject(getServiceUrl()
				+ "/accounts/{number}", Account.class, accountNumber);

		if (account == null)
			throw new AccountNotFoundException(accountNumber);
		else
			return account;
	}

	private String getServiceUrl(){
		String serviceUrl = configProperties.getApiGatewayUrl() + configProperties.getAccountServiceId();
		log.info("ServiceUrl: {} " + serviceUrl);
		return serviceUrl;
	}
}
