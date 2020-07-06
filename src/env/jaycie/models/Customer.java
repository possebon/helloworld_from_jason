package jaycie.models;

public class Customer {
	private String name;
	private String lastname;
	private String fullName;
	
	
	 // Getter Methods 

	 public String getName() {
	  return name;
	 }

	 public String getLastname() {
	  return lastname;
	 }

	 // Setter Methods 

	 public void setName(String name) {
	  this.name = name;
	 }

	 public void setLastname(String lastname) {
		  this.lastname = lastname;
	 }	 
	 
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	

}
