package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity	
@UniqueConstraint(columnNames = { "login",  "email_address"})
public class Profile extends Model {
	
	@Id
	public String id;
	
	public String firstName;
	
	public String lastName;
	
	@Required
	public String login;
	
	public String password;
	
	@Required
	public String emailAddress;
	
}
