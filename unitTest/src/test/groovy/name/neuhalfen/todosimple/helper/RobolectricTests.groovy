package name.neuhalfen.todosimple.helper

import android.widget.TextView
import org.robolectric.Robolectric
import pl.polidea.robospock.RoboSpecification

/**
 * Test that Robolectric works
 */
class RobolectricTests  extends RoboSpecification {

    def "Robolectric works"() {
        given:
        def textView = new TextView(Robolectric.application)

        and:
        def hello = "Hello"

        when:
        textView.setText(hello)

        then:
        textView.getText() == hello
    }
}
