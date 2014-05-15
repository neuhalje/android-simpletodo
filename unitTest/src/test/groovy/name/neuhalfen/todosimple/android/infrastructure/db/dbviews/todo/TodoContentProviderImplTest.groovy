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
package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo

import android.content.UriMatcher
import android.net.Uri
import pl.polidea.robospock.RoboSpecification

import static name.neuhalfen.todosimple.helper.TestConstants.taskId1

class TodoContentProviderImplTest
        extends RoboSpecification {

    def "the UriMatcher correctly identifies URIs with the aggregate UUID as id"() {
        given:
        UriMatcher sut = TodoContentProviderImpl.sURIMatcher;

        when:
        Uri aggregateIdURI = TodoContentProvider.Factory.forAggregateId(taskId1);

        then:
        sut.match(aggregateIdURI) == TodoContentProviderImpl.TODO_AGGREGATE_ID
    }

    def "the UriMatcher correctly identifies URIs with the database pk (_id:long) as id"() {
        given:
        UriMatcher sut = TodoContentProviderImpl.sURIMatcher;

        when:
        Uri aggregateIdURI = TodoContentProvider.Factory.forContenProvider_Id(666);

        then:
        sut.match(aggregateIdURI) == TodoContentProviderImpl.TODO_ID
    }

}
