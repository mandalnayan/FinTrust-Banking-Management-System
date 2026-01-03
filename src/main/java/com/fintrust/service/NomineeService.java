package com.fintrust.service;

import com.fintrust.model.Nominee;

public interface NomineeService {
	Long saveNominee(Nominee nominee);
	Long isPresentNominee(long nomineeId);
}
