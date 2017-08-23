Fluxion
============


Library to simplfy integrating the Flux Architecture in Android.
This architecture system is designed to allow for linear data flow, immutability, control of traffic flow etc.

 * Eliminate creating custom Events or Actions for Eventbus.


```java
class ExampleActivity extends Activity {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    ButterKnife.bind(this);
    // TODO Use fields...
  }
  @React
  public void doSomeViewThing(Reaction reaction){
  
  }
}
```

Download
--------
Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Add the dependency:
```gradle
dependencies {
	compile 'com.github.nigelbro:fluxion:v2.1'
}
```

More examples
-----------

Creating ActionCreators

Actions Creators are Synchronous which means you should not be doing any work. It is here to simply pass actions to the subscription manager to emit to onRxAction in the Stores.


1. Create New Class ActionsCreator
2. Create Corresponding Actions interface
3. Extend RxActionCreator
4. implement Actions interface
5. Override methods in Actions interface
6. call FluxActionCreator  emitAction(String actionId, Object... data) in Actions Interface method definition.
7. done



```java

class ActionCreator extends FluxActionCreator implements Actions {

    @Inject
    public ActionCreator(Flux flux) {
	super(flux);
    }

    @Override
    someMethodDeclarationinActionsInterface(){
      emitAction(String actionId,Object... data);
    }

}

```
Stores are responsible for all business logic and application state. This is the only place you will do worked that needs to be either displayed by a View or store state that needs to be accessed by a View.

Creating Stores

1. Create New Class {Some}Store
2. Extend FluxStore
3. Create @Action annotated methods
4. emitReaction(String reactionId,Object... data)
5. Done

```java
//Emitting reactions to Views once some work has been completed and or application state changed
//This emitReaction() method will call all methods with @React annotation and match the correction reactionType
class AppStore extends FluxStore {

    @Inject
    public MathStore(Flux flux) {
	super(flux);
	registerActionSubscriber(MathStore.this);
    }
    
    @Action(actionType = SOME_ACTION_TYPE)
    public void someMethod(FluxAction action) {
	//Do some work or business logic update state variables
	emitReaction(SOME_REACTION_TYPE);
    }
}

## Bugs and Feedback

For bugs, feature requests, and discussion please use [GitHub Issues][issues].

License

    Copyright 2017 Nigel Brown

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 [1]: http://square.github.com/dagger/
 [2]: https://github.com/koush/ion
 [issues]: https://github.com/nigelbro/fluxion/issues
