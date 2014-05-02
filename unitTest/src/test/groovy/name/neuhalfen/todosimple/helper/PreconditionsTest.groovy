package name.neuhalfen.todosimple.helper

import pl.polidea.robospock.RoboSpecification

import static name.neuhalfen.todosimple.helper.Preconditions.*

/**
 * This is really just a vehicle to test the Junit test runner in IDEA without Robolectric
 * <p/>
 * If you get
 * java.lang.IncompatibleClassChangeError: class org.objectweb.asm.commons.LocalVariablesSorter has interface org.objectweb.asm.MethodVisitor as super class
 * when running from IDEA, try to run this test first.
 */
class PreconditionsSpec extends RoboSpecification {

    def "should fail on error"() {
        given:
        def x = false

        when:
        checkArgument(x);

        then:
        thrown IllegalArgumentException
    }
}
