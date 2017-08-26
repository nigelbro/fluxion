package com.nbrown.mathaddict.dagger.module;

import com.nbrown.mathaddict.store.MathStore;
import com.nigelbrown.fluxion.Flux;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class StoreModule {
	@Provides
	@Singleton
	MathStore provideMathStore(Flux flux) {
		return new MathStore(flux);
	}

}
