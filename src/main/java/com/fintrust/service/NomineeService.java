package com.fintrust.service;
import models.Nominee;

public interface NomineeService {
	boolean saveNominee(Nominee nominee);
	boolean isPresentNominee(long nomineeId);
}
