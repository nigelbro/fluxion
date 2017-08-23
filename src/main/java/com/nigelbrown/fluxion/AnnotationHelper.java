package com.nigelbrown.fluxion;

import com.nigelbrown.fluxion.Annotation.Action;
import com.nigelbrown.fluxion.Annotation.React;
import com.nigelbrown.fluxion.Annotation.Store;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nigel.Brown on 8/21/2017.
 */
public class AnnotationHelper {

	public AnnotationHelper(){}

	static void callMethodsWithReactAnnotation(Reaction reaction) throws IllegalAccessException,InvocationTargetException {
		Set<Method> reactMethods = getMethodsAnnotatedWithReact();
		for(Method method :reactMethods ){
			Class[] paramTypes = method.getParameterTypes();
			if(paramTypes.length == 1 && paramTypes[0].equals(Reaction.class) && reaction.getType().equals(method.getAnnotation(React.class).reactionType())){
				method.invoke(method.getDeclaringClass(),reaction);
			}
		}
	}

	static void callMethodsWithActionAnnotation(FluxAction fluxAction) throws IllegalAccessException,InvocationTargetException {
		Set<Method> actionMethods = getMethodsAnnotatedWithAction();
		for(Method method : actionMethods){
			Class[] paramTypes = method.getParameterTypes();
			if(paramTypes.length == 1 && paramTypes[0].equals(FluxAction.class) && fluxAction.getType().equals(method.getAnnotation(Action.class).actionType())){
				method.invoke(method.getDeclaringClass(), fluxAction);
			}
		}
	}

	static Set<Method> getMethodsAnnotatedWithReact(){
		Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()).setScanners(
				new MethodAnnotationsScanner()));
		return reflections.getMethodsAnnotatedWith(React.class);
	}

	static Set<Method> getMethodsAnnotatedWithAction(){
		Set<Class<?>> classes = getClassesAnnotatedWithStore();
		Set<Method> actionMethods = new HashSet<>();
		for(Class<?> parentClass : classes){
			Method[] methods = parentClass.getMethods();
			for(Method method : methods){
				Annotation actionAnnotations = method.getAnnotation(Action.class);
				if(actionAnnotations == null){
					continue;
				}else {
					actionMethods.add(method);
				}
			}
		}
		return actionMethods;
	}

	static Set<Class<?>> getClassesAnnotatedWithStore() {

		Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()).setScanners(new SubTypesScanner(),new TypeAnnotationsScanner()));
		return reflections.getTypesAnnotatedWith(Store.class);
	}
}
