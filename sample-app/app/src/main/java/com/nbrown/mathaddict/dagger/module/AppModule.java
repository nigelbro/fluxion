package com.nbrown.mathaddict.dagger.module;

import android.content.Context;

import com.nbrown.mathaddict.App;
import com.nigelbrown.fluxion.Flux;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by Nigel.Brown on 3/3/2017.
 */
@Module
@Singleton
public class AppModule {
	private final App mApplication;

	public AppModule(App application) {
		this.mApplication = application;
	}

	@Provides
	@Singleton
	public App provideApp() {
		return mApplication;
	}

	@Provides
	@Singleton
	public Context provideApplicationContext() {
		return this.mApplication;
	}

	@Singleton
	@Provides
	public Flux providesRxFLux(App app) {return Flux.init(app);}

}
