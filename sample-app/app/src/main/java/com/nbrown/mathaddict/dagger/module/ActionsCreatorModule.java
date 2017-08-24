package com.nbrown.mathaddict.dagger.module;

import com.nbrown.mathaddict.action.ActionCreator;
import com.nigelbrown.fluxion.Flux;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nigel Brown on 4/24/2017.
 */
@Module
public class ActionsCreatorModule {
	@Singleton
	@Provides
	public ActionCreator providesActionCreator(Flux flux) {
		return new ActionCreator(flux);
	}

}
