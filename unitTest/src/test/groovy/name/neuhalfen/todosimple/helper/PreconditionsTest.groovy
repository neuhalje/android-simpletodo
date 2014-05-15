/*
Copyright 2014 Jens Neuhalfen

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
 */
package name.neuhalfen.todosimple.helper

import pl.polidea.robospock.RoboSpecification

import static name.neuhalfen.todosimple.helper.Preconditions.checkArgument

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
