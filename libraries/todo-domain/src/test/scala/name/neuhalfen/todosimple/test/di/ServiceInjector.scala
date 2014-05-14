package name.neuhalfen.todosimple.test.di

import com.google.inject.Guice


// =======================
// Usage: val bean = new Bean with ServiceInjector
trait ServiceInjector {
  ServiceInjector.inject(this)
}

// helper companion object
object ServiceInjector {
  val injector = Guice.createInjector(new TestDependencyModule)

  def inject(obj: AnyRef) = injector.injectMembers(obj)

  def getInstance[T](clazz: Class[T]): T = injector.getInstance(clazz)
}


