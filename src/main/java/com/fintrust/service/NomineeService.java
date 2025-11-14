package com.fintrust.service;

import com.fintrust.model.Nominee;

public interface NomineeService {
	boolean saveNominee(Nominee nominee);
	boolean isPresentNominee(long nomineeId);
}
