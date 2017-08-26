package com.nigelbrown.fluxion;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Nigel.Brown on 8/21/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationHelperTest {
	@Test
	public void callMethodsWithReactAnnotation() throws Exception {
		Set<Method> methods = AnnotationHelper.getMethodsAnnotatedWithReact();
		Assert.assertEquals(methods.size(),1);
	}

	@Test
	public void callMethodsWithActionAnnotation() throws Exception {
		Set<Method> methods = AnnotationHelper.getMethodsAnnotatedWithAction();
		Assert.assertEquals(methods.size(),0);
	}
}