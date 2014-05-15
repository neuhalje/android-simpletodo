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
package name.neuhalfen.todosimple.android.mft

import android.os.Parcelable
import com.google.gson.Gson
import name.neuhalfen.todosimple.android.view.base.GsonParcer
import pl.polidea.robospock.RoboSpecification

class GsonParcerTest
        extends RoboSpecification {

    static class RWFields {
        protected String fieldA;
        protected String fieldB;

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            RWFields rwFields = (RWFields) o

            if (fieldA != rwFields.fieldA) return false
            if (fieldB != rwFields.fieldB) return false

            return true
        }

        int hashCode() {
            int result
            result = (fieldA != null ? fieldA.hashCode() : 0)
            result = 31 * result + (fieldB != null ? fieldB.hashCode() : 0)
            return result
        }
    }

    def "serializing and deserializing an object with default constructor returns an equal instance"() {
        given:
        GsonParcer<RWFields> sut = new GsonParcer<RWFields>(new Gson())

        RWFields original = new RWFields()
        original.fieldA = "A"
        original.fieldB = "B"


        when:
        Parcelable parced = sut.wrap(original)
        RWFields deserialized = sut.unwrap(parced)

        then:
        original.equals(deserialized)
    }

    static class FinalFields {
        protected final String fieldA;
        protected final String fieldB;

        public FinalFields(String a, String b) {
            this.fieldA = a
            this.fieldB = b
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            FinalFields finalFields = (FinalFields) o

            if (fieldA != finalFields.fieldA) return false
            if (fieldB != finalFields.fieldB) return false

            return true
        }

        int hashCode() {
            int result
            result = (fieldA != null ? fieldA.hashCode() : 0)
            result = 31 * result + (fieldB != null ? fieldB.hashCode() : 0)
            return result
        }
    }

    def "serializing and deserializing an object without default constructor returns an equal instance"() {
        given:
        GsonParcer<FinalFields> sut = new GsonParcer<FinalFields>(new Gson())

        Object original = new FinalFields("A", "B")

        when:
        Parcelable parced = sut.wrap(original)
        Object deserialized = sut.unwrap(parced)

        then:
        original.equals(deserialized)
    }
}
