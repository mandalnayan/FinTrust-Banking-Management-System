package com.fintrust.model;

public class Nominee {
	private long nominee_id;
	private String nominee_name;
	private String nominee_relation;
	
	public Nominee(long nominee_id, String nominee_name, String nominee_relation) {
		super();
		this.nominee_id = nominee_id;
		this.nominee_name = nominee_name;
		this.nominee_relation = nominee_relation;
	}
	public Nominee() {
		// TODO Auto-generated constructor stub
	}
	public long getNominee_id() {
		return nominee_id;
	}
	public void setNominee_id(long nominee_id) {
		this.nominee_id = nominee_id;
	}
	public String getNominee_name() {
		return nominee_name;
	}
	public void setNominee_name(String nominee_name) {
		this.nominee_name = nominee_name;
	}
	public String getNominee_relation() {
		return nominee_relation;
	}
	public void setNominee_relation(String nominee_relation) {
		this.nominee_relation = nominee_relation;
	}
	@Override
	public String toString() {
		return "Nominee [nominee_id=" + nominee_id + ", nominee_name=" + nominee_name + ", nominee_relation="
				+ nominee_relation + "]";
	}
}
