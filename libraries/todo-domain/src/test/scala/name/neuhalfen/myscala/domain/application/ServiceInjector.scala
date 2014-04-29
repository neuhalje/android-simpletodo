package name.neuhalfen.myscala.domain.application

import com.google.inject.Guice


// =======================
// Usage: val bean = new Bean with ServiceInjector
trait ServiceInjector {
  ServiceInjector.inject(this)
}

// helper companion object
object ServiceInjector {
  private val injector = Guice.createInjector(new DependencyModule)

  def inject(obj: AnyRef) = injector.injectMembers(obj)
}


