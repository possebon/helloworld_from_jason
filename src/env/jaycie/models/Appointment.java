package jaycie.models;


import java.util.Date;


public class Appointment {
	private Professional professional;
	private Service service;
	private Customer customer;
	private Date appointment_date;
	/**
	 * @return the professional
	 */
	public Professional getProfessional() {
		return professional;
	}
	/**
	 * @param professional the professional to set
	 */
	public void setProfessional(Professional professional) {
		this.professional = professional;
	}
	/**
	 * @return the service
	 */
	public Service getService() {
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(Service service) {
		this.service = service;
	}
	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}
	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	/**
	 * @return the appointment_date
	 */
	public Date getAppointment_date() {
		return appointment_date;
	}
	/**
	 * @param appointment_date the appointment_date to set
	 */
	public void setAppointment_date(Date appointment_date) {
		this.appointment_date = appointment_date;
	}

}
