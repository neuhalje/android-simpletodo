Make Unit tests work for android
==================================

This project contains [RoboSpock](https://github.com/Polidea/RoboSpock) ([Spock](http://code.google.com/p/spock/) + [Robolectric](http://robolectric.org/)) tests.

Android projects can only include JUnit 3 integration tests that run on a device. This is clearly not good for testing.

IntelliJ caveats
==================

It seems that Robolectric, Scala and IntelliJ don't mix all to nice. Running unit tests with Robolectric from the IDE always throws (see below). After fiddling the better of a whole day to get it working I finally give up.

Solution:

* run the gradle task `unitTest:check` from the gradle tasks menu.
* look for errors in the [html report(s)](./build/test-report/debug/index.html)

The exception when running in the IDE is

```text
at java.net.URLClassLoader.access$100(URLClassLoader.java:71)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:361)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:355)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:354)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:425)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:308)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:358)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:800)
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142)
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:449)
	at java.net.URLClassLoader.access$100(URLClassLoader.java:71)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:361)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:355)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:354)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:425)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:308)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:358)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:800)
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142)
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:449)
	at java.net.URLClassLoader.access$100(URLClassLoader.java:71)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:361)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:355)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:354)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:425)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:308)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:358)
	at org.robolectric.bytecode.AsmInstrumentingClassLoader$ClassInstrumentor.redirectorMethod(AsmInstrumentingClassLoader.java:602)
	at org.robolectric.bytecode.AsmInstrumentingClassLoader$ClassInstrumentor.instrumentConstructor(AsmInstrumentingClassLoader.java:519)
	at org.robolectric.bytecode.AsmInstrumentingClassLoader$ClassInstrumentor.instrument(AsmInstrumentingClassLoader.java:410)
	at org.robolectric.bytecode.AsmInstrumentingClassLoader.getInstrumentedBytes(AsmInstrumentingClassLoader.java:233)
	at org.robolectric.bytecode.AsmInstrumentingClassLoader.findClass(AsmInstrumentingClassLoader.java:147)
	at org.robolectric.bytecode.AsmInstrumentingClassLoader.loadClass(AsmInstrumentingClassLoader.java:95)
	at java.lang.Class.privateGetDeclaredConstructors(Class.java:2493)
	at java.lang.Class.getConstructor0(Class.java:2803)
	at java.lang.Class.newInstance(Class.java:345)
	at pl.polidea.robospock.internal.RoboSpockInterceptor.getHooksInterface(RoboSpockInterceptor.java:159)
	at pl.polidea.robospock.internal.RoboSpockInterceptor.interceptSpecExecution(RoboSpockInterceptor.java:57)
	at org.spockframework.runtime.extension.AbstractMethodInterceptor.intercept(AbstractMethodInterceptor.java:52)
	at org.spockframework.runtime.extension.MethodInvocation.proceed(MethodInvocation.java:84)
	at pl.polidea.robospock.internal.RoboSputnik.run(RoboSputnik.java:260)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:157)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)
```

