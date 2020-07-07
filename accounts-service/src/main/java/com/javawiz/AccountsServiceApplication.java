package com.javawiz;

import com.javawiz.entity.Account;
import com.javawiz.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableEurekaClient
@PropertySource("classpath:db-config.properties")
public class AccountsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(AccountRepository accountRepository){
		return args -> {
			List<Account> accounts = accountRepository.findAll();
			// Populate with random balances
			Random rand = new Random();
			for (Account account : accounts) {
				BigDecimal balance = new BigDecimal(rand.nextInt(10000000) / 100.0).setScale(2, RoundingMode.HALF_UP);
				account.setBalance(balance);
			}
			accountRepository.saveAll(accounts);
		};
	}
}
