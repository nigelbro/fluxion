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

1. Create New Class ActionsCreator
2. Create Corresponding Actions interface
3. Extend FluxActionCreator
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
      emitAction(SOME_ACTION);
    }
    
     @Override
    someMethodDeclarationinActionsInterface(String data){
      emitAction(SOME_ACTION,Keys.SOME_KEY,data);
    }

}

```
Stores are responsible for all business logic and application state. This is the only place you will do work that needs to be either displayed by a View or store state that needs to be accessed by a View.

Creating Stores

1. Create New Class {Some}Store
2. Annotate Class with @Store annotation
3. Extend FluxStore
4. Create @Action annotated methods
5. emitReaction(String reactionId,Object... data)
6. Done

```java
//Emitting reactions to Views once some work has been completed and or application state changed
//This emitReaction() method will call all methods with @React annotation and match the correction reactionType
@Store
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
    
    @Action(actionType = SOME_ACTION_TYPE)
    public void someMethod(FluxAction action) {
	//Do some work or business logic update state variables or pass data to view to update itself
	emitReaction(SOME_REACTION_TYPE,Keys.SOME_KEY,action.get(Keys.SOME_KEY));
    }
}
```
##More Examples

```java
public class DummyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	@Inject ActionsCreator actionsCreator;
	private List<Object> mObjectList;

	public DummyAdapter(List<Object> list) {
		App.getInstance().geteComponent().inject(this);
		this.mObjectList = list;
		Flux.getInstance().registerReactionSubscriber(this)
	}
	
	public class DummyViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.horizontalList)RecyclerView recyclerView;

		public DummyViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
	
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new DummyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_query_item, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		final Object object = mObjectList.get(position);
	}

	@Override
	public int getItemCount() {
		return mAiObjectList.size();
	}

	@React(reactionType = ADD_ITEM)
	public void add(Reaction reaction){
		mObjectList.add(reaction.get(Keys.ITEM);
		notifyDataSetChanged();
	}
	
	@React(reactionType = UPDATE_ITEM)
	public void add(Reaction reaction){
		mObjectList.set(((Integer)reaction.get(Keys.POSITION)).intValue(), reaction.get(Keys.ITEM));
		notifyItemChanged(((Integer)reaction.get(Keys.POSITION)).intValue())
	}
	
	@React(reactionType = DELETE_ITEM)
	public void add(Reaction reaction){
		mObjectList.remove(((Integer)reaction.get(Keys.POSITION)).intValue());
		notifyItemRemoved(((Integer)reaction.get(Keys.POSITION)).intValue());
	}
}


```



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
