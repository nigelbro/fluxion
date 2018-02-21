package com.nbrown.mathaddict;

import com.nbrown.mathaddict.dagger.component.AppComponent;
import com.nbrown.mathaddict.dagger.component.DaggerAppComponent;
import com.nbrown.mathaddict.dagger.module.ActionsCreatorModule;
import com.nbrown.mathaddict.dagger.module.ApplicationModule;
import com.nbrown.mathaddict.dagger.module.StoreModule;

/**
 * Created by nigel.brown on 4/25/2016.
 */
public class App extends android.app.Application {
	public static final String TAG = App.class.getSimpleName();
	private static App mInstance;
	private AppComponent component;
	public static synchronized App getInstance() {
		return mInstance;
	}
	@Override
	public void onCreate() throws SecurityException {
		super.onCreate();
		mInstance = this;
		component = DaggerAppComponent.builder().applicationModule(new ApplicationModule(this)).actionsCreatorModule(new ActionsCreatorModule()).storeModule(new StoreModule()).build();

	}
	public AppComponent getComponent() {
		return component;
	}
}
