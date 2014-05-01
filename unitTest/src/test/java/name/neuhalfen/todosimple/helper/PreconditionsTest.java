package name.neuhalfen.todosimple.helper;

import org.junit.Test;

import static name.neuhalfen.todosimple.helper.Preconditions.checkArgument;

/**
 * This is really just a vehicle to test the Junit test runner in IDEA without Robolectric
 * <p/>
 * If you get
 * java.lang.IncompatibleClassChangeError: class org.objectweb.asm.commons.LocalVariablesSorter has interface org.objectweb.asm.MethodVisitor as super class
 * when running from IDEA, try to run this test first.
 */
public class PreconditionsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCheckArgument() throws Exception {
        checkArgument(false);

    }
}