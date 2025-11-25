package com.fintrust.service;

import com.fintrust.model.Nominee;

public interface NomineeService {
	Long saveNominee(Nominee nominee);
	boolean isPresentNominee(long nomineeId);
}
