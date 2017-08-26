package com.nbrown.mathaddict.dagger.component;

import com.nbrown.mathaddict.MainActivity;
import com.nbrown.mathaddict.dagger.module.ActionsCreatorModule;
import com.nbrown.mathaddict.dagger.module.ApplicationModule;
import com.nbrown.mathaddict.dagger.module.StoreModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Nigel.Brown on 3/3/2017.
 */


@Singleton
@Component(modules = {ApplicationModule.class, StoreModule.class, ActionsCreatorModule.class})
public interface AppComponent {
	void inject(MainActivity mainActivity);
}
