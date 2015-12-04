package actions;

import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class GetProfileAction extends Action.Simple {

	@Override
	public Promise<Result> call(Context arg0) throws Throwable {
		Logger.info("I am getting profile..." + arg0.request());
		return delegate.call(arg0);
	}
	

}
