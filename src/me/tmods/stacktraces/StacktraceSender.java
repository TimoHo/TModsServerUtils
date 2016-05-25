package me.tmods.stacktraces;

import java.util.Map;

import com.rollbar.Rollbar;
import com.rollbar.sender.RollbarResponse;
import com.rollbar.sender.RollbarResponseHandler;

public class StacktraceSender {
	private Rollbar r = new Rollbar("5ef810134c4b4fdcbba04785d82f91e5", "production");
	public StacktraceSender(String platform,String version,String application,Map<String,Object> custom) {
		r = r.codeVersion(version);
		r = r.custom(custom);
		r = r.platform(platform);
		r = r.framework(application);
		r = r.responseHandler(new RollbarResponseHandler() {
			@Override
			public void handleResponse(RollbarResponse response) {}
		});
	}
	public void log(Exception e) {
		r.log(e); e.printStackTrace();
	}
	public static void main(String[] args) {
		System.exit(0);
	}
	public Rollbar get() {
		return r;
	}
	public StacktraceSender set(Rollbar r) {
		this.r = r;
		return this;
	}
}
