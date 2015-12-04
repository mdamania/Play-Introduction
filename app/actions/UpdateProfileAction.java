package actions;

import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class UpdateProfileAction extends Action<Object>{

	@Override
	public Promise<Result> call(Context arg0) throws Throwable {
		Logger.info("I am Updating Profile.....");
		return delegate.call(arg0);
	}

}
