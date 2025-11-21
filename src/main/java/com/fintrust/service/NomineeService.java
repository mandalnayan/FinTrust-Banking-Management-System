package com.fintrust.service;

import com.fintrust.model_copy.Nominee;

public interface NomineeService {
	boolean saveNominee(Nominee nominee);
	boolean isPresentNominee(long nomineeId);
}
