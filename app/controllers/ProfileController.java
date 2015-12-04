package controllers;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import models.Profile;
import play.Logger;
import play.cache.CacheApi;
import play.data.Form;
import play.libs.Json;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import actions.GetProfilesAction;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProfileController extends Controller {

	@Inject
	CacheApi cache;
	
	private static final String PROFILES = "profiles";

	//@SuppressWarnings("unchecked")
	//@With(value = { GetProfilesAction.class })
	public Result register() {
		//List<Profile> profiles = (List<Profile>) ctx().args.get("profiles");
		return ok(views.html.createProfile.render("Register - Create User",
				getProfileList(), null));
	}

	public Result createProfile() {
		Form<Profile> profileForm = Form.form(Profile.class).bindFromRequest();
		if (profileForm.hasErrors() || profileForm.hasGlobalErrors()) {
			return ok(views.html.createProfile.render("Register - Create User",
					null, profileForm));
		}

		Profile profile = profileForm.get();
		profile.save();
		cache.remove(PROFILES);
		return redirect(routes.ProfileController.register());
	}

	public Result getProfiles() {
		List<Profile> profiles = getProfileList();
		JsonNode jsonData = Json.toJson(profiles);
		return ok(jsonData);
	}

	@SuppressWarnings("unchecked")
	private List<Profile> getProfileList() {
		Object profilesObject = cache.get(PROFILES); // checking in cache.
		List<Profile> profiles = (List<Profile>) profilesObject;
		if (null == profiles) {
			profiles = new Model.Finder<String, Profile>(Profile.class).all();
			Logger.info("Getting data from DB: "+ profiles.toString());
			cache.set(PROFILES, profiles);
		} else {
			Logger.info("Getting data from Cache: "+ profiles.toString());
		}
		return profiles;
	}

	public Result getProfile(String id) {
		JsonNode jsonData = Json.toJson(cache.getOrElse(id, new ProfileCallable(id)));
		return ok(jsonData);
	}

	public Result deleteProfile(String id) {
		Profile profile = cache.getOrElse(id, new ProfileCallable(id));
		profile.delete();

		cache.remove(id);
		cache.remove(PROFILES);
		ObjectNode result = Json.newObject();
		result.put("status", "Success");
		result.put("message", "Deleted Profile Id: " + id);
		return ok(result);
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Result updateProfile() {
		JsonNode profileData = request().body().asJson();
		ObjectMapper mapper = new ObjectMapper();
		Profile profile = mapper.convertValue(profileData, Profile.class);
		profile.update();

		cache.remove(profile.id);
		cache.remove(PROFILES);
		ObjectNode result = Json.newObject();
		result.put("status", "Success");
		result.put("message", "Update Profile Id: " + profile.id);
		return ok(result);
	}

	public Result getProfileForEdit(String id) {
		Profile profile = cache.getOrElse(id, new ProfileCallable(id));
		Form<Profile> profileForm = Form.form(Profile.class).fill(profile);
		return ok(views.html.updateProfile.render(id, profileForm));
	}

	public Result editProfile(String id) {
		Form<Profile> profileForm = Form.form(Profile.class);
		Profile profile = profileForm.bindFromRequest().get();
		
		profile.update();

		cache.remove(id);
		cache.remove(PROFILES);
		return redirect(routes.ProfileController.register());
	}
	
	public class ProfileCallable implements Callable<Profile> {
		
		String id;
		
		public ProfileCallable(String id) {
			this.id = id;
		}

		public Profile call() throws Exception {
			Logger.info("Getting Profile Data from DB: " + id);
			Profile profile = new Model.Finder<String, Profile>(Profile.class)
					.byId(id);
			return profile;
		}
		
	}

}
