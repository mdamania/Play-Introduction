package actions;

import java.util.List;

import models.Profile;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

import com.avaje.ebean.Model;

public class GetProfilesAction extends Action<List<Profile>>{

	@Override
	public Promise<Result> call(Context context) throws Throwable {
		List<Profile> profiles = new Model.Finder<String, Profile>(Profile.class).all();
		context.args.put("profiles", profiles);
		return delegate.call(context);
	}

}
