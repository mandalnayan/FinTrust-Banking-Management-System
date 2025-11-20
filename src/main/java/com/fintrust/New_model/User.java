package com.fintrust.New_model;

public class User {
	package com.fintrust.model;

	import java.math.BigDecimal;
	import java.time.LocalDateTime;

	public class Account {
	    private Long accountId;
	    private Long userId;
	    private String accountNumber;
	    private String accountType;   // savings/current/salary/fixed_deposit
	    private BigDecimal balance;
	    private String currency;
	    private String status;
	    private LocalDateTime openedAt;
	    private LocalDateTime updatedAt;

	    // Getters & Setters
	    // ...
	}

}
